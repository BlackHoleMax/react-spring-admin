import { getDictItemsByCode } from '@/services/dict';
import type { DictItem } from '@/types';

/**
 * 字典工具函数
 */

/**
 * 根据字典编码获取字典项选项
 * @param dictCode 字典编码
 * @returns Select组件的options数组
 */
export const getDictOptions = async (dictCode: string) => {
  try {
    const items = await getDictItemsByCode(dictCode);
    return items.map((item) => ({
      label: item.label,
      value: item.value,
    }));
  } catch (error) {
    console.error(`获取字典项失败: ${dictCode}`, error);
    return [];
  }
};

/**
 * 根据字典编码和值获取字典项文本
 * @param dictCode 字典编码
 * @param value 字典项值
 * @returns 字典项文本
 */
export const getDictLabel = (items: DictItem[], value: string | number): string => {
  const item = items.find((item) => item.value === String(value));
  return item ? item.label : String(value);
};
