package com.clinical.appointment.repository;

import com.clinical.appointment.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    Optional<AppointmentEntity> findByAppointmentId(String appointmentId);
    List<AppointmentEntity> findByAppointmentDate(LocalDate appointmentDate);
    List<AppointmentEntity> findByAppointmentDateAndDoctorId(LocalDate appointmentDate, String doctorId);
    List<AppointmentEntity> findByPatientId(String patientId);
}
