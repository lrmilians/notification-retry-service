package dev.lrcode.notifications.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notificationApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Retry Service API")
                        .description("API for sending, smart retries, and notification auditing")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LRCODE")
                                .email("lrmilians@gmail.com")
                                .url("https://github.com/lrmilians"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                );
    }
}