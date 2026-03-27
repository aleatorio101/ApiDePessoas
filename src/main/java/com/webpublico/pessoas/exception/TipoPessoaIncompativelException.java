package com.webpublico.pessoas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class TipoPessoaIncompativelException extends RuntimeException {

    public TipoPessoaIncompativelException(Long id, String tipoEsperado) {
        super(String.format(
                "Pessoa com id %d não é do tipo %s.", id, tipoEsperado));
    }
}
