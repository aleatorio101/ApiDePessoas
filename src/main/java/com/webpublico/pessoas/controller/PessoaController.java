package com.webpublico.pessoas.controller;

import com.webpublico.pessoas.dto.response.PessoaSummaryResponse;
import com.webpublico.pessoas.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
@Tag(name = "Pessoas", description = "Operações compartilhadas entre Pessoas Físicas e Jurídicas")
public class PessoaController {

    private final PessoaService pessoaService;

    @Operation(summary = "Listar todas as pessoas (físicas e jurídicas)",
               description = "Retorna um resumo de todas as pessoas cadastradas. " +
                             "Para obter detalhes completos (incluindo endereços), " +
                             "consulte os endpoints específicos por tipo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<PessoaSummaryResponse>> listar() {
        return ResponseEntity.ok(pessoaService.listarTodas());
    }
}
