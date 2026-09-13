package com.api.telemetryProcessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.api.telemetryProcessor")
public class TelemetryProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelemetryProcessorApplication.class, args);
	}

}
