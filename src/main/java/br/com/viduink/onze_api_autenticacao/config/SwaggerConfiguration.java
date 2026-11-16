package br.com.viduink.onze_api_autenticacao.config;

// Importa a anotação de configuração do Spring
import org.springframework.context.annotation.Configuration;

// Importa a anotação para criação de Beans
import org.springframework.context.annotation.Bean;

// Importa as classes de configuração OpenAPI
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfiguration {

    // Configura as informações da documentação Swagger
    @Bean
    public OpenAPI customOpenAPI() {

        // Define o título, a descrição e a versão da API
        return new OpenAPI()
                .info(new Info()
                        .title("Onze API - Autenticação")
                        .description("Documentação da API REST de autenticação do projeto Onze API Autenticação.")
                        .version("1.0.0"));
    }
}