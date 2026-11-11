
package br.com.ateliermanager.web.controller;

import br.com.ateliermanager.web.dto.InsumoDTO;
import br.com.ateliermanager.web.dto.InsumoRequestDTO;
import br.com.ateliermanager.web.model.Insumo;
import br.com.ateliermanager.web.repository.InsumoRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    private final InsumoRepository insumoRepository;

    public InsumoController(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    // Listar todos os insumos
    @GetMapping
    public List<InsumoDTO> listar() {

        return insumoRepository.findAll().stream()
                .map(insumo -> new InsumoDTO(
                        insumo.getIdInsumo(),
                        insumo.getNome(),
                        insumo.getQuantidadeAtual(),
                        insumo.getUnidadeMedida(),
                        insumo.getEstoqueMinimo()
                ))
                .toList();
    }

    // Buscar insumo por ID
    @GetMapping("/{id}")
    public ResponseEntity<InsumoDTO> buscarPorId(@PathVariable Integer id) {

        return insumoRepository.findById(id)
                .map(insumo -> ResponseEntity.ok(new InsumoDTO(
                        insumo.getIdInsumo(),
                        insumo.getNome(),
                        insumo.getQuantidadeAtual(),
                        insumo.getUnidadeMedida(),
                        insumo.getEstoqueMinimo()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastrar insumo
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody InsumoRequestDTO dados) {

        Insumo insumo = new Insumo();

        insumo.setNome(dados.getNome());
        insumo.setQuantidadeAtual(dados.getQuantidadeAtual());
        insumo.setUnidadeMedida(dados.getUnidadeMedida());
        insumo.setEstoqueMinimo(dados.getEstoqueMinimo());

        Insumo insumoSalvo = insumoRepository.save(insumo);

        return ResponseEntity.ok(insumoSalvo.getIdInsumo());
    }

    // Atualizar insumo
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @RequestBody InsumoRequestDTO dados) {

        var insumoEncontrado = insumoRepository.findById(id);

        if (insumoEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Insumo insumo = insumoEncontrado.get();

        insumo.setNome(dados.getNome());
        insumo.setQuantidadeAtual(dados.getQuantidadeAtual());
        insumo.setUnidadeMedida(dados.getUnidadeMedida());
        insumo.setEstoqueMinimo(dados.getEstoqueMinimo());

        insumoRepository.save(insumo);

        return ResponseEntity.ok("Insumo atualizado com sucesso.");
    }

    // Excluir insumo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Integer id) {

        if (!insumoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        insumoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
