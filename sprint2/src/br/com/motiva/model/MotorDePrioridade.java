package br.com.motiva.model;

// Motor de regras: varre os trechos e decide a prioridade de intervencao.
// Vegetacao muito alta exige rocada mecanizada; alta porem menor pede manual;
// abaixo do limite critico nao requer intervencao.
public class MotorDePrioridade {

    private static final double LIMITE_MECANIZADA = 50.0;

    public void gerarRelatorio(TrechoRodovia[] trechos) {
        System.out.println("=== Relatorio de Prioridade de Rocada ===");
        for (TrechoRodovia trecho : trechos) {
            System.out.println(trecho.getIdentificacao()
                    + " | Vegetacao: " + trecho.getNivelVegetacao() + " cm"
                    + " | Prioridade: " + definirPrioridade(trecho));
        }
    }

    private String definirPrioridade(TrechoRodovia trecho) {
        if (!trecho.precisaRocada()) {
            return "SEM NECESSIDADE";
        }
        if (trecho.getNivelVegetacao() >= LIMITE_MECANIZADA) {
            return "ROCADA MECANIZADA";
        }
        return "ROCADA MANUAL";
    }
}
