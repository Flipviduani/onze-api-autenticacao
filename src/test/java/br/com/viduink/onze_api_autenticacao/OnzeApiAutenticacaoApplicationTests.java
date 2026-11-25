package br.com.viduink.onze_api_autenticacao;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc //Habilitar a biblioteca MockMvc do Spring Boot
@ActiveProfiles("test") //Apontando o arquivo application-test.yaml
class ApiAutenticacaoApplicationTests {

    //Injeção de dependência
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("TS01 - Deve criar um usuário com sucesso na API.")
    void deveCriarUsuarioComSucesso() throws Exception {

        var faker = new Faker(new Locale("pt-BR"));

        var nome = faker.name().fullName();
        var email = faker.internet().emailAddress();

        System.out.println("TS01 - Nome gerado: " + nome + " | Email gerado: " + email);

        //ARRANGE
        var request = """
					{
						"nome" : "%s",
						"email" : "%s",
						"senha" : "@Admin123"
					}
				""".formatted(
                nome, email
        );

        //ACT
        var response = mockMvc.perform(post("/api/v1/usuario/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request));

        //ASSERT
        response.andExpect(status().isCreated());
        response.andExpect(jsonPath("$.mensagem").value("Usuário cadastrado com sucesso"));
        response.andExpect(jsonPath("$.nome").value(nome));
        response.andExpect(jsonPath("$.email").value(email));
        response.andExpect(jsonPath("$.perfil").value("Operador"));
    }

    @Test
    @DisplayName("TS02 - Não deve permitir a criação de usuário com email já cadastrado no sistema.")
    void naoDeveCriarUsuarioComEmailJaCadastrado() throws Exception {

        var faker = new Faker(new Locale("pt-BR"));

        var nome = faker.name().fullName();
        var email = faker.internet().emailAddress();

        var request = """
					{
						"nome" : "%s",
						"email" : "%s",
						"senha" : "@Admin123"
					}
				""".formatted(nome, email);

        mockMvc.perform(post("/api/v1/usuario/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/usuario/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.tipo").value("email"))
                .andExpect(jsonPath("$.mensagem").value("O email informado já está cadastrado. Tente outro."));
    }

    @Test
    @DisplayName("TS03 - Não deve permitir a criação de usuário com senha fraca.")
    void naoDeveCriarUsuarioComSenhaFraca() throws Exception {

        var faker = new Faker(new Locale("pt-BR"));

        var request = """
					{
						"nome" : "%s",
						"email" : "%s",
						"senha" : "senha"
					}
				""".formatted(faker.name().fullName(), faker.internet().emailAddress());

        mockMvc.perform(post("/api/v1/usuario/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].tipo").value("senha"))
                .andExpect(jsonPath("$[0].mensagem").value(
                        "A senha deve conter pelo menos 8 caracteres e um de cada: letra minúscula, maiúscula, " +
                                "número, caracter especial."));
    }

    @Test
    @DisplayName("TS04 - Deve autenticar usuário com sucesso.")
    void deveAutenticarUsuarioComSucesso() throws Exception {

        var faker = new Faker(new Locale("pt-BR"));

        var nome = faker.name().fullName();
        var email = faker.internet().emailAddress();
        var senha = "@Admin123";

        var requestCriacao = """
					{
						"nome" : "%s",
						"email" : "%s",
						"senha" : "%s"
					}
				""".formatted(nome, email, senha);

        mockMvc.perform(post("/api/v1/usuario/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestCriacao))
                .andExpect(status().isCreated());

        var requestAutenticacao = """
					{
						"email" : "%s",
						"senha" : "%s"
					}
				""".formatted(email, senha);

        mockMvc.perform(post("/api/v1/usuario/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestAutenticacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Usuário autenticado com sucesso."))
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.perfil").value("Operador"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("TS05 - Não deve autenticar usuário com email e senha inválida.")
    void naoDeveAutenticarUsuarioComEmailESenhaInvalida() throws Exception {

        var faker = new Faker(new Locale("pt-BR"));

        var request = """
					{
						"email" : "%s",
						"senha" : "@Admin123"
					}
				""".formatted(faker.internet().emailAddress());

        mockMvc.perform(post("/api/v1/usuario/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.tipo").value("erro"))
                .andExpect(jsonPath("$.mensagem").value("Acesso negado. Usuário inválido."));
    }
}
