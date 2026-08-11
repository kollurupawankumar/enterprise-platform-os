package com.clinical.patient.service;

import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    PatientEntity registerPatient(PatientEntity patient);
    Optional<PatientEntity> findByPatientId(String patientId);
    List<PatientEntity> getAllPatients();
}

@Service
@Transactional
class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientEntity registerPatient(PatientEntity patient) {
        if (patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
            long count = patientRepository.count() + 1;
            patient.setPatientId(String.format("PAT-%06d", count));
        }
        return patientRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientEntity> findByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientEntity> getAllPatients() {
        return patientRepository.findAll();
    }
}
