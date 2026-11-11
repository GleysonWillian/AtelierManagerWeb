package br.com.ateliermanager.web.controller;

import br.com.ateliermanager.web.dto.MovimentacaoEstoqueDTO;
import br.com.ateliermanager.web.dto.MovimentacaoEstoqueRequestDTO;
import br.com.ateliermanager.web.model.Insumo;
import br.com.ateliermanager.web.model.MovimentacaoEstoque;
import br.com.ateliermanager.web.repository.InsumoRepository;
import br.com.ateliermanager.web.repository.MovimentacaoEstoqueRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")

public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final InsumoRepository insumoRepository;

    public MovimentacaoEstoqueController(
            MovimentacaoEstoqueRepository movimentacaoRepository,
            InsumoRepository insumoRepository) {

        this.movimentacaoRepository = movimentacaoRepository;
        this.insumoRepository = insumoRepository;
    }

    // Listar histórico de movimentações
    @GetMapping
    public List<MovimentacaoEstoqueDTO> listar() {

        return movimentacaoRepository.listarComInsumo()
                .stream()
                .map(m -> new MovimentacaoEstoqueDTO(
                        m.getIdMovimentacao(),
                        m.getInsumo().getIdInsumo(),
                        m.getInsumo().getNome(),
                        m.getTipo(),
                        m.getQuantidade(),
                        m.getDataMovimentacao()
                ))
                .toList();
    }

    // Registrar entrada ou saída
    @PostMapping
    @Transactional
    public ResponseEntity<?> registrar(
            @RequestBody MovimentacaoEstoqueRequestDTO dados) {

        if (dados.getIdInsumo() == null
                || dados.getTipo() == null
                || dados.getQuantidade() == null) {

            return ResponseEntity.badRequest()
                    .body("Informe o material, o tipo e a quantidade.");
        }

        String tipo = dados.getTipo().trim();

        if (!tipo.equalsIgnoreCase("Entrada")
                && !tipo.equalsIgnoreCase("Saída")) {

            return ResponseEntity.badRequest()
                    .body("Tipo inválido. Utilize Entrada ou Saída.");
        }

        if (dados.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body("A quantidade deve ser maior que zero.");
        }

        Insumo insumo = insumoRepository
                .findById(dados.getIdInsumo())
                .orElse(null);

        if (insumo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Material não encontrado.");
        }

        BigDecimal quantidadeAtual = insumo.getQuantidadeAtual();
        BigDecimal quantidade = dados.getQuantidade();

        if (tipo.equalsIgnoreCase("Entrada")) {
            insumo.setQuantidadeAtual(
                    quantidadeAtual.add(quantidade)
            );
        } else {
            if (quantidade.compareTo(quantidadeAtual) > 0) {
                return ResponseEntity.badRequest()
                        .body("Estoque insuficiente para realizar a saída.");
            }

            insumo.setQuantidadeAtual(
                    quantidadeAtual.subtract(quantidade)
            );
        }

        insumoRepository.save(insumo);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setInsumo(insumo);
        movimentacao.setTipo(
                tipo.equalsIgnoreCase("Entrada") ? "Entrada" : "Saída");
        movimentacao.setQuantidade(quantidade);

        // A data será preenchida pelo SQL Server
        movimentacao.setDataMovimentacao(LocalDateTime.now());

        movimentacaoRepository.saveAndFlush(movimentacao);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MovimentacaoEstoqueDTO(
                        movimentacao.getIdMovimentacao(),
                        insumo.getIdInsumo(),
                        insumo.getNome(),
                        movimentacao.getTipo(),
                        movimentacao.getQuantidade(),
                        movimentacao.getDataMovimentacao()
                ));
    }
}
