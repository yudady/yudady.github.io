package io.github.yudady;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {


	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(App.class, args)));
	}
}
