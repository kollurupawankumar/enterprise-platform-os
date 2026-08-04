package com.society.certificate.controller;

import com.society.member.dto.MemberDto;
import com.society.share.dto.ShareCertificateDto;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.transform.Scale;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PrintPreviewController {

    @FXML
    private Label certNoLabel;

    @FXML
    private Label shareCertNoLabel;

    @FXML
    private Label memberNameLabel;

    @FXML
    private Label relationLabel;

    @FXML
    private Label flatNoLabel;

    @FXML
    private Label sharesWordsLabel;

    @FXML
    private Label faceValueWordsLabel;

    @FXML
    private Label totalAmountWordsLabel;

    @FXML
    private Label issueDayLabel;

    @FXML
    private Label issueMonthLabel;

    @FXML
    private Label issueYearLabel;

    @FXML
    private Label membershipNoLabel;

    @FXML
    private Label shareRangeLabel;

    @FXML
    private Label totalSharesLabel;

    @FXML
    private Label faceValueLabel;

    @FXML
    private Label totalFaceValueLabel;

    @FXML
    private Label admissionDateLabel;

    @FXML
    private Label issueDateLabel;

    public void setCertificateData(ShareCertificateDto certificate, MemberDto member) {

        // Remove cert prefix if already stored
        String cleanNo = certificate.certificateNumber().replace("SC-", "");
        certNoLabel.setText(cleanNo);
        shareCertNoLabel.setText(cleanNo);

        String fullName = "";
        if (member != null) {
            fullName = (member.firstName() + " " + (member.lastName() != null ? member.lastName() : "")).trim();
            membershipNoLabel.setText(member.membershipNumber() != null ? member.membershipNumber() : "");
            flatNoLabel.setText(member.membershipNumber() != null ? member.membershipNumber() : "B0403");
        } else {
            fullName = certificate.memberName();
            membershipNoLabel.setText("");
            flatNoLabel.setText("B0403");
        }

        memberNameLabel.setText(fullName);
        relationLabel.setText("S/o / D/o / W/o  N/A");

        int sharesCount = certificate.totalShares();
        String sharesWord = convertNumberToWords(sharesCount);
        sharesWordsLabel.setText(sharesWord + " (" + String.format("%02d", sharesCount) + ")");

        double faceVal = certificate.faceValuePerShare();
        faceValueWordsLabel.setText("₹" + String.format("%.0f", faceVal) + "/-");

        double totalVal = certificate.totalAmount();
        String totalWord = convertNumberToWords((int) totalVal);
        totalAmountWordsLabel.setText(totalWord + " Only");

        LocalDate date = certificate.issueDate() != null ? certificate.issueDate() : LocalDate.now();
        issueDayLabel.setText(getOrdinalDay(date.getDayOfMonth()));
        issueMonthLabel.setText(date.getMonth().name());
        issueYearLabel.setText(String.valueOf(date.getYear()));

        shareRangeLabel.setText(certificate.fromShareNumber() + " to " + certificate.toShareNumber());
        totalSharesLabel.setText(sharesCount + " (" + sharesWord + ")");
        faceValueLabel.setText("₹ " + String.format("%.0f", faceVal) + "/- each");
        totalFaceValueLabel.setText("₹ " + String.format("%.0f", totalVal) + "/-");

        admissionDateLabel.setText(date.toString());
        issueDateLabel.setText(date.toString());

    }

    public void print(Node node) {

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) {

            boolean proceed = job.showPrintDialog(node.getScene().getWindow());

            if (proceed) {

                Printer printer = job.getPrinter();
                PageLayout pageLayout = job.getJobSettings().getPageLayout();

                double printableWidth = pageLayout.getPrintableWidth();
                double printableHeight = pageLayout.getPrintableHeight();

                double nodeWidth = node.getBoundsInLocal().getWidth();
                double nodeHeight = node.getBoundsInLocal().getHeight();

                // Scale node to fit the A4 page layout
                double scaleX = printableWidth / nodeWidth;
                double scaleY = printableHeight / nodeHeight;
                double scale = Math.min(scaleX, scaleY);

                Scale scaleTransform = new Scale(scale, scale);
                node.getTransforms().add(scaleTransform);

                boolean success = job.printPage(node);

                node.getTransforms().remove(scaleTransform);

                if (success) {
                    job.endJob();
                    showInformation("Certificate sent to printer successfully.");
                } else {
                    showError("Printing failed.");
                }

            }

        } else {
            showError("Could not create print job. Check your printer installation.");
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
