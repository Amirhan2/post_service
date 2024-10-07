package faang.school.postservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableFeignClients(basePackages = "faang.school.postservice.client")
@OpenAPIDefinition(
        info = @Info(
                title = "Post Service for Corporation-X",
                description = "Post Service", version = "1.0.0",
                contact = @Contact(
                        name = "Sergey Sklyar aka mad_owl91",
                        email = "sklyar1091@gmail.com",
                        url = "https://github.com/Silencemess1ah"
                )
        )
)
public class PostServiceApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
