package br.com.motiva.model;

// Trecho em area umida: a vegetacao cresce mais rapido.
// Possui tecnologia IoT instalada, dispensando inspecao visual.
public class TrechoUmido extends TrechoRodovia implements MonitoravelViaIoT {

    private static final double FATOR_UMIDO = 1.5;

    public TrechoUmido(String identificacao, double quilometroInicial, double quilometroFinal, double nivelVegetacao) {
        super(identificacao, quilometroInicial, quilometroFinal, nivelVegetacao);
    }

    @Override
    protected double getFatorCrescimento() {
        return FATOR_UMIDO;
    }

    @Override
    public void transmitirDadosSensor(double crescimentoMedido) {
        registrarCrescimento(crescimentoMedido);
    }
}
