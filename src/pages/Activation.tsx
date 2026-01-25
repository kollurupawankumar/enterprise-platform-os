import React from 'react'
import useRBAC from '../hooks/useRBAC'

export default function Activation(){
  const { can } = useRBAC()
  return (
    <div>
      <h1>Activate Version</h1>
      <p>Activation affects next pipeline run. Audit trail is recorded.</p>
      {can('ACTIVATE_METADATA') ? <button className="btn btn-warning">Activate</button> : <span className="text-muted">Admin only</span>}
    </div>
  )
}
