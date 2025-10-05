package com.zerohero.gtnh_recipe_planner.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;

@Configuration
@Profile("!test")
public class DatabaseConfig {

    @Value("${spring.datasource.url:jdbc:h2:file:./data/recipe_planner}")
    private String dbUrl;

    @Value("${spring.datasource.username:sa}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Bean
    public DataSource dataSource() {
        // Ensure the data directory exists
        String currentDir = System.getProperty("user.dir");
        File dataDir = Paths.get(currentDir, "data").toFile();
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        return DataSourceBuilder.create()
                .url(dbUrl)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}