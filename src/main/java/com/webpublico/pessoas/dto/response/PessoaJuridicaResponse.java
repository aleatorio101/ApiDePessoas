package com.webpublico.pessoas.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PessoaJuridicaResponse {

    private Long id;
    private String tipo;
    private String razaoSocial;
    private String cnpj;
    private String email;
    private List<EnderecoResponse> enderecos;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
