package dev.illichitcat.api.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.illichitcat.common.common.result.Result;
import dev.illichitcat.system.config.OperationLog;
import dev.illichitcat.system.config.RequirePermission;
import dev.illichitcat.system.model.dto.NoticeDTO;
import dev.illichitcat.system.model.query.NoticeQuery;
import dev.illichitcat.system.model.vo.NoticeVO;
import dev.illichitcat.system.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 通知公告控制器
 *
 * @author Illichitcat
 * @since 2025/12/31
 */
@Tag(name = "通知公告管理", description = "通知公告管理相关接口")
@Slf4j
@RestController
@RequestMapping("/api/system/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 分页查询通知公告列表
     *
     * @param query 查询条件
     * @return 通知公告分页列表
     */
    @Operation(summary = "分页查询通知公告列表")
    @RequirePermission("notice:list")
    @GetMapping("/list")
    public Result<IPage<NoticeVO>> list(NoticeQuery query) {
        Page<NoticeVO> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<NoticeVO> noticePage = noticeService.selectNoticePage(page, query);
        return Result.ok(noticePage);
    }

    /**
     * 根据ID查询通知公告详情
     *
     * @param id 通知ID
     * @return 通知公告详情
     */
    @Operation(summary = "根据ID查询通知公告详情")
    @RequirePermission("notice:list")
    @GetMapping("/{id}")
    public Result<NoticeVO> getById(@Parameter(description = "通知ID") @PathVariable Long id) {
        NoticeVO noticeVO = noticeService.selectNoticeById(id);
        if (noticeVO == null) {
            return Result.fail("通知公告不存在");
        }
        return Result.ok(noticeVO);
    }

    /**
     * 新增通知公告
     *
     * @param noticeDTO 通知公告信息
     * @return 是否成功
     */
    @Operation(summary = "新增通知公告")
    @RequirePermission("notice:add")
    @OperationLog(title = "通知公告", businessType = OperationLog.BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@RequestBody NoticeDTO noticeDTO) {
        boolean result = noticeService.insertNotice(noticeDTO);
        return result ? Result.ok() : Result.fail("新增失败");
    }

    /**
     * 修改通知公告
     *
     * @param noticeDTO 通知公告信息
     * @return 是否成功
     */
    @Operation(summary = "修改通知公告")
    @RequirePermission("notice:edit")
    @OperationLog(title = "通知公告", businessType = OperationLog.BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@RequestBody NoticeDTO noticeDTO) {
        boolean result = noticeService.updateNotice(noticeDTO);
        return result ? Result.ok() : Result.fail("修改失败");
    }

    /**
     * 删除通知公告
     *
     * @param id 通知ID
     * @return 是否成功
     */
    @Operation(summary = "删除通知公告")
    @RequirePermission("notice:delete")
    @OperationLog(title = "通知公告", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "通知ID") @PathVariable Long id) {
        boolean result = noticeService.deleteNoticeById(id);
        return result ? Result.ok() : Result.fail("删除失败");
    }

    /**
     * 批量删除通知公告
     *
     * @param ids 通知ID数组
     * @return 是否成功
     */
    @Operation(summary = "批量删除通知公告")
    @RequirePermission("notice:delete")
    @OperationLog(title = "通知公告", businessType = OperationLog.BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Long[] ids) {
        boolean result = noticeService.deleteNoticeByIds(ids);
        return result ? Result.ok() : Result.fail("批量删除失败");
    }

    /**
     * 发布通知公告
     *
     * @param id 通知ID
     * @return 是否成功
     */
    @Operation(summary = "发布通知公告")
    @RequirePermission("notice:publish")
    @OperationLog(title = "通知公告", businessType = OperationLog.BusinessType.UPDATE)
    @PostMapping("/publish/{id}")
    public Result<Void> publish(@Parameter(description = "通知ID") @PathVariable Long id, HttpServletRequest request) {
        log.info("收到发布通知请求，通知ID: {}", id);
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        log.info("发布用户信息，用户ID: {}, 用户名: {}", userId, username);
        if (userId == null) {
            log.error("用户未登录，无法发布通知");
            return Result.fail("用户未登录");
        }
        log.info("开始调用 noticeService.publishNotice，通知ID: {}, 发布者ID: {}, 发布者用户名: {}", id, userId, username);
        boolean result = noticeService.publishNotice(id, userId, username);
        log.info("通知发布结果: {}, 通知ID: {}", result, id);
        return result ? Result.ok() : Result.fail("发布失败");
    }

    /**
     * 撤回通知公告
     *
     * @param id 通知ID
     * @return 是否成功
     */
    @Operation(summary = "撤回通知公告")
    @RequirePermission("notice:revoke")
    @OperationLog(title = "通知公告", businessType = OperationLog.BusinessType.UPDATE)
    @PostMapping("/revoke/{id}")
    public Result<Void> revoke(@Parameter(description = "通知ID") @PathVariable Long id) {
        boolean result = noticeService.revokeNotice(id);
        return result ? Result.ok() : Result.fail("撤回失败");
    }


}