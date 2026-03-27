package com.webpublico.pessoas.controller;

import com.webpublico.pessoas.dto.request.PessoaFisicaRequest;
import com.webpublico.pessoas.dto.response.PessoaFisicaResponse;
import com.webpublico.pessoas.exception.ApiError;
import com.webpublico.pessoas.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;


@RestController
@RequestMapping("/pessoas/fisicas")
@RequiredArgsConstructor
@Tag(name = "Pessoas Físicas", description = "Operações de CRUD para Pessoas Físicas")
public class PessoaFisicaController {

    private final PessoaService pessoaService;

    @Operation(summary = "Cadastrar pessoa física")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pessoa física cadastrada com sucesso"),
        @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<PessoaFisicaResponse> cadastrar(
            @Valid @RequestBody PessoaFisicaRequest request) {

        PessoaFisicaResponse response = pessoaService.cadastrarPessoaFisica(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Buscar pessoa física por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa física encontrada"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "422", description = "ID pertence a uma Pessoa Jurídica",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PessoaFisicaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pessoaService.buscarPessoaFisica(id));
    }

    @Operation(summary = "Atualizar pessoa física")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa física atualizada"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos ou tipo incompatível",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PessoaFisicaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaFisicaRequest request) {

        return ResponseEntity.ok(pessoaService.atualizarPessoaFisica(id, request));
    }

    @Operation(summary = "Remover pessoa física")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pessoa removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        pessoaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
