import React from 'react'
import { Link } from 'react-router-dom'

const versions = [
  { version: '1.0.0', createdBy: 'admin', date: '2026-01-25' , status: 'Active' },
  { version: '0.9.0', createdBy: 'admin', date: '2025-12-01', status: 'Archived' },
]

export default function Versions(){
  return (
    <div>
      <h1>Versions</h1>
      <table className="table table-striped">
        <thead><tr><th>Version</th><th>Created By</th><th>Date</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
          {versions.map(v => (
            <tr key={v.version}>
              <td>{v.version}</td>
              <td>{v.createdBy}</td>
              <td>{v.date}</td>
              <td>{v.status}</td>
              <td><Link to="#" className="btn btn-sm btn-outline-primary">Compare</Link></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
