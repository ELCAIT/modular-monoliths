package com.example.modularmonoliths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@Modulith
@SpringBootApplication
public class ModulithWithSpringModulithApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModulithWithSpringModulithApplication.class, args);
	}

}
