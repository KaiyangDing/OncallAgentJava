package com.oncallagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OncallAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(OncallAgentApplication.class, args);
    }
}
