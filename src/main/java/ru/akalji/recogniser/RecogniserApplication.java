package ru.akalji.recogniser;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.akalji.recogniser.storage.StorageProperties;
import ru.akalji.recogniser.storage.StorageService;
import ru.akalji.recogniser.QueueChecker;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class RecogniserApplication {
	static QueueChecker queueChecker;

	public static void main(String[] args) {

		queueChecker = new QueueChecker();
		Thread myThready = new Thread(queueChecker);
		myThready.start();

		SpringApplication.run(RecogniserApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}

}
