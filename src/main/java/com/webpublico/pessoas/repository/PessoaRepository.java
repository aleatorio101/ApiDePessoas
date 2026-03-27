package com.webpublico.pessoas.repository;

import com.webpublico.pessoas.domain.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {


    @Query("SELECT p FROM Pessoa p LEFT JOIN FETCH p.enderecos WHERE p.id = :id")
    Optional<Pessoa> buscarPorIdComEnderecos(@Param("id") Long id);


    @Query("SELECT CASE WHEN COUNT(pf) > 0 THEN TRUE ELSE FALSE END " +
           "FROM PessoaFisica pf WHERE pf.cpf = :cpf AND (:id IS NULL OR pf.id <> :id)")
    boolean existeCpf(@Param("cpf") String cpf, @Param("id") Long id);


    @Query("SELECT CASE WHEN COUNT(pj) > 0 THEN TRUE ELSE FALSE END " +
           "FROM PessoaJuridica pj WHERE pj.cnpj = :cnpj AND (:id IS NULL OR pj.id <> :id)")
    boolean existeCnpj(@Param("cnpj") String cnpj, @Param("id") Long id);


    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Pessoa p WHERE p.email = :email AND (:id IS NULL OR p.id <> :id)")
    boolean existeEmail(@Param("email") String email, @Param("id") Long id);


    @Query("SELECT p FROM Pessoa p ORDER BY p.id ASC")
    List<Pessoa> listarTodas();
}
