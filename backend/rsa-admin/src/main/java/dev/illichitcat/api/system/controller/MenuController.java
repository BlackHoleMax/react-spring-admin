package dev.illichitcat.api.system.controller;

import dev.illichitcat.common.common.constant.ExceptionCodes;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.MenuDTO;
import dev.illichitcat.system.model.dto.MenuExcelDTO;
import dev.illichitcat.system.model.entity.Menu;
import dev.illichitcat.system.model.query.MenuQuery;
import dev.illichitcat.system.model.vo.MenuVO;
import dev.illichitcat.system.service.MenuService;
import dev.illichitcat.system.service.RoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "菜单管理", description = "菜单管理相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 查询菜单列表
     *
     * @param query 菜单查询条件
     * @return 菜单列表
     */
    @Operation(summary = "查询菜单列表")
    @RequirePermission("menu:list")
    @GetMapping("/list")
    public Result<List<Menu>> list(MenuQuery query) {
        Menu menu = new Menu();
        menu.setName(query.getName());
        menu.setStatus(query.getStatus());
        List<Menu> menuList = menuService.selectMenuList(menu);
        return Result.ok(menuList);
    }

    /**
     * 查询菜单树
     *
     * @param query 菜单查询条件
     * @return 菜单树
     */
    @Operation(summary = "查询菜单树")
    @RequirePermission("menu:list")
    @GetMapping("/tree")
    public Result<List<MenuVO>> tree(MenuQuery query) {
        Menu menu = new Menu();
        menu.setName(query.getName());
        menu.setStatus(query.getStatus());
        List<Menu> menuList = menuService.selectMenuList(menu);
        List<MenuVO> tree = menuService.buildMenuTree(menuList);
        return Result.ok(tree);
    }

    /**
     * 根据用户ID查询菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    @Operation(summary = "根据用户ID查询菜单树")
    @GetMapping("/user/{userId}")
    public Result<List<MenuVO>> getUserMenus(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Menu> menuList = menuService.selectMenusByUserId(userId);
        List<MenuVO> tree = menuService.buildMenuTree(menuList);
        return Result.ok(tree);
    }

    /**
     * 获取当前用户菜单
     *
     * @return 当前用户可访问的菜单树
     */
    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/current")
    public Result<List<MenuVO>> getCurrentUserMenus(HttpServletRequest request) {
        // 从request属性中获取用户ID（由JwtInterceptor设置）
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.fail(ExceptionCodes.UNAUTHORIZED, "用户未登录");
        }

        List<Menu> menuList = menuService.selectMenusByUserId(userId);
        List<MenuVO> tree = menuService.buildMenuTree(menuList);
        return Result.ok(tree);
    }

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    @Operation(summary = "根据ID查询菜单")
    @RequirePermission("menu:list")
    @GetMapping("/{id}")
    public Result<Menu> getById(@Parameter(description = "菜单ID") @PathVariable Long id) {
        Menu menu = menuService.selectMenuById(id);
        if (menu == null) {
            return Result.fail(ExceptionCodes.NOT_FOUND, "菜单不存在");
        }
        return Result.ok(menu);
    }

    /**
     * 新增菜单
     *
     * @param dto 菜单信息
     * @return 操作结果
     */
    @Operation(summary = "新增菜单")
    @RequirePermission("menu:add")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@RequestBody MenuDTO dto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        boolean success = menuService.insertMenu(menu);
        return success ? Result.ok() : Result.fail("新增菜单失败");
    }

    /**
     * 更新菜单
     *
     * @param dto 菜单信息
     * @return 操作结果
     */
    @Operation(summary = "更新菜单")
    @RequirePermission("menu:edit")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@RequestBody MenuDTO dto) {
        if (dto.getId() == null) {
            return Result.fail(ExceptionCodes.PARAM_ERROR, "菜单ID不能为空");
        }
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        boolean success = menuService.updateMenu(menu);
        return success ? Result.ok() : Result.fail("更新菜单失败");
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 操作结果
     */
    @Operation(summary = "删除菜单")
    @RequirePermission("menu:delete")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "菜单ID") @PathVariable Long id) {
        // 删除角色菜单关联
        roleMenuService.deleteByMenuId(id);
        // 删除菜单
        boolean success = menuService.deleteMenuById(id);
        return success ? Result.ok() : Result.fail("删除菜单失败");
    }

    /**
     * 导出菜单数据
     *
     * @param ids 菜单ID数组，为空则导出所有
     */
    @Operation(summary = "导出菜单数据")
    @RequirePermission("menu:export")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody(required = false) Long[] ids, HttpServletResponse response) throws IOException {
        List<Long> menuIds = ids != null ? Arrays.asList(ids) : null;
        List<MenuExcelDTO> menuList = menuService.exportMenus(menuIds);
        ExcelUtils.exportExcel(response, menuList, MenuExcelDTO.class, "菜单数据", "菜单列表");
    }

    /**
     * 导入菜单数据
     *
     * @param file Excel文件
     * @return 操作结果
     */
    @Operation(summary = "导入菜单数据")
    @RequirePermission("menu:import")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.IMPORT)
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) {
        try {
            List<MenuExcelDTO> menuList = ExcelUtils.importExcel(file, MenuExcelDTO.class);
            String result = menuService.importMenus(menuList);
            return Result.ok(result);
        } catch (IOException e) {
            log.error("导入菜单数据失败", e);
            return Result.fail("文件解析失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("导入菜单数据失败", e);
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载菜单导入模板
     */
    @Operation(summary = "下载菜单导入模板")
    @RequirePermission("menu:import")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建一个示例数据作为模板
        List<MenuExcelDTO> templateData = new ArrayList<>();
        MenuExcelDTO template = new MenuExcelDTO();
        template.setParentId(0L);
        template.setName("系统管理");
        template.setPath("/system");
        template.setIcon("system");
        template.setSort(1);
        template.setHidden(0);
        template.setExternal(0);
        template.setStatus(1);
        templateData.add(template);

        MenuExcelDTO template2 = new MenuExcelDTO();
        template2.setParentId(1L);
        template2.setName("用户管理");
        template2.setPath("/system/user");
        template2.setComponent("system/user/index");
        template2.setIcon("user");
        template2.setSort(0);
        template2.setHidden(0);
        template2.setExternal(0);
        template2.setPerms("user:list");
        template2.setStatus(1);
        templateData.add(template2);

        ExcelUtils.exportExcel(response, templateData, MenuExcelDTO.class, "菜单导入模板", "模板说明");
    }

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除菜单")
    @RequirePermission("menu:delete")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody Long[] ids) {
        boolean success = menuService.deleteMenusByIds(ids);
        return success ? Result.ok() : Result.fail("批量删除菜单失败");
    }
}