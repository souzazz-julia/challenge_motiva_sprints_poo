package br.com.motiva;

import br.com.motiva.model.IntervencaoOperacional;
import br.com.motiva.model.MotorDePrioridade;
import br.com.motiva.model.Pulverizacao;
import br.com.motiva.model.RocadaMecanizada;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.model.TrechoSeco;
import br.com.motiva.model.TrechoUmido;

public class App {

    public static void main(String[] args) {
        System.out.println("=== Sistema de Monitoramento de Vegetacao - Motiva (Sprint 2) ===\n");

        TrechoUmido trechoUmido = new TrechoUmido("BR-116 KM 10 ao 15", 10.0, 15.0, 20.0);
        TrechoSeco trechoSeco = new TrechoSeco("SP-348 KM 50 ao 60", 50.0, 60.0, 20.0);
        TrechoRodovia trechoComum = new TrechoRodovia("BR-101 KM 200 ao 210", 200.0, 210.0, 45.0);

        TrechoRodovia[] trechos = { trechoUmido, trechoSeco, trechoComum };

        System.out.println("--- Crescimento com a mesma taxa (10 cm), comportamentos diferentes ---");
        trechoUmido.transmitirDadosSensor(10.0);
        trechoSeco.registrarCrescimento(10.0);
        trechoComum.registrarCrescimento(10.0);
        for (TrechoRodovia trecho : trechos) {
            exibirTrecho(trecho);
        }

        System.out.println("\n--- Crescimento adicional para gerar cenario critico ---");
        trechoUmido.transmitirDadosSensor(20.0);
        trechoSeco.registrarCrescimento(20.0);
        trechoComum.registrarCrescimento(20.0);
        for (TrechoRodovia trecho : trechos) {
            exibirTrecho(trecho);
        }

        System.out.println();
        MotorDePrioridade motor = new MotorDePrioridade();
        motor.gerarRelatorio(trechos);

        System.out.println("\n--- Executando intervencoes (polimorfismo) ---");
        IntervencaoOperacional[] intervencoes = {
                new RocadaMecanizada("Equipe Alfa"),
                new Pulverizacao("Equipe Beta")
        };
        intervencoes[0].executarServico(trechoUmido);
        intervencoes[1].executarServico(trechoSeco);

        System.out.println("\n--- Relatorio final ---");
        motor.gerarRelatorio(trechos);
    }

    private static void exibirTrecho(TrechoRodovia trecho) {
        System.out.println(trecho.getIdentificacao() + " | Vegetacao: " + trecho.getNivelVegetacao() + " cm");
    }
}
