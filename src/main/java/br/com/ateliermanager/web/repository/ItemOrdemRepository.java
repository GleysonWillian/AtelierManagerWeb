
package br.com.ateliermanager.web.repository;

import br.com.ateliermanager.web.model.ItemOrdem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOrdemRepository
        extends JpaRepository<ItemOrdem, Integer> {

    boolean existsByOrdemServico_IdOrdem(Integer idOrdem);
}
