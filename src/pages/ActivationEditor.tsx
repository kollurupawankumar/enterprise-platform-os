import React from 'react'
import useRBAC from '../hooks/useRBAC'

export default function ActivationEditor(){
  const { can } = useRBAC()
  return (
    <div>
      <h1>Activation & Versions</h1>
      <p>Activation flow controls and versioning lifecycle.</p>
      {can('ACTIVATE_METADATA') ? <button className="btn btn-warning">Activate Version</button> : <span className="text-muted">Activation requires Admin</span>}
    </div>
  )
}
