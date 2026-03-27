package com.webpublico.pessoas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class DocumentoDuplicadoException extends RuntimeException {

    public DocumentoDuplicadoException(String campo, String valor) {
        super(String.format("%s '%s' já está cadastrado.", campo, valor));
    }
}
