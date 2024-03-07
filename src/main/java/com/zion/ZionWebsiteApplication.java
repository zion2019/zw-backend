package com.zion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMongoAuditing
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SpringBootApplication
public class ZionWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZionWebsiteApplication.class, args);
	}

}
