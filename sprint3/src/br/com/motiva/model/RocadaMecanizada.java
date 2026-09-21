package br.com.motiva.model;

// Corte mecanizado, indicado para vegetacao alta. Remove toda a vegetacao do trecho.
public class RocadaMecanizada extends IntervencaoOperacional {

    public RocadaMecanizada(String responsavel) {
        super(responsavel);
    }

    @Override
    public void executarServico(TrechoRodovia trecho) {
        System.out.println("Rocada mecanizada por " + getResponsavel() + " no trecho " + trecho.getIdentificacao() + ".");
        trecho.setNivelVegetacao(0);
        System.out.println("Vegetacao removida. Nivel zerado.");
    }
}
