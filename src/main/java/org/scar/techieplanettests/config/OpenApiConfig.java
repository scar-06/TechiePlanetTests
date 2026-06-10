package org.scar.techieplanettests.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentScoresOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Student Scores API")
                .description("Records student scores in 5 subjects and produces reports "
                        + "with each student's scores, mean, median and mode. "
                        + "Techie Planet software engineer assessment.")
                .version("v1")
                .contact(new Contact().name("Oscar Nwanze").email("nwanzeoscar@gmail.com")));
    }
}
