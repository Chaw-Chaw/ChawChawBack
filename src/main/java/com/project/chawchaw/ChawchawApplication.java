package com.project.chawchaw;

import com.project.chawchaw.config.email.EmailConfig;
import com.project.chawchaw.config.jasypt.JasyptConfig;
import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling

public class ChawchawApplication {

	public static void main(String[] args) {

//		SpringApplication.run(ChawchawApplication.class, args);
		StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
		jasypt.setPassword(System.getProperty("jasypt.encryptor.password"));
		jasypt.setAlgorithm("PBEWithMD5AndDES");


		StandardEncryptableEnvironment build = StandardEncryptableEnvironment
				.builder()
				.encryptor(jasypt)
				.build();

		new SpringApplicationBuilder()
				.environment(build)
				.sources(ChawchawApplication.class).run(args);



//		new SpringApplicationBuilder()
//				.environment(StandardEncryptableEnvironment.builder().build())
//				.sources(ChawchawApplication.class).run(args);
	}



	@Bean
	public RestTemplate getRestTemplate() {
			return new RestTemplate();
		}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}



}
