import React from 'react'
import useRBAC from '../hooks/useRBAC'
export default function MetadataEditor(){
  const { can } = useRBAC()
  return (
    <div>
      <h1>Metadata Editor</h1>
      <p>Source, Transformation, and Enrichment editors.</p>
      {can('EDIT_METADATA') ? (
        <button>Save</button>
      ) : (
        <span style={{color:'#888'}}>Read-only mode</span>
      )}
    </div>
  )
}
