export type IDomainItem = { id: string; name: string };

export interface SubjectAreasService {
  getSubjectAreas(): Promise<any[]>;
  createSubjectArea(payload: any): Promise<any>;
}

export interface EntitiesService {
  getEntities(subjectAreaId: string): Promise<any[]>;
  createEntity(payload: any): Promise<any>;
}

export interface MetadataService {
  getVersions(id: string, type: string): Promise<any[]>;
}

export interface RunsService {
  getRuns(params?: any): Promise<any[]>;
  triggerRun(payload: any): Promise<any>;
}

export interface DataService {
  subjectAreas: SubjectAreasService;
  entities: EntitiesService;
  metadata: MetadataService;
  runs: RunsService;
}
