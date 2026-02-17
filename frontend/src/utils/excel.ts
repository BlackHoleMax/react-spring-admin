/**
 * Excel 工具函数
 */

/**
 * 下载 Excel 文件
 * @param blob Excel 文件的 Blob 对象
 * @param fileName 文件名
 */
export const downloadExcel = (blob: Blob, fileName: string) => {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};

/**
 * 导出 Excel
 * @param exportFn 导出函数
 * @param params 查询参数
 * @param fileName 文件名
 */
export const exportExcelFile = async (
  exportFn: (params: any) => Promise<Blob>,
  params: any,
  fileName: string
) => {
  try {
    const blob = await exportFn(params);
    downloadExcel(blob, `${fileName}.xlsx`);
  } catch (error) {
    console.error('导出失败:', error);
    throw error;
  }
};
