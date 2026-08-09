package com.society.infrastructure.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class DatabaseHealthServiceImpl implements DatabaseHealthService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DatabaseHealthServiceImpl.class);

    private final DataSource dataSource;

    public DatabaseHealthServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean isDatabaseAvailable() {

        try (Connection connection = dataSource.getConnection()) {

            LOGGER.info("SQLite connection established.");

            return connection.isValid(2);

        } catch (Exception ex) {

            LOGGER.error("Unable to connect to SQLite database.", ex);

            return false;
        }
    }
}