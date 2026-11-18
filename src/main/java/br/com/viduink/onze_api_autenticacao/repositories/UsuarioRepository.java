package br.com.viduink.onze_api_autenticacao.repositories;

import br.com.viduink.onze_api_autenticacao.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByEmail(String email);
}
