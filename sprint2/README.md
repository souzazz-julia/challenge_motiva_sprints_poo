# Challenge Motiva — Sprint 2: O Motor de Regras

Sistema de monitoramento e priorização de roçada de vegetação nas rodovias.
Esta Sprint adiciona inteligência ao domínio base da Sprint 1: comportamentos
de crescimento distintos, tipos de intervenção e um motor que gera um Relatório
de Prioridade automático a partir de um array de trechos.

## Estrutura

```
src/br/com/motiva/
├── App.java                      # Protótipo de console
├── Testes.java                   # Testes com assertions nativas
└── model/
    ├── TrechoRodovia.java        # Domínio base (Sprint 1)
    ├── TrechoUmido.java          # Cresce mais rápido + sensor IoT
    ├── TrechoSeco.java           # Cresce mais devagar
    ├── MonitoravelViaIoT.java    # Interface de transmissão de dados
    ├── IntervencaoOperacional.java  # Classe abstrata base
    ├── RocadaMecanizada.java     # Intervenção: corte mecânico
    ├── Pulverizacao.java         # Intervenção: controle químico
    ├── MotorDePrioridade.java    # Gera o Relatório de Prioridade
    ├── EquipeManutencao.java     # Sprint 1
    └── EquipeRocada.java         # Sprint 1
```

## Como executar

Compilar:

```
javac -d out $(find src -name "*.java")
```

Rodar o protótipo:

```
java -cp out br.com.motiva.App
```

Rodar os testes (a flag `-ea` habilita as assertions):

```
java -ea -cp out br.com.motiva.Testes
```

## Decisões de modelagem

- **Comportamentos de crescimento** foram modelados como subclasses de
  `TrechoRodovia` (`TrechoUmido`, `TrechoSeco`) que sobrescrevem o fator de
  crescimento. Assim o mesmo método `registrarCrescimento(taxa)` produz
  resultados diferentes por polimorfismo, sem alterar a regra de validação já
  existente na classe base.
- **Tipos de intervenção** derivam de `IntervencaoOperacional` (abstrata).
  `RocadaMecanizada` remove toda a vegetação; `Pulverizacao` reduz parcialmente.
- **Ator x ação**: o sistema separa quem executa de o que é executado.
  `EquipeManutencao` (e sua especialização `EquipeRocada`) representa o *ator* —
  a equipe que vai a campo; `IntervencaoOperacional` representa a *ação* — o tipo
  de serviço realizado no trecho. Por isso `EquipeRocada` delega a execução a uma
  `RocadaMecanizada`, em vez de repetir a lógica.
- **Motor de regras**: `MotorDePrioridade` varre o array de trechos e classifica
  cada KM em `ROCADA MECANIZADA` (vegetação ≥ 50 cm), `ROCADA MANUAL`
  (≥ 30 cm e < 50 cm) ou `SEM NECESSIDADE` (< 30 cm).

## Perguntas de Reflexão

### 1. Por que não faz sentido para a Motiva que uma equipe execute apenas uma "Intervenção Operacional" genérica sem especificar qual é?

Uma "Intervenção Operacional" genérica não descreve um trabalho real executável:
não define equipamento, técnica nem efeito sobre a vegetação. No campo, a equipe
sempre realiza uma ação concreta — uma roçada mecanizada ou uma pulverização —,
cada uma com recursos e resultados próprios. Por isso a classe base é abstrata:
ela fixa o contrato comum (`executarServico`), mas obriga que toda intervenção
real seja de um tipo específico, evitando objetos sem comportamento definido.

### 2. Qual a diferença arquitetural entre fazer um Trecho herdar de uma classe abstrata vs. implementar uma Interface?

A herança (`extends`) expressa uma relação "é um tipo de": `TrechoUmido` é um
`TrechoRodovia` e reaproveita seu estado e comportamento, especializando apenas o
crescimento. Já a interface (`implements`) expressa uma capacidade independente da
hierarquia: `MonitoravelViaIoT` define que o trecho consegue transmitir dados de
sensor, sem dizer o que ele é. Um trecho pode ter sensor ou não, mesmo sendo do
mesmo tipo. Herança define identidade e é única; interfaces adicionam contratos de
comportamento e podem ser várias, mantendo o sistema desacoplado.


