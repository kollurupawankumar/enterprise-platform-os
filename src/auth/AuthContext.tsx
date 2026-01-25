import React, { createContext, useContext, useState } from 'react'
import { DataMode } from '../types'

type UserRole = 'Admin'|'Engineer'|'Support'|'Viewer'
type User = { username: string; roles: UserRole[] } | null

type AuthContextValue = {
  user: User
  login: (username: string, password: string) => Promise<boolean>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export const AuthProvider: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User>(null)

  const login = async (username: string, password: string) => {
    // Simple in-memory mock auth; map roles based on username for demo
    const roles: User['roles'] = username === 'admin' ? ['Admin']
      : username.startsWith('eng') ? ['Engineer']
      : username.startsWith('sup') ? ['Support']
      : ['Viewer']
    setUser({ username, roles })
    return true
  }

  const logout = () => {
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
