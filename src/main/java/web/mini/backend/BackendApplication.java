package web.mini.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackages = {"web.mini.backend.model"})  // scan JPA entities
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }


    // TODO: CORS HEADER for POST in posts
    // TODO: CORS HEADER for POST in users
    // TODO: CORS HEADER for POST in CrossOrigin

    /*
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/posts").allowedOrigins("http://localhost:8080");
            }
        };
    }*/
}
