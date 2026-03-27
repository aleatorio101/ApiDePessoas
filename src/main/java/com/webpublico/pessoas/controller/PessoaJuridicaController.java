package com.webpublico.pessoas.controller;

import com.webpublico.pessoas.dto.request.PessoaJuridicaRequest;
import com.webpublico.pessoas.dto.response.PessoaJuridicaResponse;
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
@RequestMapping("/pessoas/juridicas")
@RequiredArgsConstructor
@Tag(name = "Pessoas Jurídicas", description = "Operações de CRUD para Pessoas Jurídicas")
public class PessoaJuridicaController {

    private final PessoaService pessoaService;

    @Operation(summary = "Cadastrar pessoa jurídica")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pessoa jurídica cadastrada com sucesso"),
        @ApiResponse(responseCode = "409", description = "CNPJ ou e-mail já cadastrado",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<PessoaJuridicaResponse> cadastrar(
            @Valid @RequestBody PessoaJuridicaRequest request) {

        PessoaJuridicaResponse response = pessoaService.cadastrarPessoaJuridica(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Buscar pessoa jurídica por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa jurídica encontrada"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "422", description = "ID pertence a uma Pessoa Física",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PessoaJuridicaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pessoaService.buscarPessoaJuridica(id));
    }

    @Operation(summary = "Atualizar pessoa jurídica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa jurídica atualizada"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "409", description = "CNPJ ou e-mail já cadastrado",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos ou tipo incompatível",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PessoaJuridicaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaJuridicaRequest request) {

        return ResponseEntity.ok(pessoaService.atualizarPessoaJuridica(id, request));
    }

    @Operation(summary = "Remover pessoa jurídica")
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
