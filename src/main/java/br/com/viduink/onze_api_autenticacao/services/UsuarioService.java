package br.com.viduink.onze_api_autenticacao.services;

import br.com.viduink.onze_api_autenticacao.dtos.CriarUsuarioRequest;
import br.com.viduink.onze_api_autenticacao.dtos.CriarUsuarioResponse;
import br.com.viduink.onze_api_autenticacao.entities.Perfil;
import br.com.viduink.onze_api_autenticacao.entities.Usuario;
import br.com.viduink.onze_api_autenticacao.exceptions.EmailJaCadastradoException;
import br.com.viduink.onze_api_autenticacao.repositories.PerfilRepository;
import br.com.viduink.onze_api_autenticacao.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    /*
        Metodo para implementaro fluxo de cadastro do usuário
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
        Metodo para implementaro fluxo de autenticação do usuário
     */
    public void autenticarUsuario() {

    }

    /*
        Metodo privado auxiliar para fazer a criptografia da senha do usuário
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
}