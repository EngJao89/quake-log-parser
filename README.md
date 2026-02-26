# Quake Log Parser

Programa em Java com Spring Boot que processa logs de partidas do jogo Quake 3 Arena. Lê um arquivo de log, identifica jogadores e mortes por partida e exibe um relatório em formato legível (total de kills por jogador, kills por meio de morte, etc.).

---

## Como rodar o projeto

### Pré-requisitos

- **Java 8** (ou superior)
- **Maven 3.x**

### Executando pela IDE

1. Configure a **classe principal** para: `com.joao.quakelogparser.psrcontrol.QuakeLogParserquakeLogParserApplication`
2. Execute a aplicação.
3. Quando o console pedir o caminho do arquivo, informe o path do log. Exemplo: `game.log` (se o arquivo estiver na raiz do projeto).

### Executando pelo Maven

```bash
# Compilar
mvn clean compile

# Executar (o Spring Boot pede o path do log no console)
mvn spring-boot:run -Dspring-boot.run.mainClass=com.joao.quakelogparser.psrcontrol.QuakeLogParserquakeLogParserApplication
```

Ou gerando o JAR e executando:

```bash
mvn clean package -DskipTests
java -jar target/quake-log-parser-0.0.1-SNAPSHOT.jar
```

Ao iniciar, o programa imprime no console: `Enter the log path:`. Digite o caminho do arquivo (ex.: `game.log` se estiver na pasta do projeto) e pressione Enter. O resultado será exibido em seguida.

### Testes

```bash
mvn test
```

---

## Descrição do projeto

O parser lê um arquivo de log no formato do Quake 3 Arena e:

- **Separa as partidas**: cada bloco que começa com `InitGame` é tratado como uma partida (game-1, game-2, …).
- **Extrai jogadores**: a partir de linhas `ClientUserinfoChanged`, obtém ID e nome dos jogadores.
- **Processa mortes**: em linhas `Kill`, identifica quem matou, quem morreu e o meio de morte (MOD_*, ex.: MOD_ROCKET, MOD_RAILGUN).
- **Diferencia mortes pelo mundo**: mortes causadas pelo `<world>` (queda, lava, etc.) reduzem o score do jogador que morreu.
- **Gera relatório**: por partida e globalmente, exibe total de kills, lista de jogadores, kills por jogador e kills por meio de morte.

O resultado é impresso no console em formato estruturado (ex.: JSON-like), com totais gerais e detalhes por partida.

---

## Decisões técnicas e arquiteturais

### Stack

- **Java 8**: linguagem base.
- **Spring Boot**: bootstrap da aplicação e dependências (JPA, Security, Web); a execução atual é via console, mas a stack permite evoluir para API REST ou uso de serviços.
- **Orientation a Objetos**: modelo de domínio (Game, Player, PlayerKillInfo, PlayerDeathInfo, DeathType, SingleGameLog) e responsabilidades bem definidas por classe.

### Organização do código (estilo MVC)

- **Model** (`com.joao.quakelogparser.model`): entidades de domínio — `Game`, `Player`, `PlayerKillInfo`, `PlayerDeathInfo`, `DeathType`, `SingleGameLog`. Sem lógica de I/O ou parsing.
- **Controle / orquestração** (`com.joao.quakelogparser.psrcontrol`): fluxo principal na aplicação; `Reader` (leitura do arquivo), `Printer` (saída do relatório). A classe principal coordena: ler → dividir em partidas → parsear → imprimir.
- **Componentes de parsing** (`psrcontrol.components`): `GameSplitter` (separa o log em partidas), `SingleGameParser` (interpreta linhas de cada partida e monta um `Game`). Padrões de linha (InitGame, ClientUserinfoChanged, Kill) concentrados em `GameReferences` com regex.

### Chain of Responsibility para mortes

O tratamento de cada linha de kill usa uma **cadeia de analisadores** (`AnalyzerChain`):

1. **WorldDeathAnalyzer**: se o “matador” for o mundo (ID 1022), aplica a penalidade no jogador que morreu (decremento de kill).
2. **DeathAnalyzer**: registra a morte do jogador (estatísticas por tipo de morte).
3. **KillAnalyzer**: registra a kill do jogador que matou.

