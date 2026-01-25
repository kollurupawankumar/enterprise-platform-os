import React from 'react'
import { useParams } from 'react-router-dom'

export default function RunDetails(){
  const { runId } = useParams<{ runId: string }>()
  // Mock data for demonstration
  const run = { runId, status: 'RUNNING', startedAt: '2026-01-25T12:00:00Z', endedAt: null, triggeredBy: 'admin' }
  const timeline = [
    { stageName: 'Validate', status: 'SUCCESS', duration: '2s', start: '12:00:01', end: '12:00:03', attempts: 1 },
    { stageName: 'Ingest', status: 'RUNNING', duration: '—', start: '12:00:04', end: null, attempts: 1 },
  ]
  const dlq = [
    { messageId: 'm1', stage: 'Ingest', reason: 'Parsing error', createdAt: '2026-01-25T12:01:00Z', payload: '{...}' }
  ]
  return (
    <div>
      <h1>Run Details</h1>
      <div className="card mb-3">
        <div className="card-body">
          <h5 className="card-title">Run Summary</h5>
          <p className="card-text">Run ID: {run.runId || 'N/A'} | Status: {run.status} | Triggered by: {run.triggeredBy} | Started: {run.startedAt}</p>
        </div>
      </div>
      <div className="card mb-3">
        <div className="card-body">
          <h5 className="card-title">Status Timeline</h5>
          <ul className="list-group list-group-flush">
            {timeline.map((t, idx) => (
              <li className="list-group-item" key={idx}>{t.stageName} - {t.status} (start {t.start} end {t.end || '—'})</li>
            ))}
          </ul>
        </div>
      </div>
      <div className="card mb-3">
        <div className="card-body">
          <h5 className="card-title">DLQ Messages</h5>
          <ul className="list-group list-group-flush">
            {dlq.map((m, i) => (
              <li className="list-group-item" key={i}>
                <strong>{m.messageId}</strong> - {m.stage} - {m.reason} - {m.createdAt}
                <pre style={{background:'#f8f9fa', padding:8, marginTop:6}}>{m.payload}</pre>
              </li>
            ))}
          </ul>
        </div>
      </div>
      <div className="card mb-3">
        <div className="card-body">
          <h5 className="card-title">Spark Job Insights</h5>
          <div>JobId: j-12345 | App: app-123 | Engine: EMR | Logs: <a href="#">logUrl</a></div>
        </div>
      </div>
    </div>
  )
}
