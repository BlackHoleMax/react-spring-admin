import request from '@/utils/request';

export interface GenTable {
  tableId?: number;
  tableName: string;
  tableComment?: string;
  className?: string;
  tplCategory?: string;
  packageName?: string;
  functionAuthor?: string;
  genType?: string;
  genPath?: string;
  formLayout?: string;
  options?: string;
  createTime?: string;
  updateTime?: string;
  remark?: string;
  columns?: GenTableColumn[];
}

export interface GenTableColumn {
  columnId?: number;
  tableId?: number;
  columnName?: string;
  columnComment?: string;
  columnType?: string;
  javaType?: string;
  javaField?: string;
  isPk?: string;
  isIncrement?: string;
  isRequired?: string;
  isInsert?: string;
  isEdit?: string;
  isList?: string;
  isQuery?: string;
  queryType?: string;
  htmlType?: string;
  dictType?: string;
  sort?: number;
}

export const getDbTableList = async (tableName?: string): Promise<GenTable[]> => {
  const response = await request.get<GenTable[]>('/gen/db/list', {
    params: { tableName },
  });
  return response as unknown as GenTable[];
};

export const getGenTableList = async (params?: Partial<GenTable>): Promise<GenTable[]> => {
  const response = await request.get<GenTable[]>('/gen/list', { params });
  return response as unknown as GenTable[];
};

export const importTable = async (tables: string): Promise<void> => {
  await request.post('/gen/importTable', null, {
    params: { tables },
  });
};

export const getGenTableById = async (tableId: number): Promise<GenTable> => {
  const response = await request.get<GenTable>(`/gen/${tableId}`);
  return response as unknown as GenTable;
};

export const updateGenTable = async (data: GenTable): Promise<void> => {
  await request.put('/gen', data);
};

export const deleteGenTable = async (tableIds: number[]): Promise<void> => {
  await request.delete(`/gen/${tableIds.join(',')}`);
};

export const previewCode = async (tableId: number): Promise<Record<string, string>> => {
  const response = await request.get<Record<string, string>>(`/gen/preview/${tableId}`);
  return response as unknown as Record<string, string>;
};

export const downloadCode = async (tableName: string): Promise<void> => {
  const response = await fetch(`/api/gen/download/${tableName}`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `${tableName}.zip`;
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
};

export const batchDownloadCode = async (tables: string): Promise<void> => {
  const response = await fetch(`/api/gen/batchGenCode?tables=${tables}`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'rsa.zip';
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
};
