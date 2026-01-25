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

const App: React.FC = () => {
  return (
    <div className="app-shell">
      <Header />
      <div className="main-layout">
        <Sidebar />
        <div className="content-area">
          <Routes>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/subject-areas" element={<SubjectAreas />} />
            <Route path="/subject-areas/:saId/entities" element={<Entities />} />
            <Route path="/metadata/editor" element={<MetadataEditor />} />
            <Route path="/runs" element={<Runs />} />
            <Route path="/runs/:runId" element={<RunDetails />} />
            <Route path="*" element={<Navigate to="/dashboard" />} />
          </Routes>
        </div>
      </div>
    </div>
  )
}

export default App
