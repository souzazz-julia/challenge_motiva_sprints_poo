package br.com.motiva.model;

// Pulverizacao (controle quimico), indicada para vegetacao baixa/moderada.
// Nao zera a vegetacao: reduz seu nivel e inibe o crescimento.
public class Pulverizacao extends IntervencaoOperacional {

    private static final double REDUCAO = 0.5;

    public Pulverizacao(String responsavel) {
        super(responsavel);
    }

    @Override
    public void executarServico(TrechoRodovia trecho) {
        System.out.println("Pulverizacao por " + getResponsavel() + " no trecho " + trecho.getIdentificacao() + ".");
        double nivelReduzido = trecho.getNivelVegetacao() * (1 - REDUCAO);
        trecho.setNivelVegetacao(nivelReduzido);
        System.out.println("Crescimento controlado. Nivel reduzido para " + nivelReduzido + " cm.");
    }
}
