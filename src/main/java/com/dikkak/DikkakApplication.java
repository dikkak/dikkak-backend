package com.dikkak;

import com.dikkak.aop.AspectCustom;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Import(AspectCustom.class)
public class DikkakApplication {

	public static void main(String[] args) {
		SpringApplication.run(DikkakApplication.class, args);
	}

}
