package br.com.viduink.onze_api_autenticacao.services;

import br.com.viduink.onze_api_autenticacao.dtos.AutenticarUsuarioRequest;
import br.com.viduink.onze_api_autenticacao.dtos.AutenticarUsuarioResponse;
import br.com.viduink.onze_api_autenticacao.dtos.CriarUsuarioRequest;
import br.com.viduink.onze_api_autenticacao.dtos.CriarUsuarioResponse;
import br.com.viduink.onze_api_autenticacao.entities.Perfil;
import br.com.viduink.onze_api_autenticacao.entities.Usuario;
import br.com.viduink.onze_api_autenticacao.exceptions.AcessoNegadoException;
import br.com.viduink.onze_api_autenticacao.exceptions.EmailJaCadastradoException;
import br.com.viduink.onze_api_autenticacao.repositories.PerfilRepository;
import br.com.viduink.onze_api_autenticacao.repositories.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HexFormat;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    /*
        Método para implementaro fluxo de cadastro do usuário
     */
    public CriarUsuarioResponse criarUsuario(CriarUsuarioRequest request) {

        //Verificar se o email já está cadastrado no banco de dados
        if(usuarioRepository.findByEmail(request.email()) != null) {
            throw new EmailJaCadastradoException();
        }

        //Capturar os dados recebidos e criar uma instancia da classe de entidade
        var usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(criptografarSenha(request.senha()));

        //Todo usuário que se cadastrar será vinculado a um perfil chamado "Operador"
        var perfil = perfilRepository.findByNome("Operador");

        //Se o perfil não existir iremos cadastra-lo
        if(perfil == null) {
            perfil = new Perfil();
            perfil.setNome("Operador");
            perfilRepository.save(perfil);
        }

        //Associar o usuário ao perfil operador
        usuario.setPerfil(perfil);

        //Salvar o usuário no banco de dados
        usuarioRepository.save(usuario);

        //Retornar os dados do usuário
        return new CriarUsuarioResponse(
                "Usuário cadastrado com sucesso",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().getNome(),
                LocalDateTime.now()
        );
    }

    /*
        Método para implementaro fluxo de autenticação do usuário
     */
    public AutenticarUsuarioResponse autenticarUsuario(AutenticarUsuarioRequest request) {

        //Procurar o usuário no banco de dados através do email informado
        var usuario = usuarioRepository.findByEmail(request.email());

        //Verificar se o usuário foi encontrado e se a senha corresponse ao valor enviado no request
        if(usuario != null && usuario.getSenha().equals(criptografarSenha(request.senha()))) {

            //Retornar os dados do usuário autenticado
            return new AutenticarUsuarioResponse(
                    "Usuário autenticado com sucesso.",
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getPerfil().getNome(),
                    LocalDateTime.now(),
                    gerarToken(usuario)
            );
        }
        else {
            throw new AcessoNegadoException();
        }
    }

    /*
        Método privado auxiliar para fazer a criptografia da senha do usuário
     */
    private String criptografarSenha(String senha) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(senha.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível no ambiente Java.", e);
        }
    }

    //Metodo privado auxiliar para gerar um TOKEN JWT:

    private String gerarToken(Usuario usuario){
        //Chave para criptografar e assinar o token
        var chaveAssinatura = "c9237f2f-ca98-4966-a8b6-0513db8c6c21";

        //Gerar o Token JWT:
        Instant agora = Instant.now();
        Instant dataExpiracao = agora.plusMillis(3600000); //1 hora

        return Jwts.builder()
                .setSubject(usuario.getEmail()) //Usuário que gerou o TOKEN JWT
                .claim("role", usuario.getPerfil().getNome()) //Perfil do usuário
                .setIssuedAt(Date.from(agora)) //Data e hora de geração do TOKEN
                .setExpiration(Date.from(dataExpiracao)) //Data de exíração do token
                .signWith(SignatureAlgorithm.HS256, chaveAssinatura) //Chave secreta para criptografia
                .compact(); //Retornar o TOKEN
    }
}
