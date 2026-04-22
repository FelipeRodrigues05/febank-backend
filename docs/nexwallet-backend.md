# febank-backend — Registro de Melhorias

> Data: 2026-04-13  
> Branch: `development`

---

## Sumário

1. [Bugs Críticos](#1-bugs-críticos)
2. [Segurança](#2-segurança)
3. [Validações nos DTOs](#3-validações-nos-dtos)
4. [Exception Handling](#4-exception-handling)
5. [Logging](#5-logging)
6. [Qualidade de Código](#6-qualidade-de-código)
7. [Docker e Hot Reload](#7-docker-e-hot-reload)
8. [Tabela Geral de Arquivos Alterados](#8-tabela-geral-de-arquivos-alterados)

---

## 1. Bugs Críticos

### 1.1 `CardService.validateCard()` — lógica invertida

**Problema:** A condição verificava se o cartão **estava ativo** para lançar a exceção `CardNotActiveException`, ou seja, cartões bloqueados/cancelados passavam livremente.

```java
// ANTES (errado)
if (card.getCardStatus().isUsable()) throw new CardNotActiveException("Card is not active.");

// DEPOIS (correto)
if (!card.getCardStatus().isUsable()) throw new CardNotActiveException("Card is not active.");
```

**Arquivo:** `src/main/java/com/spring/bank/domain/service/CardService.java`

---

### 1.2 `TransferService.create()` — condição de tipo de conta invertida

**Problema:** A condição usava `&&` em vez de `||`, permitindo transferências onde **nenhuma** das contas era do tipo CHECKING, ao invés de exigir que **ambas** fossem.

```java
// ANTES (errado — bloqueava apenas quando as duas eram CHECKING)
if (fromAccount.getType() != AccountTypeEnum.CHECKING && toAccount.getType() != AccountTypeEnum.CHECKING)

// DEPOIS (correto — bloqueia se qualquer uma não for CHECKING)
if (fromAccount.getType() != AccountTypeEnum.CHECKING || toAccount.getType() != AccountTypeEnum.CHECKING)
```

**Arquivo:** `src/main/java/com/spring/bank/domain/service/TransferService.java`

---

### 1.3 `AuthController.validateCode()` — retorno ignorado + NullPointerException

**Problema 1:** O retorno booleano de `authService.validateCode()` era completamente ignorado, fazendo com que qualquer código, mesmo incorreto, retornasse `200 OK`.

**Problema 2:** Se `user.getCode()` fosse `null` (usuário sem código pendente), ocorria `NullPointerException`.

```java
// ANTES — retorno ignorado, NPE possível
public boolean validateCode(String code, User user) {
    return user.getCode().equals(code);
}

// DEPOIS — lança exceção semântica, null-safe
public void validateCode(String code, User user) {
    if (user.getCode() == null || !user.getCode().equals(code)) {
        throw new InvalidVerificationCodeException("Invalid or expired verification code");
    }
}
```

**Arquivos:** `AuthService.java`, `AuthController.java`

---

## 2. Segurança

### 2.1 Dados sensíveis removidos de `CardResponseDTO`

**Problema:** A resposta da API expunha o número completo do cartão, CVV e documento do titular — violação direta de PCI DSS.

| Campo removido | Substituído por |
|---|---|
| `number` (completo) | `maskedNumber` (`**** **** **** 1234`) |
| `cvv` | — (removido) |
| `ownerDocument` | — (removido) |

**Arquivo:** `src/main/java/com/spring/bank/domain/dto/card/CardResponseDTO.java`

---

### 2.2 `VerificationCodeGenerator` — geração insegura

**Problema:** Usava `java.util.Random` (não criptográfico), com código de apenas 4 dígitos (10.000 combinações), sem expiração e armazenado em plaintext.

```java
// ANTES
Random random = new Random(); // não criptográfico
int length = 4;               // 10.000 combinações

// DEPOIS
private static final SecureRandom SECURE_RANDOM = new SecureRandom();
private static final int CODE_LENGTH = 6; // 1.000.000 combinações
```

**Arquivo:** `src/main/java/com/spring/bank/common/utils/VerificationCodeGenerator.java`

---

### 2.3 `AccountNumberGenerator` — geração insegura

**Problema:** Também usava `java.util.Random` para gerar números de conta.

```java
// ANTES
Random random = new Random();

// DEPOIS
private static final SecureRandom SECURE_RANDOM = new SecureRandom();
```

**Arquivo:** `src/main/java/com/spring/bank/common/utils/AccountNumberGenerator.java`

---

### 2.4 `SecurityConfig` — actuator exposto, sem CORS, sem headers de segurança

**Problemas:**
- `/actuator/*` inteiramente público (incluindo `/actuator/env` e `/actuator/configprops`, que expõem configurações sensíveis)
- Sem configuração de CORS explícita
- Sem headers de segurança HTTP

**Correções aplicadas:**
- Actuator restrito a: `health`, `info`, `metrics`, `prometheus`
- CORS configurável via `app.cors.allowed-origins` (env var)
- Headers adicionados: `X-Frame-Options: DENY`, `X-Content-Type-Options`, `Strict-Transport-Security`, `Referrer-Policy`
- Sessão explicitamente `STATELESS`

**Arquivo:** `src/main/java/com/spring/bank/common/config/security/SecurityConfig.java`

---

### 2.5 `application.properties` — credenciais hardcoded

**Problema:** Senhas de banco de dados, RabbitMQ e outras configurações sensíveis estavam em texto plano no código-fonte.

```properties
# ANTES
spring.datasource.password=password
spring.rabbitmq.password=guest

# DEPOIS
spring.datasource.password=${DB_PASSWORD:password}
spring.rabbitmq.password=${RABBITMQ_PASSWORD:guest}
```

**Todas as variáveis externalizadas:**

| Variável de ambiente | Padrão |
|---|---|
| `SERVER_PORT` | `8000` |
| `DB_URL` | `jdbc:mysql://localhost:3306/febank...` |
| `DB_USERNAME` | `docker` |
| `DB_PASSWORD` | `password` |
| `RABBITMQ_HOST` | `localhost` |
| `RABBITMQ_PORT` | `5672` |
| `RABBITMQ_USERNAME` | `guest` |
| `RABBITMQ_PASSWORD` | `guest` |
| `KEYCLOAK_ISSUER_URI` | `http://localhost:9000/realms/febank` |
| `MAIL_HOST` | `localhost` |
| `MAIL_PORT` | `1025` |
| `DDL_AUTO` | `update` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` |
| `INVESTMENT_CRON` | `0 0 0 * * *` |
| `CARD_CREDIT_DEFAULT_LIMIT` | `1000` |

**Arquivo:** `src/main/resources/application.properties`

---

## 3. Validações nos DTOs

Todos os DTOs abaixo não tinham nenhuma validação de entrada.

| DTO | Validações adicionadas |
|---|---|
| `ForgotPasswordDTO` | `@NotBlank`, `@Email` no campo `email` |
| `ChangePasswordDTO` | `@NotBlank`, `@Size(min=8)` no campo `password` |
| `ValidateCodeDTO` | `@NotBlank`, `@Size(min=6, max=6)` no campo `code` |
| `ApplyInvestmentDTO` | `@NotNull` em `userId`, `@NotNull @Positive` em `amount` |
| `RegisterDTO` | `@Size(min=8)` adicionado ao campo `password`; `@NotNull` → `@NotBlank` |
| `LoginDTO` | `@NonNull` (Spring) → `@NotBlank` (Jakarta Validation) |

---

## 4. Exception Handling

### 4.1 Novas exceções customizadas

Criados três novos tipos para cobrir casos que usavam `RuntimeException` ou `Exception` genéricos:

- `TransferNotFoundException` — lançada quando uma transferência não é encontrada pelo ID
- `TransactionNotFoundException` — lançada quando uma transação não é encontrada pelo ID
- `InvalidVerificationCodeException` — lançada quando o código de verificação é inválido ou ausente

**Pacote:** `src/main/java/com/spring/bank/common/exception/`

---

### 4.2 Substituição de exceções genéricas

| Serviço / Método | Antes | Depois |
|---|---|---|
| `AccountService.getFirstByUser()` | `RuntimeException` | `AccountNotFoundException` |
| `TransferService.completeTransfer()` | `Exception` genérica | `TransferNotFoundException` |
| `TransactionService.completeTransaction()` | `RuntimeException` | `TransactionNotFoundException` |

---

### 4.3 `GlobalExceptionHandler` — handlers faltantes

**Adicionados:**
- `MethodArgumentNotValidException` — agrupa e retorna todas as mensagens de validação em `400 Bad Request`
- `InvalidVerificationCodeException` → `401 Unauthorized`
- `TransferNotFoundException` → `404 Not Found`
- `TransactionNotFoundException` → `404 Not Found`
- `IllegalArgumentException` → `400 Bad Request`
- `Exception` (fallback genérico) → `500 Internal Server Error` com log de erro

---

### 4.4 Listeners — exceções não tratadas

**Problema:** `TransferListener.listenResponseQueue()` declarava `throws Exception` na assinatura, sem nenhum tratamento. Uma falha no processamento derrubava o consumer silenciosamente.

```java
// ANTES
public void listenResponseQueue(Long transferId) throws Exception {
    this.transferService.completeTransfer(transferId);
}

// DEPOIS
public void listenResponseQueue(Long transferId) {
    try {
        this.transferService.completeTransfer(transferId);
    } catch (Exception e) {
        log.error("Failed to complete transfer id={}: {}", transferId, e.getMessage(), e);
    }
}
```

Mesma correção aplicada em `TransactionListener`.

**Arquivos:** `TransactionListener.java`, `TransferListener.java`

---

### 4.5 `UserController.forgotPassword()` — exceção embrulhada indevidamente

```java
// ANTES — embrulhava MessagingException em RuntimeException genérica
} catch (MessagingException e) {
    throw new RuntimeException(e);
}

// DEPOIS — retorna 503 com mensagem clara
} catch (MessagingException e) {
    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
        "Failed to send verification email. Please try again later.");
}
```

---

### 4.6 `AccountService.addFunds / subtractFunds` — sem validação de valor

```java
// ADICIONADO em ambos os métodos
if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
    throw new IllegalArgumentException("Amount must be greater than zero");
}
```

---

## 5. Logging

Adicionado `SLF4J Logger` em todos os services e listeners que não possuíam nenhum registro de atividade.

| Classe | O que é logado |
|---|---|
| `AccountService` | Abertura de conta, depósito, saque concluídos |
| `CardService` | Criação, bloqueio, desbloqueio de cartão; pagamento processado |
| `TransferService` | Transferência criada, transferência completada |
| `TransactionService` | Transação enfileirada (DEBUG), completada (DEBUG) |
| `UserService` | Criação de usuário, atualização, envio de código, troca de senha |
| `InvestmentService` | Investimento aplicado, rendimentos diários processados |
| `InvestmentScheduler` | Início e fim do job agendado; erros durante execução |
| `TransactionListener` | Erros no processamento de mensagens |
| `TransferListener` | Erros no processamento de mensagens |
| `GlobalExceptionHandler` | Exceções não tratadas (nível ERROR) |

---

## 6. Qualidade de Código

### 6.1 `Card.java` — anotações Lombok redundantes

```java
// ANTES — @Data já inclui @Getter e @Setter
@Getter
@Data
@Setter

// DEPOIS — apenas @Data
@Data
```

Adicionado `toString()` customizado que **não expõe** `cvv` nem `number`.

---

### 6.2 `TransactionController` — path sem barra inicial

```java
// ANTES — path relativo, comportamento imprevisível
@RequestMapping(name = "transaction", path = "transaction")

// DEPOIS
@RequestMapping("/transaction")
```

---

### 6.3 HTTP Status codes incorretos

| Controller / Endpoint | Antes | Depois |
|---|---|---|
| `CardController.createCard()` | `200 OK` | `201 CREATED` |
| `InvestmentController.apply()` | `200 OK` | `201 CREATED` |

---

### 6.4 `InvestmentScheduler` — `fixedRate` → cron configurável

```java
// ANTES — fixo em 1 hora, sem configuração
@Scheduled(fixedRate = 60 * 60 * 1000)

// DEPOIS — cron diário à meia-noite, configurável por env var
@Scheduled(cron = "${app.investment.scheduler.cron:0 0 0 * * *}")
```

---

### 6.5 `InvestmentService.processDailyEarnings()` — melhorias

- Early return se não houver contas (evita exceção desnecessária)
- Skip de contas com saldo zero ou negativo
- Cálculo corrigido: aplica apenas o **rendimento** (delta), não o saldo total
- `@Transactional` adicionado

```java
// ANTES — erro se lista vazia; cálculo confuso
List<Account> accounts = repo.findAllByType(INVESTMENT)
    .orElseThrow(() -> new AccountTypeNotFoundException(...));
BigDecimal updatedAmount = balance.multiply(1 + dailyRate);
createTransaction(..., updatedAmount.subtract(balance), ...); // correto mas obscuro

// DEPOIS — claro e seguro
List<Account> accounts = repo.findAllByType(INVESTMENT).orElse(List.of());
if (accounts.isEmpty()) { log.info("..."); return; }

BigDecimal earnings = balance.multiply(BigDecimal.valueOf(dailyRate));
BigDecimal updatedBalance = balance.add(earnings);
createTransaction(..., earnings, ...);
```

---

### 6.6 Limite do cartão de crédito configurável

```java
// ANTES — hardcoded
card.setLimitAvailable(BigDecimal.valueOf(1000));

// DEPOIS — configurável via application.properties ou env var
@Value("${app.card.credit.default-limit:1000}")
private BigDecimal creditDefaultLimit;
```

---

### 6.7 `TransferResponseDTO` — imports desnecessários removidos

Removidos `jakarta.persistence.*`, `org.hibernate.annotations.*` e `Account` que não eram usados.

---

## 7. Docker e Hot Reload

### 7.1 `Dockerfile` — multi-stage

O Dockerfile agora possui três stages:

| Stage | Propósito |
|---|---|
| `dev` | Hot reload com `spring-boot:run` + Spring DevTools |
| `builder` | Compila o JAR para produção |
| `prod` | Imagem JRE mínima, usuário não-root |

```dockerfile
# Dev: código fonte montado como volume, sem COPY src
FROM eclipse-temurin:21-jdk-jammy AS dev
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments=..."]

# Prod: JRE leve + usuário sem privilégios
FROM eclipse-temurin:21-jre-jammy AS prod
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser
```

---

### 7.2 `docker-compose.yaml` — serviço `app` adicionado

**Novidades:**
- Serviço `app` buildado no stage `dev` com hot reload
- Volumes `./src` e `./pom.xml` montados para que mudanças locais sejam refletidas imediatamente
- Volume `maven_cache` para reutilizar dependências Maven entre reinicializações
- `healthcheck` configurado em MySQL e RabbitMQ
- `app` aguarda MySQL e RabbitMQ estarem saudáveis antes de iniciar (`condition: service_healthy`)
- Todas as configurações sensíveis passadas como variáveis de ambiente

**Para subir o ambiente completo com hot reload:**

```bash
docker compose up --build
```

**Portas expostas pelo serviço app:**

| Porta | Uso |
|---|---|
| `8000` | API REST |
| `35729` | Spring LiveReload |

---

## 8. Tabela Geral de Arquivos Alterados

| Arquivo | Tipo de mudança |
|---|---|
| `CardService.java` | Bug fix (lógica invertida), logging, `@Value` no limite |
| `TransferService.java` | Bug fix (condição invertida), exceção customizada, logging |
| `AuthService.java` | Bug fix (NPE + retorno ignorado), nova exceção |
| `AuthController.java` | Bug fix (resultado ignorado) |
| `CardResponseDTO.java` | Segurança (CVV/documento removidos, mascaramento) |
| `VerificationCodeGenerator.java` | Segurança (SecureRandom, 6 dígitos) |
| `AccountNumberGenerator.java` | Segurança (SecureRandom) |
| `SecurityConfig.java` | Segurança (actuator, CORS, headers) |
| `application.properties` | Segurança (env vars, actuator restrito) |
| `ForgotPasswordDTO.java` | Validação |
| `ChangePasswordDTO.java` | Validação |
| `ValidateCodeDTO.java` | Validação |
| `ApplyInvestmentDTO.java` | Validação |
| `RegisterDTO.java` | Validação |
| `LoginDTO.java` | Validação |
| `AccountService.java` | Exceção customizada, validação, logging |
| `TransactionService.java` | Exceção customizada, logging |
| `UserService.java` | Logging |
| `InvestmentService.java` | Logging, @Transactional, early return, cálculo corrigido |
| `CardService.java` | Logging |
| `GlobalExceptionHandler.java` | Novos handlers, logging |
| `TransactionListener.java` | Try/catch, logging |
| `TransferListener.java` | Try/catch, logging, remoção de `throws Exception` |
| `UserController.java` | Tratamento correto de `MessagingException` |
| `Card.java` | Remoção de anotações redundantes, `toString()` seguro |
| `TransactionController.java` | Path corrigido (barra faltando) |
| `CardController.java` | HTTP status 200 → 201 |
| `InvestmentController.java` | HTTP status 200 → 201 |
| `InvestmentScheduler.java` | `fixedRate` → cron configurável, logging |
| `TransferResponseDTO.java` | Imports desnecessários removidos |
| `TransferNotFoundException.java` | **Novo arquivo** |
| `TransactionNotFoundException.java` | **Novo arquivo** |
| `InvalidVerificationCodeException.java` | **Novo arquivo** |
| `Dockerfile` | **Novo arquivo** (multi-stage, hot reload) |
| `docker-compose.yaml` | Serviço `app`, healthchecks, volumes |
