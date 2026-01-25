import React, { useEffect, useState, useContext } from 'react'
import { DataContext } from '../context/DataContext'
import useRBAC from '../hooks/useRBAC'

export default function Runs(){
  const data = useContext(DataContext) as any
  const [runs, setRuns] = useState<any[]>([])
  const [payload, setPayload] = useState('{}')

  useEffect(() => {
    if (data?.runs?.getRuns) {
      data.runs.getRuns().then((r: any[]) => setRuns(r))
    }
  }, [data])

  const { can } = useRBAC()
  const trigger = async () => {
    if (!payload) return
    const res = await data.runs.triggerRun({ payload: JSON.parse(payload) })
    // refresh runs list
    data.runs.getRuns().then((r: any[]) => setRuns(r))
  }

  return (
    <div>
      <h1>Runs</h1>
      <div style={{marginBottom:12}}>
        <textarea value={payload} onChange={e => setPayload(e.target.value)} rows={4} cols={50} />
        <button onClick={trigger} style={{marginLeft:8}}>Trigger Run</button>
      </div>
      <table border={1} cellPadding={6} cellSpacing={0}>
        <thead><tr><th>RunId</th><th>Status</th><th>TriggeredBy</th></tr></thead>
        <tbody>
          {runs.map(r => (
            <tr key={r.runId || Math.random()}>
              <td>{r.runId || 'N/A'}</td>
              <td>{r.status || 'UNKNOWN'}</td>
              <td>{r.triggeredBy || 'system'}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {can('TRIGGER_RUN') && (
        <div style={{marginTop:8}}>
          <button onClick={trigger}>Retry/run</button>
        </div>
      )}
    </div>
  )
}
