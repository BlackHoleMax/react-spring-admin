import React, { type ReactNode } from 'react';
import { usePermission } from '@/hooks/usePermission';

interface AuthorizedProps {
  /**
   * 权限标识，可以是单个字符串或字符串数组
   */
  permission: string | string[];
  /**
   * 是否需要全部满足所有权限（默认为 false，满足任意一个即可）
   */
  requireAll?: boolean;
  /**
   * 有权限时渲染的内容
   */
  children: ReactNode;
  /**
   * 无权限时渲染的内容（默认不渲染）
   */
  fallback?: ReactNode;
  /**
   * 无权限时是否显示（默认为 false，不显示）
   */
  hideNoPermission?: boolean;
}

/**
 * 权限控制组件
 *
 * 根据用户权限控制组件的显示/隐藏
 *
 * @example
 * // 基本用法
 * <Authorized permission="user:add">
 *   <Button>添加用户</Button>
 * </Authorized>
 *
 * // 多个权限（满足任意一个）
 * <Authorized permission={['user:add', 'user:edit']}>
 *   <Button>操作</Button>
 * </Authorized>
 *
 * // 多个权限（必须全部满足）
 * <Authorized permission={['user:add', 'user:edit']} requireAll>
 *   <Button>操作</Button>
 * </Authorized>
 *
 * // 自定义无权限时的显示内容
 * <Authorized permission="user:delete" fallback={<span>无权限</span>}>
 *   <Button>删除</Button>
 * </Authorized>
 */
const Authorized: React.FC<AuthorizedProps> = ({
  permission,
  requireAll = false,
  children,
  fallback = null,
  hideNoPermission = true,
}) => {
  const hasPermission = usePermission(permission, requireAll);

  if (hasPermission) {
    return <>{children}</>;
  }

  if (!hideNoPermission && fallback !== null) {
    return <>{fallback}</>;
  }

  return null;
};

export default Authorized;
