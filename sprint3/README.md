# Challenge Motiva — Sprint 3: Persistência com Oracle e JDBC

Evolução do sistema de monitoramento e priorização de roçada de vegetação nas
rodovias. Esta sprint adiciona persistência de dados em banco Oracle usando JDBC
puro, mantendo intacta toda a camada de domínio das Sprints 1 e 2.

## Estrutura do projeto

```
challenge-motiva-sprint3/
├── lib/                                  # coloque aqui o ojdbc17.jar
├── sql/
│   ├── seu-script-criacao.sql            # cria as tabelas
│   └── seu-script-dados.sql              # popula com dados de teste
└── src/br/com/motiva/
    ├── model/                            # domínio reaproveitado das Sprints 1/2
    ├── db/
    │   └── ConexaoBD.java                # Singleton de conexão
    ├── dao/
    │   ├── EquipeManutencaoDAO.java
    │   ├── TrechoRodoviaDAO.java
    │   ├── IntervencaoOperacionalDAO.java
    │   └── RelatorioPrioridadeDAO.java
    ├── service/
    │   └── GeradorRelatorio.java         # motor de prioridade + persistência
    └── main/
        └── Main.java                     # demonstra todas as operações
```

## Modelagem do banco

Cada hierarquia de classes é persistida em uma única tabela com uma coluna
discriminadora `TIPO`:

| Tabela                   | Classes de origem                              | Discriminador `TIPO`            |
|--------------------------|------------------------------------------------|---------------------------------|
| `EQUIPE_MANUTENCAO`      | EquipeManutencao, EquipeRocada                 | MANUTENCAO, ROCADA              |
| `TRECHO_RODOVIA`         | TrechoRodovia, TrechoUmido, TrechoSeco         | COMUM, UMIDO, SECO              |
| `INTERVENCAO_OPERACIONAL`| RocadaMecanizada, Pulverizacao                 | MECANIZADA, PULVERIZACAO        |
| `RELATORIO_PRIORIDADE`   | (nova) histórico dos relatórios gerados        | —                               |

`TRECHO_RODOVIA.EQUIPE_ID` é uma FK para `EQUIPE_MANUTENCAO`, refletindo o
atributo `equipeResponsavel` da classe `TrechoRodovia`.

## Pré-requisitos

- JDK 17 ou superior (o projeto usa `records` e `switch` de expressão)
- Um banco Oracle acessível (local ou do laboratório)
- Driver `ojdbc17.jar` dentro da pasta `lib/`

## Passo a passo de execução

### 1. Preparar o banco

Conecte no Oracle (SQL Developer, SQLcl ou sqlplus) com o seu usuário e rode,
nesta ordem:

```
@sql/seu-script-criacao.sql
@sql/seu-script-dados.sql
```

### 2. Configurar a conexão

As credenciais ficam em `ConexaoBD.java` com valores padrão para um Oracle local
(service `FREEPDB1`). Você pode editá-los diretamente ou sobrescrevê-los na
execução com `-D` (sem recompilar):

```
-Ddb.url="jdbc:oracle:thin:@//HOST:1521/SERVICE"
-Ddb.user="SEU_USUARIO"
-Ddb.password="SUA_SENHA"
```

### 3. Compilar

```
javac -d out $(find src -name "*.java")
```

No Windows (PowerShell):

```
javac -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
```

### 4. Executar

Linux/macOS:

```
java -cp "out:lib/ojdbc17.jar" br.com.motiva.main.Main
```

Windows (o separador de classpath é `;`):

```
java -cp "out;lib/ojdbc17.jar" br.com.motiva.main.Main
```

Passando credenciais na hora de rodar:

```
java -Ddb.url="jdbc:oracle:thin:@//localhost:1521/FREEPDB1" -Ddb.user="motiva" -Ddb.password="motiva" -cp "out:lib/ojdbc17.jar" br.com.motiva.main.Main
```

## Padrão DAO

Todos os DAOs seguem o mesmo padrão: construtor padrão; métodos `inserir`,
`buscarPorId`, `listarTodas`, `atualizar` e `deletar`; queries SQL como
constantes; `PreparedStatement` (sem concatenação de strings); e um `record`
representando a entidade. `ResultSet` e `PreparedStatement` são fechados no
`finally` de cada operação; a conexão é fechada uma única vez ao final, no `Main`.
