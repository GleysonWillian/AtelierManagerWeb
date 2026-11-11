package br.com.ateliermanager.web.repository;

import br.com.ateliermanager.web.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovimentacaoEstoqueRepository
        extends JpaRepository<MovimentacaoEstoque, Integer> {

    @EntityGraph(attributePaths = "insumo")
    @Query("SELECT m FROM MovimentacaoEstoque m")
    List<MovimentacaoEstoque> listarComInsumo();
}