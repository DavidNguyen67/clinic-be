package com.camel.clinic.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            Map<String, Object> props = new HashMap<>();
            dotenv.entries().forEach(e -> props.put(e.getKey(), e.getValue()));

            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenvProperties", props));
        } catch (Exception e) {
            System.out.println("Warning: .env file not found");
        }
    }
}