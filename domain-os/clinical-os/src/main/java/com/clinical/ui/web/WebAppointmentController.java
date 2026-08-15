package com.clinical.ui.web;

import com.clinical.appointment.entity.AppointmentEntity;
import com.clinical.appointment.service.AppointmentService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebAppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;

    public WebAppointmentController(AppointmentService appointmentService, PatientService patientService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @GetMapping("/appointments")
    public String listAppointments(
            @RequestParam(value = "date", required = false) String dateStr,
            @RequestParam(value = "doctor", required = false) String doctorFilter,
            @RequestParam(value = "scheduled", required = false) Boolean scheduled,
            Model model) {

        model.addAttribute("pageTitle", "OPD Token Queue & Appointment Schedule");
        model.addAttribute("activeTab", "appointments");

        LocalDate selectedDate = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : LocalDate.now();
        model.addAttribute("selectedDate", selectedDate.toString());
        model.addAttribute("doctorFilter", doctorFilter != null ? doctorFilter : "");

        List<AppointmentEntity> appointments = appointmentService.getAppointmentsByDateAndDoctor(selectedDate, doctorFilter != null ? doctorFilter : "");
        if (appointments.isEmpty()) {
            appointments = appointmentService.getAllAppointments();
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("totalCount", appointments.size());
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(scheduled));

        return "appointments";
    }

    @GetMapping("/appointments/book")
    public String bookAppointmentForm(Model model) {
        model.addAttribute("pageTitle", "Schedule OPD Appointment");
        model.addAttribute("activeTab", "appointment-book");

        List<PatientEntity> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        model.addAttribute("todayDate", LocalDate.now().toString());

        return "appointment_book";
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(
            @RequestParam("patientId") String patientIdStr,
            @RequestParam("doctorName") String doctorName,
            @RequestParam(value = "appointmentDate", required = false) String appointmentDateStr,
            @RequestParam(value = "slotTime", required = false) String slotTime,
            @RequestParam(value = "notes", required = false) String notes) {

        PatientEntity p = patientService.findByPatientId(patientIdStr).orElse(null);
        if (p == null) {
            try {
                Long id = Long.parseLong(patientIdStr);
                p = patientService.getAllPatients().stream()
                        .filter(patient -> id.equals(patient.getId()))
                        .findFirst().orElse(null);
            } catch (Exception ignored) {}
        }

        if (p != null) {
            AppointmentEntity apt = new AppointmentEntity();
            apt.setPatientId(p.getPatientId());
            apt.setPatientName(p.getFirstName() + " " + (p.getLastName() != null ? p.getLastName() : ""));
            apt.setDoctorId("DOC-101");
            apt.setDoctorName(doctorName);
            
            LocalDate aptDate = (appointmentDateStr != null && !appointmentDateStr.isBlank()) 
                    ? LocalDate.parse(appointmentDateStr) 
                    : LocalDate.now();
            apt.setAppointmentDate(aptDate);
            apt.setSlotTime(slotTime != null ? slotTime : "10:00 AM");
            apt.setStatus("SCHEDULED");
            apt.setNotes(notes);

            appointmentService.bookAppointment(apt);
        }

        return "redirect:/appointments?scheduled=true";
    }

    @GetMapping("/appointments/status/{id}/{status}")
    public String updateStatus(@PathVariable("id") String id, @PathVariable("status") String status) {
        appointmentService.updateStatus(id, status);
        return "redirect:/appointments";
    }
}
