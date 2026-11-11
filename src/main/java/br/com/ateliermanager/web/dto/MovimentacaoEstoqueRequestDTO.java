package br.com.ateliermanager.web.dto;

import java.math.BigDecimal;

public class MovimentacaoEstoqueRequestDTO {

    private Integer idInsumo;
    private String tipo;
    private BigDecimal quantidade;

    public Integer getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(Integer idInsumo) {
        this.idInsumo = idInsumo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}
