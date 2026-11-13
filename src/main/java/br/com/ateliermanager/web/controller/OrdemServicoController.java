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
import br.com.ateliermanager.web.dto.ItemOrdemRequestDTO;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

import java.util.Map;


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
    @Transactional
    public ResponseEntity<?> cadastrar(
            @RequestBody OrdemServicoRequestDTO dados) {

        if (dados.getItens() == null || dados.getItens().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A ordem deve possuir pelo menos uma peça.");
        }

        // Busca o cliente pelo ID informado
        var cliente = clienteRepository.findById(dados.getIdCliente());

        if (cliente.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Cliente não encontrado.");
        }

        for (var item : dados.getItens()) {

            if (item.getTipoRoupa() == null
                    || item.getTipoRoupa().isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Informe o tipo de roupa de todas as peças.");
            }

            if (item.getServico() == null
                    || item.getServico().isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Informe o serviço de todas as peças.");
            }

            if (item.getValor() == null
                    || item.getValor().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest()
                        .body("O valor de cada peça deve ser informado e não pode ser negativo.");
            }
        }

        // Cria a ordem de serviço
        OrdemServico ordem = new OrdemServico();

        ordem.setCliente(cliente.get());
        ordem.setDataEntrada(dados.getDataEntrada());
        ordem.setDataPrevistaEntrega(dados.getDataPrevistaEntrega());
        BigDecimal valorTotal = dados.getItens()
                .stream()
                .map(item -> item.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ordem.setValorTotal(valorTotal);
        ordem.setObservacoes(dados.getObservacoes());
        ordem.setStatus(
                dados.getStatus() == null || dados.getStatus().isBlank()
                        ? "Pendente"
                        : dados.getStatus()
        );

        // Salva no banco
        OrdemServico ordemSalva = ordemServicoRepository.save(ordem);

        for (var itemDTO : dados.getItens()) {

            ItemOrdem item = new ItemOrdem();

            item.setOrdemServico(ordemSalva);
            item.setTipoRoupa(itemDTO.getTipoRoupa());
            item.setServico(itemDTO.getServico());
            item.setMedidas(itemDTO.getMedidas());
            item.setObservacoes(itemDTO.getObservacoes());
            item.setValor(itemDTO.getValor());

            itemOrdemRepository.save(item);
        }

        return ResponseEntity.ok(ordemSalva.getIdOrdem());
    }

    // Atualizar ordem de serviço

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @RequestBody OrdemServicoRequestDTO dados) {

        // Busca a ordem de serviço
        var ordemEncontrada = ordemServicoRepository.findById(id);

        if (ordemEncontrada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Valida os itens recebidos
        if (dados.getItens() == null || dados.getItens().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A ordem deve possuir pelo menos uma peça.");
        }

        for (ItemOrdemRequestDTO item : dados.getItens()) {
            if (item.getTipoRoupa() == null || item.getTipoRoupa().isBlank()
                    || item.getServico() == null || item.getServico().isBlank()
                    || item.getValor() == null
                    || item.getValor().compareTo(BigDecimal.ZERO) < 0) {

                return ResponseEntity.badRequest()
                        .body("Verifique os dados das peças da ordem.");
            }
        }

        // Busca o cliente informado
        var cliente = clienteRepository.findById(dados.getIdCliente());

        if (cliente.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Cliente não encontrado.");
        }

        OrdemServico ordem = ordemEncontrada.get();

        // Busca as peças já cadastradas
        List<ItemOrdem> itensExistentes =
                itemOrdemRepository.findByOrdemServico_IdOrdem(id);

        // Identifica os IDs recebidos
        List<Integer> idsRecebidos = dados.getItens()
                .stream()
                .map(ItemOrdemRequestDTO::getIdItem)
                .filter(itemId -> itemId != null)
                .toList();

        // Verifica se todos os IDs pertencem a esta ordem
        for (Integer idItem : idsRecebidos) {
            boolean pertenceAOrdem = itensExistentes.stream()
                    .anyMatch(item -> item.getIdItem().equals(idItem));

            if (!pertenceAOrdem) {
                return ResponseEntity.badRequest()
                        .body("Peça não encontrada nesta ordem de serviço.");
            }
        }

        // Atualiza os dados gerais da ordem
        ordem.setCliente(cliente.get());
        ordem.setDataEntrada(dados.getDataEntrada());
        ordem.setDataPrevistaEntrega(dados.getDataPrevistaEntrega());
        ordem.setObservacoes(dados.getObservacoes());
        ordem.setStatus(dados.getStatus());

        // Calcula o valor total das peças
        BigDecimal valorTotal = dados.getItens()
                .stream()
                .map(item -> item.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ordem.setValorTotal(valorTotal);

        ordemServicoRepository.save(ordem);

        // Atualiza peças existentes e cadastra novas
        for (ItemOrdemRequestDTO itemDTO : dados.getItens()) {

            ItemOrdem item;

            if (itemDTO.getIdItem() == null) {
                item = new ItemOrdem();
                item.setOrdemServico(ordem);
            } else {
                item = itensExistentes.stream()
                        .filter(i -> i.getIdItem().equals(itemDTO.getIdItem()))
                        .findFirst()
                        .orElseThrow();
            }

            item.setTipoRoupa(itemDTO.getTipoRoupa());
            item.setServico(itemDTO.getServico());
            item.setMedidas(itemDTO.getMedidas());
            item.setObservacoes(itemDTO.getObservacoes());
            item.setValor(itemDTO.getValor());

            itemOrdemRepository.save(item);
        }

        // Exclui as peças removidas do formulário
        List<ItemOrdem> itensRemovidos = itensExistentes.stream()
                .filter(item -> !idsRecebidos.contains(item.getIdItem()))
                .toList();

        itemOrdemRepository.deleteAll(itensRemovidos);

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
