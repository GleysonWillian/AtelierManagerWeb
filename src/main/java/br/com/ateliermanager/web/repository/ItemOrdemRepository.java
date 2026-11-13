package br.com.ateliermanager.web.repository;

import br.com.ateliermanager.web.model.ItemOrdem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemOrdemRepository
        extends JpaRepository<ItemOrdem, Integer> {

    boolean existsByOrdemServico_IdOrdem(Integer idOrdem);

    List<ItemOrdem> findByOrdemServico_IdOrdem(Integer idOrdem);
}