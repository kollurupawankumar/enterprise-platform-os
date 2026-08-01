package com.society.config;

public final class SocietyConfig {

    private SocietyConfig() {
    }

    public static String appName() {

        return AppConfig.get("app.name");

    }

    public static String appVersion() {

        return AppConfig.get("app.version");

    }

    public static String societyName() {

        return AppConfig.get("society.name");

    }

    public static String shortName() {

        return AppConfig.get("society.short.name");

    }

    public static String registrationNumber() {

        return AppConfig.get("society.registration.number");

    }

    public static String address() {

        return AppConfig.get("society.address");

    }

    public static String phone() {

        return AppConfig.get("society.phone");

    }

    public static String email() {

        return AppConfig.get("society.email");

    }

    public static String databaseFile() {

        return AppConfig.get("database.file");

    }

    public static String backupFolder() {

        return AppConfig.get("backup.folder");

    }

    public static String importFolder() {

        return AppConfig.get("import.folder");

    }

    public static String exportFolder() {

        return AppConfig.get("export.folder");

    }

    public static String certificateFolder() {

        return AppConfig.get("certificate.folder");

    }

}