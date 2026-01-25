import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function Login(){
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    if (!username || !password) {
      setError('Please enter username and password')
      return
    }
    const ok = await login(username, password)
    if (ok) {
      navigate('/dashboard', { replace: true })
    } else {
      setError('Login failed')
    }
  }

  return (
    <div style={{maxWidth: 400, margin: '100px auto', padding: 20, border: '1px solid #ddd', borderRadius: 6, background: '#fff'}}>
      <h2>Login</h2>
      <form onSubmit={submit}>
        <div style={{marginBottom: 12}}>
          <label>Username</label>
          <input value={username} onChange={e => setUsername(e.target.value)} style={{width:'100%', padding:8}} />
        </div>
        <div style={{marginBottom: 12}}>
          <label>Password</label>
          <input type="password" value={password} onChange={e => setPassword(e.target.value)} style={{width:'100%', padding:8}} />
        </div>
        {error && <div style={{color: 'red', marginBottom: 8}}>{error}</div>}
        <button type="submit" style={{padding: '8px 12px'}}>Login</button>
      </form>
      <p style={{fontSize:12, color:'#666', marginTop:12}}>Demo: use username 'admin' for Admin role, 'eng-...' for Engineer, 'sup-...' for Support, other as Viewer.</p>
    </div>
  )
}
