package br.com.ateliermanager.web.controller;
import br.com.ateliermanager.web.model.OrdemServico;
import br.com.ateliermanager.web.repository.OrdemServicoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.ateliermanager.web.dto.OrdemServicoDTO;
import br.com.ateliermanager.web.dto.OrdemServicoRequestDTO;
import br.com.ateliermanager.web.repository.ClienteRepository;
import br.com.ateliermanager.web.repository.ItemOrdemRepository;
import br.com.ateliermanager.web.model.ItemOrdem;
import br.com.ateliermanager.web.dto.ItemOrdemDTO;


import java.util.List;

@RestController
@RequestMapping("/api/ordens")
public class OrdemServicoController {

    private final OrdemServicoRepository ordemServicoRepository;

    private final ClienteRepository clienteRepository;

    private final ItemOrdemRepository itemOrdemRepository;

    public  OrdemServicoController(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            ItemOrdemRepository itemOrdemRepository) {

        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.itemOrdemRepository = itemOrdemRepository;
    }



    @GetMapping
    public List<OrdemServicoDTO> listar() {

        return ordemServicoRepository.listarComCliente()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(
            @PathVariable Integer id) {

        return ordemServicoRepository.buscarPorIdComCliente(id)
                .map(ordem -> ResponseEntity.ok(converterParaDTO(ordem)))
                .orElse(ResponseEntity.notFound().build());
    }

    private OrdemServicoDTO converterParaDTO(OrdemServico ordem) {

        List<ItemOrdemDTO> itens = itemOrdemRepository
                .findByOrdemServico_IdOrdem(ordem.getIdOrdem())
                .stream()
                .map(item -> new ItemOrdemDTO(
                        item.getIdItem(),
                        item.getTipoRoupa(),
                        item.getServico(),
                        item.getMedidas(),
                        item.getObservacoes(),
                        item.getValor()
                ))
                .toList();

        String servicos = itens.stream()
                .map(ItemOrdemDTO::getServico)
                .distinct()
                .collect(java.util.stream.Collectors.joining(", "));

        return new OrdemServicoDTO(
                ordem.getIdOrdem(),
                ordem.getCliente().getIdCliente(),
                ordem.getCliente().getNome(),
                ordem.getCliente().getTelefone(),
                ordem.getDataEntrada(),
                ordem.getDataPrevistaEntrega(),
                ordem.getValorTotal(),
                ordem.getObservacoes(),
                ordem.getStatus(),
                servicos,
                itens
        );
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody OrdemServicoRequestDTO dados) {

        // Busca o cliente pelo ID informado
        var cliente = clienteRepository.findById(dados.getIdCliente());

        if (cliente.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Cliente não encontrado.");
        }

        // Cria a ordem de serviço
        OrdemServico ordem = new OrdemServico();

        ordem.setCliente(cliente.get());
        ordem.setDataEntrada(dados.getDataEntrada());
        ordem.setDataPrevistaEntrega(dados.getDataPrevistaEntrega());
        ordem.setValorTotal(dados.getValorTotal());
        ordem.setObservacoes(dados.getObservacoes());
        ordem.setStatus(
                dados.getStatus() == null || dados.getStatus().isBlank()
                        ? "Pendente"
                        : dados.getStatus()
        );

        // Salva no banco
        OrdemServico ordemSalva = ordemServicoRepository.save(ordem);

        return ResponseEntity.ok(ordemSalva.getIdOrdem());
    }

    // Atualizar ordem de serviço
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @RequestBody OrdemServicoRequestDTO dados) {

        // Busca a ordem de serviço
        var ordemEncontrada = ordemServicoRepository.findById(id);

        if (ordemEncontrada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Busca o cliente informado
        var cliente = clienteRepository.findById(dados.getIdCliente());

        if (cliente.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Cliente não encontrado.");
        }

        // Atualiza os dados da ordem
        OrdemServico ordem = ordemEncontrada.get();

        ordem.setCliente(cliente.get());
        ordem.setDataEntrada(dados.getDataEntrada());
        ordem.setDataPrevistaEntrega(dados.getDataPrevistaEntrega());
        ordem.setValorTotal(dados.getValorTotal());
        ordem.setObservacoes(dados.getObservacoes());
        ordem.setStatus(dados.getStatus());

        ordemServicoRepository.save(ordem);

        return ResponseEntity.ok("Ordem de serviço atualizada com sucesso.");
    }

    // Excluir ordem de serviço
    // Excluir ordem de serviço
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Integer id) {

        // Verifica se a ordem existe
        if (!ordemServicoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Verifica se existem itens associados
        if (itemOrdemRepository.existsByOrdemServico_IdOrdem(id)) {
            return ResponseEntity.status(409)
                    .body("Não é possível excluir esta ordem, pois existem itens associados.");
        }

        // Exclui a ordem
        ordemServicoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
