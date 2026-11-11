package br.com.ateliermanager.web.dto;

import java.math.BigDecimal;

public class InsumoDTO {

    private Integer idInsumo;
    private String nome;
    private BigDecimal quantidadeAtual;
    private String unidadeMedida;
    private BigDecimal estoqueMinimo;

    public InsumoDTO(Integer idInsumo, String nome,
                     BigDecimal quantidadeAtual, String unidadeMedida,
                     BigDecimal estoqueMinimo) {
        this.idInsumo = idInsumo;
        this.nome = nome;
        this.quantidadeAtual = quantidadeAtual;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMinimo = estoqueMinimo;
    }

    public Integer getIdInsumo() {
        return idInsumo;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo;
    }
}
