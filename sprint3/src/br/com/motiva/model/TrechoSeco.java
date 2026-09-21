package br.com.motiva.model;

// Trecho em area seca: a vegetacao cresce mais devagar.
public class TrechoSeco extends TrechoRodovia {

    private static final double FATOR_SECO = 0.6;

    public TrechoSeco(String identificacao, double quilometroInicial, double quilometroFinal, double nivelVegetacao) {
        super(identificacao, quilometroInicial, quilometroFinal, nivelVegetacao);
    }

    @Override
    protected double getFatorCrescimento() {
        return FATOR_SECO;
    }
}
