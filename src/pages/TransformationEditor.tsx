import React, { useState } from 'react'
import EditorPane from '../components/EditorPane'
import useRBAC from '../hooks/useRBAC'

export default function TransformationEditor(){
  const [content, setContent] = useState('# Spark transformations config')
  const { can } = useRBAC()
  return (
    <div>
      <h1>Transformation Metadata Editor</h1>
      <EditorPane title="Transformation YAML" value={content} onChange={setContent} />
      <button className="btn btn-primary" disabled={!can('EDIT_METADATA')}>Save Draft</button>
      <button className="btn btn-secondary" style={{marginLeft:8}}>Validate</button>
    </div>
  )
}
