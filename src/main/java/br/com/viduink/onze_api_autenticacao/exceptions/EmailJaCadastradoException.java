package br.com.viduink.onze_api_autenticacao.exceptions;

public class EmailJaCadastradoException extends RuntimeException {

    @Override
    public String getMessage() {
        return "O email informado já está cadastrado. Tente outro.";
    }
}