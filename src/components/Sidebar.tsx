import React from 'react'
import { Link } from 'react-router-dom'
export default function Sidebar(){
  return (
    <aside style={{width: 240, padding: 16, borderRight: '1px solid #ddd'}}>
      <nav>
        <ul style={{listStyle: 'none', padding: 0}}>
          <li><Link to="/dashboard">Dashboard</Link></li>
          <li><Link to="/subject-areas">Subject Areas</Link></li>
          <li><Link to="/subject-areas/1/entities">Entities</Link></li>
          <li><Link to="/metadata/editor">Metadata</Link></li>
          <li><Link to="/runs">Runs</Link></li>
        </ul>
      </nav>
    </aside>
  )
}
