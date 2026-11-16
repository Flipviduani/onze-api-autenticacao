package br.com.viduink.onze_api_autenticacao.entities;

import lombok.Data;

@Data
public class Usuario {

    private Integer id;
    private String nome;
    private String email;
    private String senha;
    private Perfil perfil;
}
