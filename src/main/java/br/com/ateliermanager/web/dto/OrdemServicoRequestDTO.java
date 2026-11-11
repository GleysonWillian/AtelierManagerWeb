package br.com.ateliermanager.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrdemServicoRequestDTO {

    private Integer idCliente;
    private LocalDate dataEntrada;
    private LocalDate dataPrevistaEntrega;
    private BigDecimal valorTotal;
    private String observacoes;
    private String status;

    public Integer getIdCliente() {
        return idCliente;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataPrevistaEntrega() {
        return dataPrevistaEntrega;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getStatus() {
        return status;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public void setDataPrevistaEntrega(LocalDate dataPrevistaEntrega) {
        this.dataPrevistaEntrega = dataPrevistaEntrega;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}