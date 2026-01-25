import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import Dashboard from './pages/Dashboard'
import SubjectAreas from './pages/SubjectAreas'
import Entities from './pages/Entities'
import Runs from './pages/Runs'
import RunDetails from './pages/RunDetails'
import MetadataEditor from './pages/MetadataEditor'
import Sidebar from './components/Sidebar'
import Header from './components/Header'
import { DataProvider } from './context/DataContext'
import { AuthProvider } from './auth/AuthContext'
import ProtectedRoute from './auth/ProtectedRoute'
import Login from './pages/Login'
import { DataMode } from './types'
import SourceEditor from './pages/SourceEditor'
import TransformationEditor from './pages/TransformationEditor'
import EnrichmentEditor from './pages/EnrichmentEditor'
import Versions from './pages/Versions'
import Activation from './pages/Activation'

const App: React.FC = () => {
  // Dynamically determine mode from env; default to mock
  const mode: DataMode = (import.meta as any).env?.VITE_DATA_MODE === 'api' ? 'api' : 'mock'
  return (
    <AuthProvider>
      <DataProvider mode={mode}>
        <div className="app-shell">
          <Header />
          <div className="main-layout">
            <Sidebar />
            <div className="content-area">
              <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/subject-areas" element={<SubjectAreas />} />
                <Route path="/subject-areas/:saId/entities" element={<Entities />} />
                <Route path="/subject-areas/:saId/versions" element={<Versions />} />
                <Route path="/metadata/source" element={<SourceEditor />} />
                <Route path="/metadata/transformation" element={<TransformationEditor />} />
                <Route path="/metadata/enrichment" element={<EnrichmentEditor />} />
                <Route path="/activation" element={<Activation />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/runs" element={<Runs />} />
                <Route path="/runs/:runId" element={<RunDetails />} />
                <Route path="*" element={<Navigate to="/dashboard" />} />
              </Routes>
            </div>
          </div>
        </div>
      </DataProvider>
    </AuthProvider>
  )
}

export default App
