package br.com.motiva.service;

import br.com.motiva.dao.RelatorioPrioridadeDAO;
import br.com.motiva.model.TrechoRodovia;

/**
 * Evolucao do MotorDePrioridade da Sprint 2.
 * Alem de imprimir o relatorio no console, agora conta os trechos por categoria
 * e persiste um resumo no banco atraves do RelatorioPrioridadeDAO.
 */
public class GeradorRelatorio {

    private static final double LIMITE_MECANIZADA = 50.0;

    private final RelatorioPrioridadeDAO relatorioDAO = new RelatorioPrioridadeDAO();

    public void gerarRelatorio(TrechoRodovia[] trechos) {
        int qtMecanizada = 0;
        int qtManual = 0;
        int qtSemNecessidade = 0;

        System.out.println("=== Relatorio de Prioridade de Rocada ===");
        for (TrechoRodovia trecho : trechos) {
            String prioridade = definirPrioridade(trecho);
            System.out.println(trecho.getIdentificacao()
                    + " | Vegetacao: " + trecho.getNivelVegetacao() + " cm"
                    + " | Prioridade: " + prioridade);

            switch (prioridade) {
                case "ROCADA MECANIZADA" -> qtMecanizada++;
                case "ROCADA MANUAL" -> qtManual++;
                default -> qtSemNecessidade++;
            }
        }

        String resumo = "Mecanizada: " + qtMecanizada
                + " | Manual: " + qtManual
                + " | Sem necessidade: " + qtSemNecessidade;
        System.out.println("Resumo -> " + resumo);

        // NOVO na Sprint 3: salvar o resultado no banco.
        relatorioDAO.salvarRelatorio(qtMecanizada, qtManual, qtSemNecessidade, resumo);
        System.out.println("Relatorio salvo no historico do banco.");
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
