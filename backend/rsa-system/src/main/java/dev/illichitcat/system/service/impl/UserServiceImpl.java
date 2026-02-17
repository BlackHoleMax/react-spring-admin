package dev.illichitcat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.illichitcat.common.exception.BizException;
import dev.illichitcat.common.utils.SecurePasswordGenerator;
import dev.illichitcat.system.dao.mapper.UserMapper;
import dev.illichitcat.system.dao.mapper.UserRoleMapper;
import dev.illichitcat.system.model.dto.UserExcelDTO;
import dev.illichitcat.system.model.entity.File;
import dev.illichitcat.system.model.entity.Role;
import dev.illichitcat.system.model.entity.User;
import dev.illichitcat.system.model.entity.UserRole;
import dev.illichitcat.system.service.FileService;
import dev.illichitcat.system.service.RoleService;
import dev.illichitcat.system.service.UserRoleService;
import dev.illichitcat.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private FileService fileService;

    @Override
    public IPage<User> selectUserList(IPage<User> page, User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (user != null) {
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                queryWrapper.like("username", user.getUsername());
            }
            if (user.getNickname() != null && !user.getNickname().isEmpty()) {
                queryWrapper.like("nickname", user.getNickname());
            }
            if (user.getStatus() != null) {
                queryWrapper.eq("status", user.getStatus());
            }
        }
        queryWrapper.orderByAsc("id");
        return this.page(page, queryWrapper);
    }

    @Override
    public User selectUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    @Override
    public User selectUserById(Long userId) {
        return this.getById(userId);
    }

    @Override
    public boolean insertUser(User user) {
        log.info("新增用户开始, username={}, nickname={}, email={}",
                user.getUsername(), user.getNickname(), user.getEmail());

        // 如果没有密码，自动生成默认密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            String defaultPassword = "123456";
            String encodedPassword = passwordEncoder.encode(defaultPassword);
            user.setPassword(encodedPassword);
            log.info("为用户生成默认密码, username={}", user.getUsername());
        }

        boolean result = this.save(user);

        // 清除总用户数缓存
        clearTotalUserCountCache();

        log.info("新增用户结束, username={}, result={}", user.getUsername(), result);
        return result;
    }

    @Override
    public boolean updateUser(User user) {
        log.info("更新用户开始, id={}, username={}", user.getId(), user.getUsername());
        boolean result = this.updateById(user);
        log.info("更新用户结束, id={}, result={}", user.getId(), result);
        return result;
    }

    @Override
    public boolean deleteUserById(Long userId) {
        log.info("删除用户开始, userId={}", userId);

        // 检查用户是否有关联数据
        List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", userId));
        if (!userRoles.isEmpty()) {
            log.warn("删除用户失败，用户有关联的角色数据, userId={}", userId);
            throw new BizException("该用户已分配角色，无法删除");
        }

        boolean result = this.removeById(userId);

        // 清除总用户数缓存
        clearTotalUserCountCache();

        log.info("删除用户结束, userId={}, result={}", userId, result);
        return result;
    }

    @Override
    public boolean deleteUsersByIds(Long[] userIds) {
        log.info("批量删除用户开始, userIds={}, count={}", Arrays.toString(userIds), userIds.length);

        // 检查用户是否有关联数据
        for (Long userId : userIds) {
            List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", userId));
            if (!userRoles.isEmpty()) {
                User user = this.getById(userId);
                String username = user != null ? user.getUsername() : String.valueOf(userId);
                log.warn("批量删除用户失败，用户有关联的角色数据, userId={}, username={}", userId, username);
                throw new BizException("用户 " + username + " 已分配角色，无法删除");
            }
        }

        List<Long> ids = new ArrayList<>(Arrays.asList(userIds));
        boolean result = this.removeBatchByIds(ids);

        // 清除总用户数缓存
        clearTotalUserCountCache();

        log.info("批量删除用户结束, count={}, result={}", userIds.length, result);
        return result;
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        log.info("更新用户状态开始, userId={}, status={}", userId, status);
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        boolean result = this.updateById(user);
        log.info("更新用户状态结束, userId={}, status={}, result={}", userId, status, result);
        return result;
    }

    @Override
    public String resetPassword(Long userId) {
        log.info("重置用户密码开始, userId={}", userId);

        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        SecurePasswordGenerator passwordGenerator = new SecurePasswordGenerator();
        // 生成8位随机密码，包含大小写字母、数字和特殊字符
        String newPassword = passwordGenerator.generate(8, true, true, true);

        // 对密码进行BCrypt加密
        String encodedPassword = passwordEncoder.encode(newPassword);

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(encodedPassword);

        boolean result = this.updateById(updateUser);
        log.info("重置用户密码结束, userId={}, result={}", userId, result);

        if (!result) {
            throw new RuntimeException("重置密码失败");
        }

        return newPassword;
    }

    @Override
    public boolean changePassword(Long userId, String newPassword) {
        log.info("修改用户密码开始, userId={}", userId);

        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 对密码进行BCrypt加密
        String encodedPassword = passwordEncoder.encode(newPassword);

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(encodedPassword);

        boolean result = this.updateById(updateUser);
        log.info("修改用户密码结束, userId={}, result={}", userId, result);

        if (result) {
            // 清除用户的token和权限缓存，使其当前登录失效
            try {
                redisTemplate.delete("user_token:" + userId);
                redisTemplate.delete("user_perms:" + userId);
                log.info("已清除用户token和权限缓存, userId={}", userId);
            } catch (Exception e) {
                log.warn("清除用户token和权限缓存失败, userId={}, error={}", userId, e.getMessage());
            }
        }

        return result;
    }

    @Override
    public List<UserExcelDTO> exportUsers(List<Long> userIds) {
        log.info("导出用户数据开始, userIds={}", userIds);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userIds != null && !userIds.isEmpty()) {
            queryWrapper.in("id", userIds);
        }
        queryWrapper.orderByAsc("id");

        List<User> userList = this.list(queryWrapper);
        List<UserExcelDTO> excelDTOList = new ArrayList<>();

        for (User user : userList) {
            UserExcelDTO dto = new UserExcelDTO();
            BeanUtils.copyProperties(user, dto);

            // 获取用户角色编码
            List<Role> roles = roleService.selectRolesByUserId(user.getId());
            String roleCodes = roles.stream()
                    .map(Role::getCode)
                    .collect(Collectors.joining(","));
            dto.setRoleCodes(roleCodes);

            // 导出时不包含密码
            dto.setPassword(null);

            excelDTOList.add(dto);
        }

        log.info("导出用户数据结束, count={}", excelDTOList.size());
        return excelDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importUsers(List<UserExcelDTO> userExcelDTOList) {
        log.info("导入用户数据开始, count={}", userExcelDTOList.size());

        ImportResult result = new ImportResult();

        for (int i = 0; i < userExcelDTOList.size(); i++) {
            UserExcelDTO excelDTO = userExcelDTOList.get(i);
            int rowNum = i + 2;

            try {
                processUserImport(excelDTO, rowNum, result);
            } catch (Exception e) {
                handleImportException(excelDTO, rowNum, e, result);
            }
        }

        log.info("导入用户数据结束, success={}, fail={}", result.successCount, result.failCount);
        return buildImportResult(result);
    }

    /**
     * 处理单个用户导入
     */
    private void processUserImport(UserExcelDTO excelDTO, int rowNum, ImportResult result) {
        // 验证必填字段
        if (!validateUsername(excelDTO, rowNum, result)) {
            return;
        }

        // 检查用户名是否已存在
        if (!checkUsernameExists(excelDTO, rowNum, result)) {
            return;
        }

        // 检查邮箱是否已存在
        if (!checkEmailExists(excelDTO, rowNum, result)) {
            return;
        }

        // 创建并保存用户
        User user = createUserFromExcelDTO(excelDTO);
        if (this.save(user)) {
            processRoleAssignment(user, excelDTO);
            result.successCount++;
            log.info("成功导入用户, username={}, id={}", user.getUsername(), user.getId());
        } else {
            result.failMessages.append(String.format("第%d行：保存用户失败；", rowNum));
            result.failCount++;
        }
    }

    /**
     * 验证用户名
     */
    private boolean validateUsername(UserExcelDTO excelDTO, int rowNum, ImportResult result) {
        if (excelDTO.getUsername() == null || excelDTO.getUsername().trim().isEmpty()) {
            result.failMessages.append(String.format("第%d行：登录账号不能为空；", rowNum));
            result.failCount++;
            return false;
        }
        return true;
    }

    /**
     * 检查用户名是否已存在
     */
    private boolean checkUsernameExists(UserExcelDTO excelDTO, int rowNum, ImportResult result) {
        User existUser = selectUserByUsername(excelDTO.getUsername());
        if (existUser != null) {
            result.failMessages.append(String.format("第%d行：登录账号%s已存在；", rowNum, excelDTO.getUsername()));
            result.failCount++;
            return false;
        }
        return true;
    }

    /**
     * 检查邮箱是否已存在
     */
    private boolean checkEmailExists(UserExcelDTO excelDTO, int rowNum, ImportResult result) {
        if (excelDTO.getEmail() != null && !excelDTO.getEmail().trim().isEmpty()) {
            QueryWrapper<User> emailQuery = new QueryWrapper<>();
            emailQuery.eq("email", excelDTO.getEmail());
            User existEmailUser = this.getOne(emailQuery);
            if (existEmailUser != null) {
                result.failMessages.append(String.format("第%d行：邮箱%s已存在；", rowNum, excelDTO.getEmail()));
                result.failCount++;
                return false;
            }
        }
        return true;
    }

    /**
     * 从ExcelDTO创建用户对象
     */
    private User createUserFromExcelDTO(UserExcelDTO excelDTO) {
        User user = new User();
        BeanUtils.copyProperties(excelDTO, user);

        // 处理密码
        String password = excelDTO.getPassword();
        if (password == null || password.trim().isEmpty()) {
            password = "123456";
        }
        user.setPassword(passwordEncoder.encode(password));

        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        return user;
    }

    /**
     * 处理角色关联
     */
    private void processRoleAssignment(User user, UserExcelDTO excelDTO) {
        if (excelDTO.getRoleCodes() == null || excelDTO.getRoleCodes().trim().isEmpty()) {
            return;
        }

        List<Long> roleIds = resolveRoleIds(excelDTO.getRoleCodes());
        if (!roleIds.isEmpty()) {
            userRoleService.assignRolesToUser(user.getId(), roleIds);
        }
    }

    /**
     * 解析角色ID列表
     */
    private List<Long> resolveRoleIds(String roleCodes) {
        String[] roleCodeArray = roleCodes.split(",");
        List<Long> roleIds = new ArrayList<>(roleCodeArray.length);

        for (String roleCode : roleCodeArray) {
            roleCode = roleCode.trim();
            QueryWrapper<Role> roleQuery = new QueryWrapper<>();
            roleQuery.eq("code", roleCode);
            Role role = roleService.getOne(roleQuery);
            if (role != null) {
                roleIds.add(role.getId());
            }
        }

        return roleIds;
    }

    /**
     * 处理导入异常
     */
    private void handleImportException(UserExcelDTO excelDTO, int rowNum, Exception e, ImportResult result) {
        log.error("导入用户失败, row={}, username={}, error={}", rowNum, excelDTO.getUsername(), e.getMessage());
        result.failMessages.append(String.format("第%d行：%s；", rowNum, e.getMessage()));
        result.failCount++;
    }

    /**
     * 构建导入结果字符串
     */
    private String buildImportResult(ImportResult result) {
        String resultStr = String.format("导入完成：成功%d条，失败%d条", result.successCount, result.failCount);
        if (result.failCount > 0) {
            resultStr += "。失败详情：" + result.failMessages;
        }
        return resultStr;
    }

    @Override
    public boolean updateAvatar(Long userId, String avatarUrl) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        // 如果用户之前有头像，将旧头像文件标记为已删除
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            updateFileStatusByFileUrl(user.getAvatar(), true);
        }

        // 更新新头像文件状态为正常
        updateFileStatusByFileUrl(avatarUrl, false);

        user.setAvatar(avatarUrl);
        return this.updateById(user);
    }

    /**
     * 根据文件URL更新文件状态
     *
     * @param fileUrl   文件URL
     * @param isDeleted 是否删除
     */
    private void updateFileStatusByFileUrl(String fileUrl, boolean isDeleted) {
        try {
            LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(File::getFileUrl, fileUrl);
            wrapper.last("LIMIT 1");
            File file = fileService.getOne(wrapper);
            if (file != null) {
                file.setDelFlag(isDeleted ? 1 : 0);
                fileService.updateById(file);
            }
        } catch (Exception e) {
            log.error("更新文件状态失败: fileUrl={}, isDeleted={}, error={}", fileUrl, isDeleted, e.getMessage());
        }
    }

    @Override
    public boolean validatePassword(Long userId, String password) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public boolean updateProfile(Long userId, String nickname, String email, String phone, Integer gender) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setGender(gender);
        return this.updateById(user);
    }

    @Override
    public long getTotalUserCount() {
        // 尝试从 Redis 缓存获取
        String cacheKey = "system:total:user:count";
        String cachedCount = redisTemplate.opsForValue().get(cacheKey);

        if (cachedCount != null) {
            try {
                return Long.parseLong(cachedCount);
            } catch (NumberFormatException e) {
                log.warn("解析缓存的用户数失败: {}", cachedCount, e);
            }
        }

        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDelFlag, 0);
        long count = this.count(wrapper);

        // 存入缓存，有效期 1 小时
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(count), 1, TimeUnit.HOURS);
        log.info("查询总用户数: {}, 已缓存", count);

        return count;
    }

    /**
     * 清除总用户数缓存
     */
    private void clearTotalUserCountCache() {
        try {
            String cacheKey = "system:total:user:count";
            redisTemplate.delete(cacheKey);
            log.debug("已清除总用户数缓存");
        } catch (Exception e) {
            log.warn("清除总用户数缓存失败", e);
        }
    }

    /**
     * 导入结果内部类
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    private static class ImportResult {
        int successCount = 0;
        int failCount = 0;
        StringBuilder failMessages = new StringBuilder();
    }
}