import React, { useState } from 'react'
import EditorPane from '../components/EditorPane'
import useRBAC from '../hooks/useRBAC'

export default function EnrichmentEditor(){
  const [content, setContent] = useState('-- SQL files list placeholder')
  const { can } = useRBAC()
  return (
    <div>
      <h1>Enrichment Metadata Editor</h1>
      <EditorPane title="Enrichment SQL" value={content} onChange={setContent} />
      <button className="btn btn-primary" disabled={!can('EDIT_METADATA')}>Save Draft</button>
      <button className="btn btn-secondary" style={{marginLeft:8}}>Upload SQL File</button>
    </div>
  )
}
