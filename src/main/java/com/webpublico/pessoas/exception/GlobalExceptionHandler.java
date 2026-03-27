package com.webpublico.pessoas.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PessoaNaoEncontradaException.class)
    public ResponseEntity<ApiError> handlePessoaNaoEncontrada(
            PessoaNaoEncontradaException ex, WebRequest request) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erro(HttpStatus.NOT_FOUND.getReasonPhrase())
                .mensagem(ex.getMessage())
                .path(extrairPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DocumentoDuplicadoException.class)
    public ResponseEntity<ApiError> handleDocumentoDuplicado(
            DocumentoDuplicadoException ex, WebRequest request) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erro(HttpStatus.CONFLICT.getReasonPhrase())
                .mensagem(ex.getMessage())
                .path(extrairPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TipoPessoaIncompativelException.class)
    public ResponseEntity<ApiError> handleTipoIncompativel(
            TipoPessoaIncompativelException ex, WebRequest request) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .erro(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .mensagem(ex.getMessage())
                .path(extrairPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<ApiError.CampoErro> camposErro = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toCampoErro)
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .erro(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .mensagem("Requisição contém campos inválidos.")
                .path(extrairPath(request))
                .campos(camposErro)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest request) {
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .erro(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .mensagem("Ocorreu um erro inesperado. Tente novamente mais tarde.")
                .path(extrairPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ApiError.CampoErro toCampoErro(FieldError fieldError) {
        return ApiError.CampoErro.builder()
                .campo(fieldError.getField())
                .mensagem(fieldError.getDefaultMessage())
                .build();
    }

    private String extrairPath(WebRequest request) {

        return request.getDescription(false).replace("uri=", "");
    }
}
