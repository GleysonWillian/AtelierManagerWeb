package br.com.ateliermanager.web.dto;

import java.math.BigDecimal;

public class ItemOrdemDTO {

    private Integer idItem;
    private String tipoRoupa;
    private String servico;
    private String medidas;
    private String observacoes;
    private BigDecimal valor;

    public ItemOrdemDTO(
            Integer idItem,
            String tipoRoupa,
            String servico,
            String medidas,
            String observacoes,
            BigDecimal valor) {

        this.idItem = idItem;
        this.tipoRoupa = tipoRoupa;
        this.servico = servico;
        this.medidas = medidas;
        this.observacoes = observacoes;
        this.valor = valor;
    }

    public Integer getIdItem() { return idItem; }
    public String getTipoRoupa() { return tipoRoupa; }
    public String getServico() { return servico; }
    public String getMedidas() { return medidas; }
    public String getObservacoes() { return observacoes; }
    public BigDecimal getValor() { return valor; }
}