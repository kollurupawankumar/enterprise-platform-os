package com.clinical.patient.service;

import com.clinical.admin.service.ClinicSettingService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    PatientEntity registerPatient(PatientEntity patient);
    PatientEntity updatePatient(PatientEntity patient);
    Optional<PatientEntity> findByPatientId(String patientId);
    Optional<PatientEntity> findByMobileNumber(String mobileNumber);
    List<PatientEntity> getAllPatients();
    void deletePatient(String patientId);
}

@Service
@Transactional
class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public PatientServiceImpl(PatientRepository patientRepository, com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.patientRepository = patientRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public PatientEntity registerPatient(PatientEntity patient) {
        if (patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
            long count = patientRepository.count() + 1;
            String patientId = idGenerator.generateId("PATIENT_ID_FORMAT", "PAT-{YYYY}-{SEQ}", count);
            patient.setPatientId(patientId);
        }
        return patientRepository.save(patient);
    }

    @Override
    public PatientEntity updatePatient(PatientEntity patient) {
        return patientRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientEntity> findByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientEntity> findByMobileNumber(String mobileNumber) {
        return patientRepository.findByPhone(mobileNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientEntity> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public void deletePatient(String patientId) {
        patientRepository.findByPatientId(patientId).ifPresent(patientRepository::delete);
    }
}
