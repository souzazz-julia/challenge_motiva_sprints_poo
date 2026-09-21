package br.com.motiva;

import br.com.motiva.model.IntervencaoOperacional;
import br.com.motiva.model.MonitoravelViaIoT;
import br.com.motiva.model.TrechoRodovia;

import java.lang.reflect.Modifier;

// Testes executados com assertions nativas do Java. Rodar com: java -ea
public class Testes {

    public static void main(String[] args) {
        if (!Testes.class.desiredAssertionStatus()) {
            System.out.println("Assertions desligadas. Rode com a flag -ea.");
            return;
        }

        testarBaseNaoInstanciavel();
        testarMockIoT();

        System.out.println("Todos os testes passaram.");
    }

    // Sugestao 1: garantir a impossibilidade de instanciar a classe base abstrata.
    private static void testarBaseNaoInstanciavel() {
        assert Modifier.isAbstract(IntervencaoOperacional.class.getModifiers())
                : "IntervencaoOperacional deveria ser abstrata.";
        System.out.println("OK: IntervencaoOperacional e abstrata (nao pode usar new).");
    }

    // Sugestao 2: mock que implementa MonitoravelViaIoT e testa a captura de crescimento.
    private static void testarMockIoT() {
        TrechoRodovia trecho = new TrechoRodovia("MOCK KM 0 ao 1", 0.0, 1.0, 10.0);
        MonitoravelViaIoT sensorMock = crescimento -> trecho.registrarCrescimento(crescimento);

        sensorMock.transmitirDadosSensor(5.0);

        assert trecho.getNivelVegetacao() == 15.0
                : "Esperado 15.0 cm apos transmissao do sensor, obtido " + trecho.getNivelVegetacao();
        System.out.println("OK: mock IoT capturou crescimento (10 + 5 = 15 cm).");
    }
}
