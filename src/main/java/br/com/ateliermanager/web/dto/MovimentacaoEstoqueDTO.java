package br.com.ateliermanager.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoEstoqueDTO {

    private Integer idMovimentacao;
    private Integer idInsumo;
    private String nomeInsumo;
    private String tipo;
    private BigDecimal quantidade;
    private LocalDateTime dataMovimentacao;

    public MovimentacaoEstoqueDTO(
            Integer idMovimentacao,
            Integer idInsumo,
            String nomeInsumo,
            String tipo,
            BigDecimal quantidade,
            LocalDateTime dataMovimentacao) {

        this.idMovimentacao = idMovimentacao;
        this.idInsumo = idInsumo;
        this.nomeInsumo = nomeInsumo;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataMovimentacao = dataMovimentacao;
    }

    public Integer getIdMovimentacao() {
        return idMovimentacao;
    }

    public Integer getIdInsumo() {
        return idInsumo;
    }

    public String getNomeInsumo() {
        return nomeInsumo;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }
}
