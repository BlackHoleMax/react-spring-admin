import { useAppSelector } from '@/store/hooks';

/**
 * 权限检查 Hook
 *
 * @example
 * // 检查单个权限
 * const hasPermission = usePermission('user:add');
 *
 * // 检查多个权限（满足任意一个即可）
 * const hasAnyPermission = usePermission(['user:add', 'user:edit']);
 *
 * // 检查多个权限（必须全部满足）
 * const hasAllPermissions = usePermission(['user:add', 'user:edit'], true);
 */
export const usePermission = (
  permission: string | string[],
  requireAll: boolean = false
): boolean => {
  const permissions = useAppSelector((state) => state.permission.permissions);

  if (!permissions || permissions.length === 0) {
    return false;
  }

  if (typeof permission === 'string') {
    return permissions.includes(permission);
  }

  if (requireAll) {
    return permission.every((perm) => permissions.includes(perm));
  }

  return permission.some((perm) => permissions.includes(perm));
};
