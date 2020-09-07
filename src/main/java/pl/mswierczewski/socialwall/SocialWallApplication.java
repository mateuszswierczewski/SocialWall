package pl.mswierczewski.socialwall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import pl.mswierczewski.socialwall.configs.SwaggerConfig;

@SpringBootApplication
@EnableAsync
@Import(SwaggerConfig.class)
public class SocialWallApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialWallApplication.class, args);
	}

}
