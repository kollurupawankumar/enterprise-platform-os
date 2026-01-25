import React from 'react'
import { DataMode } from '../types'
import { MockDataService } from '../services/mockService'
import { ApiDataService } from '../services/apiService'

export { MockDataService, ApiDataService }

export const DataContext = React.createContext<any>(null)

export const DataProvider: React.FC<{ mode: DataMode, children?: React.ReactNode }> = ({ mode, children }) => {
  const dataService = mode === 'mock' ? MockDataService : ApiDataService
  return (
    <DataContext.Provider value={dataService}>
      {children}
    </DataContext.Provider>
  )
}
