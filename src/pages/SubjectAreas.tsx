import React, { useEffect, useState, useContext } from 'react'
import { DataContext } from '../context/DataContext'
import useRBAC from '../hooks/useRBAC'
import { Link } from 'react-router-dom'

export default function SubjectAreas(){
  const data = useContext(DataContext) as any
  const [list, setList] = useState<any[]>([])
  const [name, setName] = useState('')

  useEffect(() => {
    if (data?.subjectAreas?.getSubjectAreas) {
      data.subjectAreas.getSubjectAreas().then((x: any[]) => setList(x))
    }
  }, [data])

  const { can } = useRBAC()
  const add = async () => {
    if (!name.trim()) return
    const res = await data.subjectAreas.createSubjectArea({ name })
    setList(prev => [...prev, res || { id: String(Date.now()), name }])
    setName('')
  }

  return (
    <div>
      <h1>Subject Areas</h1>
      <div className="mb-3">
        <input className="form-control d-inline-block w-auto" value={name} onChange={e => setName(e.target.value)} placeholder="New subject area name" />
        <button className="btn btn-primary ms-2" onClick={add} disabled={!can('MANAGE_SUBJECTS')}>Add</button>
        <Link to="/subject-areas/1/versions" className="btn btn-outline-secondary ms-2">Versions</Link>
      </div>
      <div className="list-group">{list.map(sa => (
          <div key={sa.id || sa.name} className="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
            <span>{sa.name || sa.subjectAreaName}</span>
            <span>
              <button className="btn btn-sm btn-outline-primary me-2" disabled={!can('MANAGE_SUBJECTS')}>Edit</button>
              <button className="btn btn-sm btn-outline-danger" disabled={!can('MANAGE_SUBJECTS')}>Delete</button>
            </span>
          </div>
        ))}</div>
    </div>
  )
}
