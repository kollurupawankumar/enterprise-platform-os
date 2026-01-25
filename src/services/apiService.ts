import { DataService } from './interfaces'
import axios from 'axios'

const apiBase = '/api'

export const ApiDataService: DataService = {
  subjectAreas: {
    getSubjectAreas: async () => (await axios.get(`${apiBase}/subject-areas`)).data,
    createSubjectArea: async (payload) => (await axios.post(`${apiBase}/subject-areas`, payload)).data,
  } as any,
  entities: {
    getEntities: async (saId: string) => (await axios.get(`${apiBase}/subject-areas/${saId}/entities`)).data,
    createEntity: async (payload) => (await axios.post(`${apiBase}/entities`, payload)).data,
  } as any,
  metadata: {
    getVersions: async (id: string, type: string) => (await axios.get(`${apiBase}/entities/${id}/metadata/${type}/versions`)).data,
  } as any,
  runs: {
    getRuns: async (params?: any) => (await axios.get(`${apiBase}/runs`, { params })).data,
    triggerRun: async (payload) => (await axios.post(`${apiBase}/runs/trigger`, payload)).data,
  } as any
}
