package com.society.certificate.controller;

import com.society.member.dto.MemberDto;
import com.society.share.dto.ShareCertificateDto;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PrintPreviewController {

    @FXML
    private WebView certificateWebView;

    public void setCertificateData(ShareCertificateDto certificate, MemberDto member) {
        setCertificateData(certificate, member, null);
    }

    public void setCertificateData(ShareCertificateDto certificate, MemberDto member, String propertyNumber) {
        try {
            InputStream is = getClass().getResourceAsStream("/templates/indus_crest_editable_certificate_final.html");
            if (is == null) {
                showError("Share certificate template not found!");
                return;
            }

            String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            String memberNo = (member != null && member.membershipNumber() != null && !member.membershipNumber().isBlank())
                    ? member.membershipNumber()
                    : certificate.certificateNumber().replace("SC-", "");

            String fullName = "";
            String guardianName = "";
            String flatVal = (propertyNumber != null && !propertyNumber.isBlank()) ? propertyNumber : "101";

            if (member != null) {
                fullName = (member.firstName() + " " + (member.lastName() != null ? member.lastName() : "")).trim();
                if (member.fatherOrSpouseName() != null && !member.fatherOrSpouseName().isBlank()) {
                    guardianName = member.fatherOrSpouseName();
                }
            } else {
                fullName = certificate.memberName();
            }

            int sharesCount = certificate.totalShares();
            String sharesWord = convertNumberToWords(sharesCount);

            double totalVal = certificate.totalAmount();
            String totalWord = convertNumberToWords((int) totalVal);

            double faceVal = certificate.faceValuePerShare();

            LocalDate date = certificate.issueDate() != null ? certificate.issueDate() : LocalDate.now();
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String admDate = (member != null && member.admissionDate() != null && !member.admissionDate().isBlank())
                    ? member.admissionDate()
                    : date.format(dateFmt);

            String shareRange = certificate.fromShareNumber() + " to " + certificate.toShareNumber();

            String certNoDisplay = (member != null && member.membershipNumber() != null && !member.membershipNumber().isBlank())
                    ? member.membershipNumber()
                    : certificate.certificateNumber().replace("SC-", "");
            String shareCertNoDisplay = certificate.certificateNumber().replace("SC-", "").replace("SH/", "");

            // Inject dynamic data into the template without underlines
            html = html.replace("<span class=\"red\">ICSC/</span> ____________", "<span class=\"red\"><b>" + certNoDisplay + "</b></span>")
                    .replace("<span class=\"red\">SH/</span> __________", "<span class=\"red\"><b>" + shareCertNoDisplay + "</b></span>")
                    .replace("____________________________________________,", "<b>" + fullName + "</b>,")
                    .replace("________________________________________,", "<b>" + (guardianName.isEmpty() ? "" : guardianName) + "</b>,")
                    .replace("________,", "<b>" + flatVal + "</b>,")
                    .replace("<span class=\"red\"><b>Two (02) Shares</b></span>", "<span class=\"red\"><b>" + sharesWord + " (" + String.format("%02d", sharesCount) + ") Shares</b></span>")
                    .replace("<span class=\"red\"><b>₹100/-</b></span>", "<span class=\"red\"><b>₹" + String.format("%.0f", faceVal) + "/-</b></span>")
                    .replace("<span class=\"red\"><b>₹200/-</b></span>", "<span class=\"red\"><b>₹" + String.format("%.0f", totalVal) + "/-</b></span>")
                    .replace("(Rupees Two Hundred Only)", "(Rupees " + totalWord + " Only)")
                    .replace("<input value=\"________________\"></td></tr>\n    <tr><td>Share No.</td><td><input value=\"________________\"></td></tr>\n    <tr><td>No. of Shares</td><td><span class=\"red\">2 (Two Shares)</span></td></tr>\n    <tr><td>Face Value</td><td><span class=\"red\">₹100/- each</span></td></tr>\n    <tr><td>Total Face Value</td><td><span class=\"red\">₹200/-</span></td></tr>\n    <tr><td>Date of Admission</td><td><input value=\"________________\"></td></tr>\n    <tr><td>Date of Issue</td><td><input value=\"________________\"></td></tr>",
                             "<b>" + memberNo + "</b></td></tr>\n    <tr><td>Share No.</td><td><b>" + shareRange + "</b></td></tr>\n    <tr><td>No. of Shares</td><td><span class=\"red\"><b>" + sharesCount + " (" + sharesWord + " Shares)</b></span></td></tr>\n    <tr><td>Face Value</td><td><span class=\"red\"><b>₹" + String.format("%.0f", faceVal) + "/- each</b></span></td></tr>\n    <tr><td>Total Face Value</td><td><span class=\"red\"><b>₹" + String.format("%.0f", totalVal) + "/-</b></span></td></tr>\n    <tr><td>Date of Admission</td><td><b>" + admDate + "</b></td></tr>\n    <tr><td>Date of Issue</td><td><b>" + date.format(dateFmt) + "</b></td></tr>")
                    .replace("_____ day of ______________ 20____.", "<b>" + getOrdinalDay(date.getDayOfMonth()) + "</b> day of <b>" + date.getMonth().name() + "</b> 20<b>" + String.valueOf(date.getYear()).substring(2) + "</b>.");

            this.renderedHtmlContent = html;
            certificateWebView.getEngine().loadContent(html);

        } catch (Exception ex) {
            showError("Failed to load certificate preview: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String renderedHtmlContent = "";

    public void print() {
        if (certificateWebView != null && certificateWebView.getEngine() != null) {
            try {
                certificateWebView.getEngine().print(null);
            } catch (Exception ex) {
                // JavaFX PrinterJob fallback if no active printer service is configured on OS
                saveHtmlPdf();
            }
        }
    }

    public void saveHtmlPdf() {
        if (renderedHtmlContent == null || renderedHtmlContent.isBlank()) return;

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Save Share Certificate PDF / HTML Document");
        fileChooser.setInitialFileName("Share_Certificate_" + System.currentTimeMillis() + ".html");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("HTML Web Document / Print-ready PDF (*.html)", "*.html"),
                new javafx.stage.FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
        );

        java.io.File file = fileChooser.showSaveDialog(certificateWebView.getScene().getWindow());
        if (file != null) {
            try {
                java.nio.file.Files.writeString(file.toPath(), renderedHtmlContent);
                showInformation("Certificate document saved successfully to:\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                showError("Failed to save certificate: " + ex.getMessage());
            }
        }
    }

    private String getOrdinalDay(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        return switch (day % 10) {
            case 1 -> day + "st";
            case 2 -> day + "nd";
            case 3 -> day + "rd";
            default -> day + "th";
        };
    }

    private String convertNumberToWords(int number) {
        if (number == 0) {
            return "Zero";
        }

        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        StringBuilder words = new StringBuilder();

        if ((number / 100) > 0) {
            words.append(units[number / 100]).append(" Hundred ");
            number %= 100;
        }

        if (number > 0) {
            if (number < 20) {
                words.append(units[number]);
            } else {
                words.append(tens[number / 10]).append(" ");
                words.append(units[number % 10]);
            }
        }

        return words.toString().trim();
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
