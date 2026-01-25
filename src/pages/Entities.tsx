import React, { useEffect, useState, useContext } from 'react'
import { DataContext } from '../context/DataContext'
import useRBAC from '../hooks/useRBAC'

export default function Entities(){
  const data = useContext(DataContext) as any
  const [list, setList] = useState<any[]>([])

  const { can } = useRBAC()
  // simplistic fetch for demo purposes
  useEffect(() => {
    // get from subject areas id 1 for demo
    if (data?.entities?.getEntities) {
      data.entities.getEntities('sa1').then((rows: any[]) => setList(rows))
    }
  }, [data])
  return (
    <div>
      <h1>Entities</h1>
      <ul>
        {list.map(e => (
          <li key={e.id}>{e.name} ({e.tableName})</li>
        ))}
      </ul>
      {can('MANAGE_SUBJECTS') && (
        <div style={{marginTop: 8}}>
          {/* placeholder for future entity creation UI guarded by RBAC */}
        </div>
      )}
    </div>
  )
}
