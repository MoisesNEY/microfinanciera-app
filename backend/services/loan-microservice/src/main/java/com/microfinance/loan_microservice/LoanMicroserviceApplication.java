package com.microfinance.loan_microservice;

import com.microfinance.loan_microservice.config.AccountingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties(AccountingProperties.class)
@EnableFeignClients
public class LoanMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanMicroserviceApplication.class, args);
	}

}
