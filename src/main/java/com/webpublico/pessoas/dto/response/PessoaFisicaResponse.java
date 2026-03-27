package com.webpublico.pessoas.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
public class PessoaFisicaResponse {

    private Long id;
    private String tipo;
    private String nome;
    private String cpf;
    private String email;
    private List<EnderecoResponse> enderecos;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
