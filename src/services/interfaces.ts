export type IDomainItem = { id: string; name: string };

export interface SubjectAreasService {
  getSubjectAreas(): Promise<any[]>;
  createSubjectArea(payload: any): Promise<any>;
  updateSubjectArea(id: string, payload: any): Promise<any>;
  deleteSubjectArea(id: string): Promise<any>;
}

export interface EntitiesService {
  getEntities(subjectAreaId: string): Promise<any[]>;
  createEntity(payload: any): Promise<any>;
  updateEntity(id: string, payload: any): Promise<any>;
  deleteEntity(id: string): Promise<any>;
}

export interface MetadataService {
  getVersions(id: string, type: string): Promise<any[]>;
  getVersion(id: string, type: string, version: string): Promise<any>;
  createMetadataVersion(id: string, type: string, payload: any): Promise<any>;
  updateMetadataVersion(id: string, type: string, version: string, payload: any): Promise<any>;
}

export interface RunsService {
  getRuns(params?: any): Promise<any[]>;
  triggerRun(payload: any): Promise<any>;
  getRun(runId: string): Promise<any>;
  retryStage(runId: string, stage: string, payload?: any): Promise<any>;
}

export interface DataService {
  subjectAreas: SubjectAreasService;
  entities: EntitiesService;
  metadata: MetadataService;
  runs: RunsService;
}