Cada analisador decide se processa o evento e repassa (ou não) ao próximo. Isso mantém uma única responsabilidade por analisador e facilita incluir novos comportamentos (ex.: ignorar suicídios, regras especiais por modo de jogo).

### Imutabilidade e dados

- Objetos de modelo usam construtores e getters; listas e estruturas internas são preenchidas durante o parse e não expostas para alteração externa desnecessária.
- Uso de `Optional` e checagens de null onde faz sentido (ex.: último game no splitter, jogador por ID nos analisadores).

### Leitura e saída

- **Leitura**: `Reader` usa `Files.readAllLines(Path)`; em caso de falha retorna lista vazia e mensagem no console, mantendo o fluxo simples.
- **Saída**: `Printer` formata o relatório em texto (overall + por partida) no console; a formatação pode ser trocada depois (ex.: JSON, outro layout) sem alterar o modelo.

### Testes

- Testes unitários com JUnit (e suporte do Spring Boot Test onde aplicável) para validar o parser e a lógica de negócio (ex.: `GameSplitter`, `SingleGameParser`), garantindo que mudanças não quebrem o comportamento esperado.

---

## Arquitetura (visão geral)

```
┌─────────────────────────────────────────────────────────────────────────┐
│  QuakeLogParserquakeLogParserApplication (main)                           │
│  Orquestra: path → Reader → GameSplitter → SingleGameParser → Printer    │
└─────────────────────────────────────────────────────────────────────────┘
                                      │
         ┌────────────────────────────┼────────────────────────────┐
         ▼                            ▼                            ▼
┌─────────────────┐    ┌─────────────────────────────┐    ┌─────────────────┐
│ Reader           │    │ GameSplitter                │    │ Printer          │
│ readFile(path)   │    │ split(lines) → SingleGameLog│    │ printGames(games)│
└─────────────────┘    └──────────────┬──────────────┘    └─────────────────┘
                                      │
                                      ▼
                      ┌───────────────────────────────┐
                      │ SingleGameParser              │
                      │ parses(singleGameLogs) → Game │
                      │ - UserInfo (ClientUserinfo…)  │
                      │ - Kill (AnalyzerChain)        │
                      └───────────────┬───────────────┘
                                      │
                      ┌───────────────┴───────────────┐
                      ▼                               ▼
            ┌──────────────────┐           ┌─────────────────────┐
            │ GameReferences   │           │ AnalyzerChain        │
            │ (regex patterns) │           │ WorldDeath → Death   │
            └──────────────────┘           │ → Kill               │
                                          └─────────────────────┘
```

- **model**: `Game`, `Player`, `PlayerKillInfo`, `PlayerDeathInfo`, `DeathType`, `SingleGameLog`
- **psrcontrol**: aplicação, `Reader`, `Printer`
- **psrcontrol.components**: `GameSplitter`, `GameReferences`, `SingleGameParser`
- **psrcontrol.components.analyzers**: `AnalyzerChain`, `WorldDeathAnalyzer`, `DeathAnalyzer`, `KillAnalyzer`

---

## Estrutura de pastas (fontes)

```
src/main/java/com/joao/quakelogparser/
├── model/                    # Entidades de domínio
│   ├── Game.java
│   ├── Player.java
│   ├── SingleGameLog.java
│   ├── PlayerKillInfo.java
│   ├── PlayerDeathInfo.java
│   └── DeathType.java
└── psrcontrol/
    ├── QuakeLogParserquakeLogParserApplication.java
    ├── Reader.java
    ├── Printer.java
    └── components/
        ├── GameReferences.java
        ├── GameSplitter.java
        ├── SingleGameParser.java
        └── analyzers/
            ├── AnalyzerChain.java
            ├── WorldDeathAnalyzer.java
            ├── DeathAnalyzer.java
            └── KillAnalyzer.java
```

---

Documentação criada a partir do código existente. Para rodar com o log de exemplo na raiz do projeto: execute a aplicação e, quando solicitado, informe `game.log`.
