import React, { useEffect, useState, useContext } from 'react'
import { DataContext } from '../context/DataContext'
import useRBAC from '../hooks/useRBAC'

export default function Dashboard(){
  const data = useContext(DataContext) as any
  const { can } = useRBAC()
  const [counts, setCounts] = useState({ subjectAreas: 0, entities: 0, runs: 0, success: 0, failed: 0 })

  useEffect(() => {
    // In a real app we'd call data service; here we simulate by querying mock API if available
    if (data?.subjectAreas) {
      data.subjectAreas.getSubjectAreas().then((sa: any[]) => {
        setCounts(c => ({ ...c, subjectAreas: sa.length }))
      })
    }
  }, [data])

  return (
    <div>
      <h1>Dashboard</h1>
      <p>Overview metrics will appear here.</p>
      <div style={{display:'grid', gridTemplateColumns:'repeat(4, 1fr)', gap:16, marginTop:16}}>
        <div style={{padding:12, border:'1px solid #ddd', borderRadius:6}}>Subject Areas: {counts.subjectAreas}</div>
        <div style={{padding:12, border:'1px solid #ddd', borderRadius:6}}>Entities: {counts.entities}</div>
        <div style={{padding:12, border:'1px solid #ddd', borderRadius:6}}>Runs: {counts.runs}</div>
        <div style={{padding:12, border:'1px solid #ddd', borderRadius:6}}>Success/Fail: {counts.success}/{counts.failed}</div>
      </div>
      {can('TRIGGER_RUN') && (
        <div style={{marginTop:12}}>
          <button>Manual Trigger</button>
        </div>
      )}
    </div>
  )
}
