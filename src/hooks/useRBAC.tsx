import { useAuth } from '../auth/AuthContext'
import { canAccess } from '../auth/rbac-types'
import { Permission } from '../auth/rbac-types'

export const useRBAC = () => {
  const { user } = useAuth()
  const has = (perm: Permission) => canAccess(user?.roles ?? [], perm)
  return { can: has }
}

export default useRBAC
