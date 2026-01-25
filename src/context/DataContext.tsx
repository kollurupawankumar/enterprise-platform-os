import React from 'react'
import { DataMode } from '../types'
import { MockDataService } from '../services/mockService'
import { ApiDataService } from '../services/apiService'

export { MockDataService, ApiDataService }

export const DataContext = React.createContext<any>(null)

export const DataProvider: React.FC<{ mode?: DataMode, children?: React.ReactNode }> = ({ mode, children }) => {
  const modeToUse: DataMode = mode ?? ((import.meta as any).env?.VITE_DATA_MODE === 'api' ? 'api' : 'mock')
  const dataService = modeToUse === 'mock' ? MockDataService : ApiDataService
  return (
    <DataContext.Provider value={dataService}>
      {children}
    </DataContext.Provider>
  )
}

export const useDataService = () => React.useContext(DataContext)
