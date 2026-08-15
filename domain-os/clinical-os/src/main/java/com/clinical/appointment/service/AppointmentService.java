package com.clinical.appointment.service;

import com.clinical.appointment.entity.AppointmentEntity;
import com.clinical.appointment.repository.AppointmentRepository;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    AppointmentEntity bookAppointment(AppointmentEntity appointment);
    AppointmentEntity updateStatus(String appointmentId, String status);
    AppointmentEntity checkInAppointment(String appointmentId);
    Optional<AppointmentEntity> getAppointmentById(String appointmentId);
    List<AppointmentEntity> getAppointmentsByDate(LocalDate date);
    List<AppointmentEntity> getAppointmentsByDateAndDoctor(LocalDate date, String doctorId);
    List<AppointmentEntity> getAllAppointments();
}

@Service
@Transactional
class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final VisitService visitService;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  VisitService visitService,
                                  com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.appointmentRepository = appointmentRepository;
        this.visitService = visitService;
        this.idGenerator = idGenerator;
    }

    @Override
    public AppointmentEntity bookAppointment(AppointmentEntity appointment) {
        long count = appointmentRepository.count() + 1;
        if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isBlank()) {
            appointment.setAppointmentId(idGenerator.generateId("APPOINTMENT_ID_FORMAT", "APT-{YYYY}-{SEQ6}", count));
        }
        if (appointment.getAppointmentDate() == null) {
            appointment.setAppointmentDate(LocalDate.now());
        }
        if (appointment.getStatus() == null) {
            appointment.setStatus("BOOKED");
        }
        return appointmentRepository.save(appointment);
    }

    @Override
    public AppointmentEntity updateStatus(String appointmentId, String status) {
        AppointmentEntity apt = appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
        apt.setStatus(status);
        return appointmentRepository.save(apt);
    }

    @Override
    public AppointmentEntity checkInAppointment(String appointmentId) {
        AppointmentEntity apt = appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        // Assign Token Number
        List<AppointmentEntity> todayApts = appointmentRepository.findByAppointmentDate(apt.getAppointmentDate());
        int maxToken = 100;
        for (AppointmentEntity a : todayApts) {
            if (a.getTokenNumber() != null && a.getTokenNumber() > maxToken) {
                maxToken = a.getTokenNumber();
            }
        }
        int nextToken = maxToken + 1;
        apt.setTokenNumber(nextToken);
        apt.setStatus("CHECKED_IN");

        // Automatically create active OPD visit queue record
        try {
            visitService.createVisit(
                    apt.getPatientId(),
                    apt.getDoctorName() != null ? apt.getDoctorName() : "General OPD Doctor",
                    "OPD-APPOINTMENT"
            );
        } catch (Exception ignored) {}

        return appointmentRepository.save(apt);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppointmentEntity> getAppointmentById(String appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEntity> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date != null ? date : LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEntity> getAppointmentsByDateAndDoctor(LocalDate date, String doctorId) {
        List<AppointmentEntity> all = appointmentRepository.findAll();
        List<AppointmentEntity> result = new java.util.ArrayList<>();
        LocalDate targetDate = date != null ? date : LocalDate.now();

        for (AppointmentEntity apt : all) {
            boolean dateMatches = apt.getAppointmentDate() == null || apt.getAppointmentDate().equals(targetDate);
            if (dateMatches) {
                if (doctorId == null || doctorId.isBlank() || "ALL".equalsIgnoreCase(doctorId) || "ALL - All Doctors".equalsIgnoreCase(doctorId)) {
                    result.add(apt);
                } else if (doctorId.equalsIgnoreCase(apt.getDoctorId()) || (apt.getDoctorName() != null && apt.getDoctorName().toLowerCase().contains(doctorId.toLowerCase()))) {
                    result.add(apt);
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEntity> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}
