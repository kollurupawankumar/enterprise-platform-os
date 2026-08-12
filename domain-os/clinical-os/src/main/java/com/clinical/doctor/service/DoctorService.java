package com.clinical.doctor.service;

import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DoctorService {
    DoctorEntity registerDoctor(DoctorEntity doctor);
    DoctorEntity updateDoctor(DoctorEntity doctor);
    void deleteDoctor(String doctorId);
    Optional<DoctorEntity> findByDoctorId(String doctorId);
    List<DoctorEntity> getAllDoctors();
    void deactivateDoctor(String doctorId);
}

@Service
@Transactional
class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public DoctorServiceImpl(DoctorRepository doctorRepository, com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.doctorRepository = doctorRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public DoctorEntity registerDoctor(DoctorEntity doctor) {
        if (doctor.getDoctorId() == null || doctor.getDoctorId().isEmpty()) {
            long count = doctorRepository.count() + 1;
            doctor.setDoctorId(idGenerator.generateId("DOCTOR_ID_FORMAT", "DOC-{SEQ6}", count));
        }
        return doctorRepository.save(doctor);
    }

    @Override
    public DoctorEntity updateDoctor(DoctorEntity doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public void deleteDoctor(String doctorId) {
        doctorRepository.findByDoctorId(doctorId).ifPresent(doctorRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorEntity> findByDoctorId(String doctorId) {
        return doctorRepository.findByDoctorId(doctorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorEntity> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public void deactivateDoctor(String doctorId) {
        doctorRepository.findByDoctorId(doctorId).ifPresent(d -> {
            d.setActive(0);
            doctorRepository.save(d);
        });
    }
}
