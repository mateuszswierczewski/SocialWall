package pl.mswierczewski.socialwall;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SocialWallApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialWallApplication.class, args);
	}

}
