import React, { useEffect, useState, useContext } from 'react'
import { DataContext } from '../context/DataContext'
import useRBAC from '../hooks/useRBAC'
import { Link } from 'react-router-dom'
import WithPermission from '../components/WithPermission'

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
        <WithPermission permission={'MANAGE_SUBJECTS'}>
          <button className="btn btn-primary ms-2" onClick={add}>Add</button>
        </WithPermission>
        <Link to="/subject-areas/1/versions" className="btn btn-outline-secondary ms-2">Versions</Link>
      </div>
      <div className="list-group">{list.map(sa => (
          <div key={sa.id || sa.name} className="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
            <span>{sa.name || sa.subjectAreaName}</span>
            <span>
              <WithPermission permission={'MANAGE_SUBJECTS'}>
                <button className="btn btn-sm btn-outline-primary me-2">Edit</button>
              </WithPermission>
              <WithPermission permission={'MANAGE_SUBJECTS'}>
                <button className="btn btn-sm btn-outline-danger">Delete</button>
              </WithPermission>
            </span>
          </div>
        ))}</div>
    </div>
  )
}
