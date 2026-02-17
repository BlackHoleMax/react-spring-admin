package dev.illichitcat.api.user.controller;

import dev.illichitcat.common.common.properties.MinioProperties;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.FileUtils;
import dev.illichitcat.common.utils.MinioUtils;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.model.dto.UpdatePasswordDTO;
import dev.illichitcat.system.model.dto.UpdateProfileDTO;
import dev.illichitcat.system.model.entity.File;
import dev.illichitcat.system.model.entity.User;
import dev.illichitcat.system.model.vo.UserProfileVO;
import dev.illichitcat.system.service.FileService;
import dev.illichitcat.system.service.LoginLogService;
import dev.illichitcat.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "个人中心")
@RestController
@Slf4j
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@ConditionalOnBean(MinioUtils.class)
public class ProfileController {

    private static final int MIN_LENGTH = 6;
    private static final String IMG_TYPE = "image/";
    private static final String STORAGE_PROVIDER = "minio";
    private final UserService userService;
    private final LoginLogService loginLogService;
    private final MinioUtils minioUtils;
    private final MinioProperties minioProperties;
    private final FileService fileService;

    @Operation(summary = "上传头像")
    @OperationLog(title = "个人中心", businessType = OperationLog.BusinessType.INSERT)
    @PostMapping("/upload/avatar")
    public Result<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute Long userId,
            @RequestAttribute String username,
            HttpServletRequest request) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(IMG_TYPE)) {
            return Result.fail("只能上传图片文件");
        }

        long maxSize = 2 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return Result.fail("图片大小不能超过2MB");
        }

        String url = minioUtils.uploadFile(file);
        String originalFilename = file.getOriginalFilename();

        // 保存文件信息到数据库
        saveFileInfo(file, originalFilename, url, userId, username);

        // 更新用户头像
        boolean success = userService.updateAvatar(userId, url);
        if (!success) {
            return Result.fail("更新头像失败");
        }

        Map<String, String> result = new HashMap<>(2);
        result.put("url", url);
        result.put("fileName", originalFilename);
        return Result.ok(result);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping
    public Result<UserProfileVO> getProfile(@RequestAttribute Long userId) {
        User user = userService.selectUserById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(user, vo);

        long loginCount = loginLogService.countByUserId(userId);
        vo.setLoginCount(loginCount);

        return Result.ok(vo);
    }

    @Operation(summary = "更新用户头像")
    @OperationLog(title = "个人中心", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping("/avatar")
    public Result<Map<String, String>> updateAvatar(
            @RequestAttribute Long userId,
            @RequestBody Map<String, String> request) {
        String avatarUrl = request.get("avatar");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return Result.fail("头像URL不能为空");
        }

        boolean success = userService.updateAvatar(userId, avatarUrl);
        if (!success) {
            return Result.fail("更新头像失败");
        }

        Map<String, String> result = new HashMap<>(1);
        result.put("avatar", avatarUrl);
        return Result.ok(result);
    }

    @Operation(summary = "修改密码")
    @OperationLog(title = "个人中心", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestAttribute Long userId,
            @RequestBody UpdatePasswordDTO dto) {
        if (dto.getOldPassword() == null || dto.getOldPassword().isEmpty()) {
            return Result.fail("旧密码不能为空");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().isEmpty()) {
            return Result.fail("新密码不能为空");
        }
        if (dto.getNewPassword().length() < MIN_LENGTH) {
            return Result.fail("新密码长度不能少于6位");
        }

        boolean valid = userService.validatePassword(userId, dto.getOldPassword());
        if (!valid) {
            return Result.fail("旧密码错误");
        }

        boolean success = userService.changePassword(userId, dto.getNewPassword());
        if (!success) {
            return Result.fail("修改密码失败");
        }

        return Result.ok();
    }

    @Operation(summary = "更新个人资料")
    @OperationLog(title = "个人中心", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping
    public Result<UserProfileVO> updateProfile(
            @RequestAttribute Long userId,
            @RequestBody UpdateProfileDTO dto) {
        boolean success = userService.updateProfile(
                userId,
                dto.getNickname(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getGender()
        );

        if (!success) {
            return Result.fail("更新个人资料失败");
        }

        User user = userService.selectUserById(userId);
        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(user, vo);

        long loginCount = loginLogService.countByUserId(userId);
        vo.setLoginCount(loginCount);

        return Result.ok(vo);
    }

    /**
     * 保存文件信息到数据库
     */
    private void saveFileInfo(MultipartFile file, String originalFilename, String url, Long userId, String username) {
        try {
            File fileInfo = FileUtils.buildFileInfo(file, originalFilename, url, STORAGE_PROVIDER,
                    minioProperties.getBucketName(), userId, username, File.class);
            fileService.save(fileInfo);
        } catch (Exception e) {
            // 保存文件信息失败不影响上传结果，只记录日志
            log.error("保存文件信息到数据库失败", e);
        }
    }
}
