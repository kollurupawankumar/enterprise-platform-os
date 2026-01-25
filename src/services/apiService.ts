import { DataService } from './interfaces'
import axios from 'axios'

const apiBase = '/api'

export const ApiDataService: DataService = {
  subjectAreas: {
    getSubjectAreas: async () => (await axios.get(`${apiBase}/subject-areas`)).data,
    createSubjectArea: async (payload) => (await axios.post(`${apiBase}/subject-areas`, payload)).data,
    updateSubjectArea: async (id, payload) => (await axios.put(`${apiBase}/subject-areas/${id}`, payload)).data,
    deleteSubjectArea: async (id) => (await axios.delete(`${apiBase}/subject-areas/${id}`)).data,
  } as any,
  entities: {
    getEntities: async (saId: string) => (await axios.get(`${apiBase}/subject-areas/${saId}/entities`)).data,
    createEntity: async (payload) => (await axios.post(`${apiBase}/entities`, payload)).data,
    updateEntity: async (id: string, payload) => (await axios.put(`${apiBase}/entities/${id}`, payload)).data,
    deleteEntity: async (id: string) => (await axios.delete(`${apiBase}/entities/${id}`)).data,
  } as any,
  metadata: {
    getVersions: async (id: string, type: string) => (await axios.get(`${apiBase}/entities/${id}/metadata/${type}/versions`)).data,
    getVersion: async (id: string, type: string, version: string) => (await axios.get(`${apiBase}/entities/${id}/metadata/${type}/versions/${version}`)).data,
    createMetadataVersion: async (id: string, type: string, payload) => (await axios.post(`${apiBase}/entities/${id}/metadata/${type}/versions`, payload)).data,
    updateMetadataVersion: async (id: string, type: string, version: string, payload) => (await axios.put(`${apiBase}/entities/${id}/metadata/${type}/versions/${version}`, payload)).data,
  } as any,
  runs: {
    getRuns: async (params?: any) => (await axios.get(`${apiBase}/runs`, { params })).data,
    triggerRun: async (payload) => (await axios.post(`${apiBase}/runs/trigger`, payload)).data,
    getRun: async (runId: string) => (await axios.get(`${apiBase}/runs/${runId}`)).data,
    retryStage: async (runId: string, stage: string, payload?: any) => (await axios.post(`${apiBase}/runs/${runId}/retry-stage/${stage}`, payload)).data,
  } as any
}
