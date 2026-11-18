package br.com.viduink.onze_api_autenticacao.services;

import br.com.viduink.onze_api_autenticacao.dtos.AutenticarUsuarioRequest;
import br.com.viduink.onze_api_autenticacao.dtos.AutenticarUsuarioResponse;
import br.com.viduink.onze_api_autenticacao.dtos.CriarUsuarioRequest;
import br.com.viduink.onze_api_autenticacao.dtos.CriarUsuarioResponse;
import br.com.viduink.onze_api_autenticacao.entities.Usuario;
import br.com.viduink.onze_api_autenticacao.repositories.PerfilRepository;
import br.com.viduink.onze_api_autenticacao.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
    public class UsuarioService {

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private PerfilRepository perfilRepository;

        /*
            Metodo para implementar o fluxo de cadastro do usuário
         */
        public CriarUsuarioResponse criarUsuario(CriarUsuarioRequest request) {
            //Capturar os dados recebidos e criar uma instância da classe de entidade
            var usuario = new Usuario();
            usuario.setNome(request.nome());
            usuario.setEmail(request.email());
            usuario.setSenha(request.senha());
            //Salvar o usuário no banco de dados:
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
            Metodo para implementar o fluxo de autenticação do usuário
         */
        public AutenticarUsuarioResponse autenticarUsuario(AutenticarUsuarioRequest request) {
            //TODO
            return null;
        }
    }
