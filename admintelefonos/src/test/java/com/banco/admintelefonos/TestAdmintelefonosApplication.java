package com.banco.admintelefonos;

import org.springframework.boot.SpringApplication;

public class TestAdmintelefonosApplication {

	public static void main(String[] args) {
		SpringApplication.from(AdmintelefonosApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
