import { MockDataService } from './mockService'
import { ApiDataService } from './apiService'

export type DataMode = 'mock' | 'api'

export function getDataService(mode: DataMode) {
  return mode === 'mock' ? MockDataService : ApiDataService
}
