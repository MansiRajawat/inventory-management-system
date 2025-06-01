package com.project.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(
		exclude = {
				org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
				org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
		},
scanBasePackages = "com.project.orders"
)
@EnableMongoRepositories(basePackages = "com.project.orders.dao")
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}


