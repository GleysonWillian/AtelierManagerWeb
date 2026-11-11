
package br.com.ateliermanager.web.repository;

import br.com.ateliermanager.web.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

}
