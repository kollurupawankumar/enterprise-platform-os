import React, { useState } from 'react'
import EditorPane from '../components/EditorPane'
import useRBAC from '../hooks/useRBAC'

export default function SourceEditor(){
  const [content, setContent] = useState('# Source metadata YAML')
  const { can } = useRBAC()
  return (
    <div>
      <h1>Source Metadata Editor</h1>
      <EditorPane title="Source YAML" value={content} onChange={setContent} />
      <button className="btn btn-primary" disabled={!can('EDIT_METADATA')}>Save Draft</button>
      <button className="btn btn-secondary" style={{marginLeft:8}}>Validate</button>
    </div>
  )
}
