/**
 * 根据文件名获取语言类型
 */
export const getLanguageByFilename = (filename: string): string => {
  const ext = filename.split('.').pop()?.toLowerCase();
  const languageMap: Record<string, string> = {
    tsx: 'typescript',
    ts: 'typescript',
    jsx: 'javascript',
    js: 'javascript',
    java: 'java',
    xml: 'xml',
    sql: 'sql',
    json: 'json',
    css: 'css',
    html: 'html',
    md: 'markdown',
    vm: 'velocity',
    yml: 'yaml',
    yaml: 'yaml',
  };
  return languageMap[ext || ''] || 'plaintext';
};
