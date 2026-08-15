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

@Controller
public class WebAppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;

    public WebAppointmentController(AppointmentService appointmentService, PatientService patientService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("pageTitle", "Doctor Appointments & Queue");
        model.addAttribute("activeTab", "appointments");

        LocalDate today = LocalDate.now();
        model.addAttribute("selectedDate", today.toString());

        List<AppointmentEntity> appointments = appointmentService.getAppointmentsByDateAndDoctor(today, "");
        model.addAttribute("appointments", appointments);

        List<PatientEntity> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        return "appointments";
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(
            @RequestParam("patientId") String patientIdStr,
            @RequestParam("doctorName") String doctorName,
            @RequestParam("appointmentDate") String appointmentDateStr,
            @RequestParam("slotTime") String slotTime) {

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
            apt.setAppointmentDate(LocalDate.parse(appointmentDateStr));
            apt.setSlotTime(slotTime);
            apt.setStatus("SCHEDULED");

            appointmentService.bookAppointment(apt);
        }

        return "redirect:/appointments";
    }

    @GetMapping("/appointments/checkin/{id}")
    public String checkIn(@PathVariable("id") String id) {
        appointmentService.updateStatus(id, "CHECKED_IN");
        return "redirect:/appointments";
    }
}
