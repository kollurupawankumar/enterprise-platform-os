package com.core.os.reporting.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class UniversalCsvReportExporter {

    public String exportToCsv(String reportName, List<String> headers, List<List<String>> rows, String targetDir) throws IOException {
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = reportName.toLowerCase().replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".csv";
        File csvFile = Paths.get(targetDir, fileName).toFile();

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(String.join(",", headers) + "\n");
            for (List<String> row : rows) {
                writer.write(String.join(",", row.stream().map(this::escapeCsv).toList()) + "\n");
            }
        }

        return csvFile.getAbsolutePath();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
