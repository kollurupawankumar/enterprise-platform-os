import React from 'react'

type Props = {
  title: string
  value: string
  onChange: (v: string) => void
  language?: 'yaml'|'json'|'sql'|'text'
}

export default function EditorPane({ title, value, onChange }: Props) {
  return (
    <div className="card mb-3">
      <div className="card-header">{title}</div>
      <div className="card-body">
        <textarea className="form-control" rows={12} value={value} onChange={e => onChange(e.target.value)} />
      </div>
    </div>
  )
}
