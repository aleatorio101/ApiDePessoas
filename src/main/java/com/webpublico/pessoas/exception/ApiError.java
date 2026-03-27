package com.webpublico.pessoas.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;
    private String erro;
    private String mensagem;
    private String path;

    private List<CampoErro> campos;

    @Getter
    @Builder
    public static class CampoErro {
        private String campo;
        private String mensagem;
    }
}
