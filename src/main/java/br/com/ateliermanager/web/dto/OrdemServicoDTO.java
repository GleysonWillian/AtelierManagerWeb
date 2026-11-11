package br.com.ateliermanager.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrdemServicoDTO {

    private Integer idOrdem;
    private Integer idCliente;
    private String nomeCliente;
    private LocalDate dataEntrada;
    private LocalDate dataPrevistaEntrega;
    private BigDecimal valorTotal;
    private String observacoes;
    private String status;

    public OrdemServicoDTO(
            Integer idOrdem,
            Integer idCliente,
            String nomeCliente,
            LocalDate dataEntrada,
            LocalDate dataPrevistaEntrega,
            BigDecimal valorTotal,
            String observacoes,
            String status) {

        this.idOrdem = idOrdem;
        this.idCliente = idCliente;
        this.nomeCliente = nomeCliente;
        this.dataEntrada = dataEntrada;
        this.dataPrevistaEntrega = dataPrevistaEntrega;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
        this.status = status;
    }

    // Getters

    public Integer getIdOrdem() { return idOrdem; }
    public Integer getIdCliente() { return idCliente; }
    public String getNomeCliente() { return nomeCliente; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public LocalDate getDataPrevistaEntrega() { return dataPrevistaEntrega; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getObservacoes() { return observacoes; }
    public String getStatus() { return status; }
}