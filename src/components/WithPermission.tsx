import React from 'react'
import useRBAC from '../hooks/useRBAC'
import { Permission } from '../auth/rbac-types'

type Props = {
  permission: Permission
  fallback?: React.ReactNode
  children: React.ReactNode
}

export default function WithPermission({ permission, fallback = null, children }: Props) {
  const { can } = useRBAC()
  return can(permission) ? <>{children}</> : (fallback ?? null)
}
