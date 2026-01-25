export type Role = 'Admin' | 'Engineer' | 'Support' | 'Viewer'
export type Permission =
  | 'MANAGE_SUBJECTS'
  | 'EDIT_METADATA'
  | 'ACTIVATE_METADATA'
  | 'TRIGGER_RUN'
  | 'RETRY_STAGE'
  | 'VIEW_DLQ'
  | 'VIEW_SPARK'

export const ROLE_PERMISSIONS: Record<Role, Permission[]> = {
  Admin: ['MANAGE_SUBJECTS', 'EDIT_METADATA', 'ACTIVATE_METADATA', 'TRIGGER_RUN', 'RETRY_STAGE', 'VIEW_DLQ', 'VIEW_SPARK'],
  Engineer: ['MANAGE_SUBJECTS', 'EDIT_METADATA', 'TRIGGER_RUN', 'RETRY_STAGE', 'VIEW_DLQ', 'VIEW_SPARK'],
  Support: ['VIEW_DLQ', 'RETRY_STAGE', 'VIEW_SPARK'],
  Viewer: ['VIEW_SPARK'],
}

export function canAccess(roles: string[] | null | undefined, perm: Permission): boolean {
  if (!roles) return false
  for (const r of roles) {
    const perms = ROLE_PERMISSIONS[r as Role]
    if (perms?.includes(perm)) return true
  }
  return false
}
