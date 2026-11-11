
package br.com.ateliermanager.web.repository;

import br.com.ateliermanager.web.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository
        extends JpaRepository<OrdemServico, Integer> {

    @EntityGraph(attributePaths = "cliente")
    @Query("SELECT os FROM OrdemServico os")
    List<OrdemServico> listarComCliente();

    @EntityGraph(attributePaths = "cliente")
    @Query("SELECT os FROM OrdemServico os WHERE os.idOrdem = :id")
    Optional<OrdemServico> buscarPorIdComCliente(@Param("id") Integer id);
}
