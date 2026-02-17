package dev.illichitcat.api.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.common.utils.ExcelUtils;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.DictDTO;
import dev.illichitcat.system.model.dto.DictExcelDTO;
import dev.illichitcat.system.model.dto.DictItemDTO;
import dev.illichitcat.system.model.entity.Dict;
import dev.illichitcat.system.model.entity.DictItem;
import dev.illichitcat.system.model.query.DictQuery;
import dev.illichitcat.system.service.DictItemService;
import dev.illichitcat.system.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * 字典管理控制器
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Tag(name = "字典管理", description = "字典管理相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @Autowired
    private DictItemService dictItemService;

    /**
     * 分页查询字典列表
     *
     * @param query 字典查询条件
     * @return 字典分页列表
     */
    @Operation(summary = "分页查询字典列表")
    @RequirePermission("dict:list")
    @GetMapping("/list")
    public Result<IPage<Dict>> list(DictQuery query) {
        Dict dict = new Dict();
        dict.setDictName(query.getDictName());
        dict.setDictCode(query.getDictCode());
        dict.setStatus(query.getStatus());
        Page<Dict> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<Dict> dictPage = dictService.selectDictList(page, dict);
        return Result.ok(dictPage, toPageInfo(dictPage));
    }

    /**
     * 获取所有启用的字典列表
     *
     * @return 字典列表
     */
    @Operation(summary = "获取所有启用的字典列表")
    @GetMapping("/all")
    public Result<List<Dict>> getAllEnabled() {
        List<Dict> dictList = dictService.selectAllEnabled();
        return Result.ok(dictList);
    }

    /**
     * 根据ID查询字典
     *
     * @param id 字典ID
     * @return 字典信息
     */
    @Operation(summary = "根据ID查询字典")
    @RequirePermission("dict:list")
    @GetMapping("/{id}")
    public Result<Dict> getById(@Parameter(description = "字典ID") @PathVariable Long id) {
        Dict dict = dictService.selectDictById(id);
        if (dict == null) {
            return Result.fail(404, "字典不存在");
        }
        return Result.ok(dict);
    }

    /**
     * 根据字典编码查询字典
     *
     * @param dictCode 字典编码
     * @return 字典信息
     */
    @Operation(summary = "根据字典编码查询字典")
    @RequirePermission("dict:list")
    @GetMapping("/code/{dictCode}")
    public Result<Dict> getByCode(@Parameter(description = "字典编码") @PathVariable String dictCode) {
        Dict dict = dictService.selectDictByCode(dictCode);
        if (dict == null) {
            return Result.fail(404, "字典不存在");
        }
        return Result.ok(dict);
    }

    /**
     * 新增字典
     *
     * @param dto 字典信息
     * @return 操作结果
     */
    @Operation(summary = "新增字典")
    @RequirePermission("dict:add")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@RequestBody DictDTO dto) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dto, dict);
        boolean success = dictService.insertDict(dict);
        return success ? Result.ok() : Result.fail("新增字典失败");
    }

    /**
     * 更新字典
     *
     * @param dto 字典信息
     * @return 操作结果
     */
    @Operation(summary = "更新字典")
    @RequirePermission("dict:edit")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@RequestBody DictDTO dto) {
        if (dto.getId() == null) {
            return Result.fail(400, "字典ID不能为空");
        }
        Dict dict = new Dict();
        BeanUtils.copyProperties(dto, dict);
        boolean success = dictService.updateDict(dict);
        return success ? Result.ok() : Result.fail("更新字典失败");
    }

    /**
     * 删除字典
     *
     * @param id 字典ID
     * @return 操作结果
     */
    @Operation(summary = "删除字典")
    @RequirePermission("dict:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "字典ID") @PathVariable Long id) {
        // 删除字典项
        dictItemService.deleteItemsByDictId(id);
        // 删除字典
        boolean success = dictService.deleteDictById(id);
        return success ? Result.ok() : Result.fail("删除字典失败");
    }

    // ==================== 字典项相关接口 ====================

    /**
     * 根据字典ID查询字典项列表
     *
     * @param dictId 字典ID
     * @return 字典项列表
     */
    @Operation(summary = "根据字典ID查询字典项列表")
    @RequirePermission("dict:list")
    @GetMapping("/{dictId}/items")
    public Result<List<DictItem>> getItemsByDictId(@Parameter(description = "字典ID") @PathVariable Long dictId) {
        List<DictItem> items = dictItemService.selectItemsByDictId(dictId);
        return Result.ok(items);
    }

    /**
     * 根据字典编码查询字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    @Operation(summary = "根据字典编码查询字典项列表")
    @RequirePermission("dict:list")
    @GetMapping("/code/{dictCode}/items")
    public Result<List<DictItem>> getItemsByDictCode(@Parameter(description = "字典编码") @PathVariable String dictCode) {
        List<DictItem> items = dictItemService.selectItemsByDictCode(dictCode);
        return Result.ok(items);
    }

    /**
     * 根据ID查询字典项
     *
     * @param id 字典项ID
     * @return 字典项信息
     */
    @Operation(summary = "根据ID查询字典项")
    @RequirePermission("dict:list")
    @GetMapping("/item/{id}")
    public Result<DictItem> getItemById(@Parameter(description = "字典项ID") @PathVariable Long id) {
        DictItem item = dictItemService.selectItemById(id);
        if (item == null) {
            return Result.fail(404, "字典项不存在");
        }
        return Result.ok(item);
    }

    /**
     * 新增字典项
     *
     * @param dto 字典项信息
     * @return 操作结果
     */
    @Operation(summary = "新增字典项")
    @RequirePermission("dict:add")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.INSERT)
    @PostMapping("/item")
    public Result<Void> addItem(@RequestBody DictItemDTO dto) {
        DictItem item = new DictItem();
        BeanUtils.copyProperties(dto, item);
        boolean success = dictItemService.insertItem(item);
        return success ? Result.ok() : Result.fail("新增字典项失败");
    }

    /**
     * 更新字典项
     *
     * @param dto 字典项信息
     * @return 操作结果
     */
    @Operation(summary = "更新字典项")
    @RequirePermission("dict:edit")
    @PutMapping("/item")
    public Result<Void> updateItem(@RequestBody DictItemDTO dto) {
        if (dto.getId() == null) {
            return Result.fail(400, "字典项ID不能为空");
        }
        DictItem item = new DictItem();
        BeanUtils.copyProperties(dto, item);
        boolean success = dictItemService.updateItem(item);
        return success ? Result.ok() : Result.fail("更新字典项失败");
    }

    /**
     * 删除字典项
     *
     * @param id 字典项ID
     * @return 操作结果
     */
    @Operation(summary = "删除字典项")
    @RequirePermission("dict:delete")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/item/{id}")
    public Result<Void> deleteItem(@Parameter(description = "字典项ID") @PathVariable Long id) {
        boolean success = dictItemService.deleteItemById(id);
        return success ? Result.ok() : Result.fail("删除字典项失败");
    }

    private Result.PageInfo toPageInfo(IPage<?> page) {
        return new Result.PageInfo()
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent());
    }

    /**
     * 导出字典数据
     *
     * @param ids 字典ID数组，为空则导出所有
     */
    @Operation(summary = "导出字典数据")
    @RequirePermission("dict:export")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody(required = false) Long[] ids, HttpServletResponse response) throws IOException {
        List<Long> dictIds = ids != null ? Arrays.asList(ids) : null;
        List<DictExcelDTO> dictList = dictService.exportDicts(dictIds);
        ExcelUtils.exportExcel(response, dictList, DictExcelDTO.class, "字典数据", "字典列表");
    }

    /**
     * 导入字典数据
     *
     * @param file Excel文件
     * @return 操作结果
     */
    @Operation(summary = "导入字典数据")
    @RequirePermission("dict:import")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.IMPORT)
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) {
        try {
            List<DictExcelDTO> dictList = ExcelUtils.importExcel(file, DictExcelDTO.class);
            String result = dictService.importDicts(dictList);
            return Result.ok(result);
        } catch (IOException e) {
            log.error("导入字典数据失败", e);
            return Result.fail("文件解析失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("导入字典数据失败", e);
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载字典导入模板
     */
    @Operation(summary = "下载字典导入模板")
    @RequirePermission("dict:import")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建一个示例数据作为模板
        List<DictExcelDTO> templateData = new ArrayList<>();
        DictExcelDTO template = new DictExcelDTO();
        template.setDictName("性别");
        template.setDictCode("sex");
        template.setSort(0);
        template.setRemark("性别字典");
        template.setStatus(1);
        templateData.add(template);

        ExcelUtils.exportExcel(response, templateData, DictExcelDTO.class, "字典导入模板", "模板说明");
    }

    /**
     * 批量删除字典
     *
     * @param ids 字典ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除字典")
    @RequirePermission("dict:delete")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody Long[] ids) {
        boolean success = dictService.deleteDictsByIds(ids);
        return success ? Result.ok() : Result.fail("批量删除字典失败");
    }
}