import { message } from 'antd';
import React from 'react';
import type { Notice } from '@/types/notice';

export const showNoticeMessage = (notice: Notice) => {
  const priorityColors: Record<number, string> = {
    1: '#1890ff',
    2: '#faad14',
    3: '#ff4d4f',
  };

  const priorityIcons: Record<number, string> = {
    1: 'ℹ️',
    2: '⚠️',
    3: '🚨',
  };

  const icon = priorityIcons[notice.priority] || '📢';
  const color = priorityColors[notice.priority] || '#1890ff';

  message.open({
    type: 'info',
    content: React.createElement(
      'div',
      {
        style: { display: 'flex', alignItems: 'flex-start', gap: 8 },
      },
      React.createElement('span', { style: { fontSize: 20 } }, icon),
      React.createElement(
        'div',
        { style: { flex: 1 } },
        React.createElement(
          'div',
          { style: { fontWeight: 'bold', marginBottom: 4, color: color } },
          notice.title
        ),
        React.createElement(
          'div',
          { style: { color: '#666', fontSize: 12, marginBottom: 4 } },
          `${notice.typeName} · ${notice.publisherName} · ${new Date(notice.publishTime).toLocaleString('zh-CN')}`
        ),
        React.createElement(
          'div',
          { style: { color: '#999', fontSize: 13 } },
          notice.content.length > 100 ? notice.content.substring(0, 100) + '...' : notice.content
        )
      )
    ),
    duration: 5,
    style: {
      marginTop: 20,
    },
  });
};
