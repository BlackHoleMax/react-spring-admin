package dev.illichitcat.common.utils;

import cn.idev.excel.ExcelReader;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类
 * <p>
 * 基于 FastExcel 实现 Excel 的导入导出功能
 *
 * @author Illichitcat
 * @since 2025/12/24
 */
@Slf4j
public class ExcelUtils {

    /**
     * 导出Excel
     *
     * @param response  HTTP响应
     * @param dataList  数据列表
     * @param clazz     实体类Class对象
     * @param fileName  文件名（不含扩展名）
     * @param sheetName 工作表名
     * @param <T>       实体类型
     */
    public static <T> void exportExcel(HttpServletResponse response, List<T> dataList, Class<T> clazz,
                                       String fileName, String sheetName) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            try (ExcelWriter excelWriter = FastExcel.write(response.getOutputStream(), clazz).build()) {
                WriteSheet writeSheet = FastExcel.writerSheet(sheetName).build();

                // 设置表头样式
                WriteCellStyle headWriteCellStyle = new WriteCellStyle();
                headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                WriteFont headWriteFont = new WriteFont();
                headWriteFont.setFontHeightInPoints((short) 11);
                headWriteFont.setBold(true);
                headWriteCellStyle.setWriteFont(headWriteFont);
                headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

                excelWriter.write(dataList, writeSheet);
            }

            log.info("Excel 导出成功，文件名：{}", fileName);
        } catch (IOException e) {
            log.error("Excel 导出失败", e);
            throw new RuntimeException("Excel 导出失败", e);
        }
    }

    /**
     * 导入Excel
     *
     * @param file  Excel文件
     * @param clazz 实体类Class对象
     * @param <T>   实体类型
     * @return 数据列表
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            ExcelReader excelReader = FastExcel.read(inputStream, clazz, new AnalysisEventListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    dataList.add(data);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel解析完成，共读取{}条数据", dataList.size());
                }
            }).build();

            excelReader.readAll();
        }

        return dataList;
    }

    /**
     * 导入Excel（带自定义处理）
     *
     * @param file     Excel文件
     * @param clazz    实体类Class对象
     * @param listener 数据处理函数
     * @param <T>      实体类型
     */
    public static <T> void importExcel(MultipartFile file, Class<T> clazz,
                                       AnalysisEventListener<T> listener) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            ExcelReader excelReader = FastExcel.read(inputStream, clazz, listener).build();
            excelReader.readAll();
        }
    }

    /**
     * 创建简单的读取监听器
     *
     * @param dataList 数据列表
     * @param <T>      实体类型
     * @return 读取监听器
     */
    public static <T> AnalysisEventListener<T> createSimpleReadListener(List<T> dataList) {
        return new AnalysisEventListener<T>() {
            @Override
            public void invoke(T data, AnalysisContext context) {
                dataList.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Excel 读取完成，共读取 {} 条数据", dataList.size());
            }
        };
    }
}