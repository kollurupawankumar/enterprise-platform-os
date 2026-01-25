import React, { useEffect, useState, useContext } from 'react'
import { DataContext } from '../context/DataContext'
import useRBAC from '../hooks/useRBAC'

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
      <div>
        <input value={name} onChange={e => setName(e.target.value)} placeholder="New subject area name" />
        <button onClick={add} style={{marginLeft:8}} disabled={!can('MANAGE_SUBJECTS')}>Add</button>
      </div>
      <ul>
        {list.map(sa => (
          <li key={sa.id || sa.name}>{sa.name || sa.subjectAreaName}</li>
        ))}
      </ul>
    </div>
  )
}
