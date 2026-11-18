package br.com.viduink.onze_api_autenticacao.repositories;

import br.com.viduink.onze_api_autenticacao.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {

    Perfil findByNome(String nome);
}
