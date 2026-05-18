package com.inditex.g1_agencia_viajes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.retry.annotation.EnableRetry;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableRetry
public class
G1AgenciaViajesApplication {

	public static void main(String[] args) {
		SpringApplication.run(G1AgenciaViajesApplication.class, args);
	}

}
