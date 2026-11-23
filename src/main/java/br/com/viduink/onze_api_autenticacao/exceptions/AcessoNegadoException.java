package br.com.viduink.onze_api_autenticacao.exceptions;

public class AcessoNegadoException extends RuntimeException {

    //Método para retornar uma mensagem de erro padrão..
    @Override
    public String getMessage() {
        return "Acesso negado. Usuário inválido.";
    }
}