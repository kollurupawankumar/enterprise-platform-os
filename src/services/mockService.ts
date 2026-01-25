import { DataService } from './interfaces'

const mockData = {
  subjectAreas: [{ id: 'sa1', name: 'Sales', domain: 'Sales', owner: 'team-a' }],
  entities: [{ id: 'e1', subjectAreaId: 'sa1', name: 'Customer', tableName: 'customer', partitionKeys: ['dt'], storageTarget: 'bronze', loadType: 'full' }],
}

export const MockDataService: DataService = {
  subjectAreas: {
    getSubjectAreas: async () => mockData.subjectAreas,
    createSubjectArea: async (payload) => payload,
  } as any,
  entities: {
    getEntities: async (saId: string) => mockData.entities.filter(e => e.subjectAreaId === saId),
    createEntity: async (payload) => payload,
  } as any,
  metadata: {
    getVersions: async () => [],
  } as any,
  runs: {
    getRuns: async () => [],
    triggerRun: async (payload) => payload,
  } as any
}
