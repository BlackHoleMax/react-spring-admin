import React from 'react';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism';

interface CodePreviewProps {
  code: string;
  language?: string;
  showLineNumbers?: boolean;
  maxHeight?: number | string;
}

/**
 * 代码预览组件
 * 支持语法高亮、行号显示、自定义高度
 */
const CodePreview: React.FC<CodePreviewProps> = ({
  code,
  language = 'plaintext',
  showLineNumbers = true,
  maxHeight = 600,
}) => {
  return (
    <div
      style={{
        border: '1px solid #434343',
        borderRadius: 8,
        overflow: 'hidden',
      }}
    >
      <SyntaxHighlighter
        language={language}
        style={vscDarkPlus}
        showLineNumbers={showLineNumbers}
        customStyle={{
          margin: 0,
          padding: '16px',
          fontSize: 13,
          lineHeight: 1.5,
          maxHeight,
          overflow: 'auto',
        }}
        codeTagProps={{
          style: {
            fontFamily: "'Fira Code', 'Consolas', 'Monaco', monospace",
          },
        }}
      >
        {code}
      </SyntaxHighlighter>
    </div>
  );
};

export default CodePreview;
