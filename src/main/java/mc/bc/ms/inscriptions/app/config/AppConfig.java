package mc.bc.ms.inscriptions.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
	
	@Bean
	public WebClient createWebClient() {
		return WebClient.create("http://localhost:8002/courses");
	}
}
