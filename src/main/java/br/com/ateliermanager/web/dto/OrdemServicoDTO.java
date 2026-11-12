
package br.com.ateliermanager.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrdemServicoDTO {

    private Integer idOrdem;
    private Integer idCliente;
    private String nomeCliente;
    private String telefoneCliente;
    private LocalDate dataEntrada;
    private LocalDate dataPrevistaEntrega;
    private BigDecimal valorTotal;
    private String observacoes;
    private String status;
    private String servicos;
    private List<ItemOrdemDTO> itens;

    public OrdemServicoDTO(
            Integer idOrdem,
            Integer idCliente,
            String nomeCliente,
            String telefoneCliente,
            LocalDate dataEntrada,
            LocalDate dataPrevistaEntrega,
            BigDecimal valorTotal,
            String observacoes,
            String status,
            String servicos,
            List<ItemOrdemDTO> itens) {

        this.idOrdem = idOrdem;
        this.idCliente = idCliente;
        this.nomeCliente = nomeCliente;
        this.telefoneCliente = telefoneCliente;
        this.dataEntrada = dataEntrada;
        this.dataPrevistaEntrega = dataPrevistaEntrega;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
        this.status = status;
        this.servicos = servicos;
        this.itens = itens;
    }

    public Integer getIdOrdem() { return idOrdem; }
    public Integer getIdCliente() { return idCliente; }
    public String getNomeCliente() { return nomeCliente; }
    public String getTelefoneCliente() { return telefoneCliente; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public LocalDate getDataPrevistaEntrega() { return dataPrevistaEntrega; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getObservacoes() { return observacoes; }
    public String getStatus() { return status; }
    public String getServicos() { return servicos; }
    public List<ItemOrdemDTO> getItens() { return itens; }
}