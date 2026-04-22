# FeBank Backend 🏦

![Java](https://img.shields.io/badge/Java-21-blue?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.3-success?style=flat&logo=springboot)
![License](https://img.shields.io/github/license/FelipeRodrigues05/febank-backend)
![MySQL](https://img.shields.io/badge/MySQL-8.0-informational?style=flat&logo=mysql)
![OAuth2](https://img.shields.io/badge/Auth-OAuth2.0-yellow?style=flat&logo=jsonwebtokens)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3-orange?style=flat&logo=rabbitmq)

Backend oficial do **FeBank**, um sistema bancário digital criado com foco em segurança, escalabilidade e arquitetura moderna. Desenvolvido com **Java 21**, **Spring Boot**, **OAuth 2.0** (Client Credentials) e **MySQL**.

---

## 🚀 Tecnologias

- Java 21  
- Spring Boot  
- Spring Security + JWT (HMAC256)  
- OAuth 2.0 (Client Credentials — implementação própria)  
- MySQL 8  
- RabbitMQ (processamento assíncrono de transações e transferências)  
- Spring Data JPA  
- Maven  
- Docker & Docker Compose

---

## 📦 Funcionalidades

- ✅ Autenticação via OAuth 2.0 (Client Credentials) com JWT próprio  
- ✅ Criação e gerenciamento de contas bancárias  
- ✅ Registro e consulta de transações financeiras (processamento assíncrono via RabbitMQ)  
- ✅ Transferências entre contas  
- ✅ Cartões de débito e crédito  
- ✅ Fatura de cartão de crédito com fechamento automático mensal  
- ✅ Chaves Pix  
- ✅ Cofrinhos (Savings Box)  
- ✅ Investimentos com rendimento automático agendado  
- ✅ API RESTful com arquitetura limpa

---

## 🛠️ Requisitos

- Java 21  
- MySQL 8+  
- Maven 3.8+  
- Docker (opcional)

---

## 🔧 Configuração e Execução

1. Clone o repositório:
   ```bash
   git clone https://github.com/FelipeRodrigues05/febank-backend.git
   cd febank-backend
   ```

2. Suba os containers (app + MySQL + RabbitMQ + Mailpit + Prometheus + Grafana):
   ```bash
   docker compose up
   ```

   Ou execute localmente configurando as variáveis de ambiente:
   ```bash
   DB_URL=jdbc:mysql://localhost:3306/nexwallet
   DB_USERNAME=root
   DB_PASSWORD=senha
   RABBITMQ_HOST=localhost
   JWT_SECRET=seu-secret
   DEFAULT_CLIENT_SECRET=seu-client-secret
   ```

   ```bash
   ./mvnw spring-boot:run
   ```

## 🔐 Autenticação OAuth2 (Client Credentials)

Implementação própria do OAuth 2.0 Client Credentials — sem Keycloak. Na inicialização, um client padrão é criado automaticamente via `ClientSeeder`.

```http
POST /oauth/token
Authorization: Basic base64(client_id:client_secret)
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials
```

O token JWT retornado deve ser enviado nas demais requisições:

```http
Authorization: Bearer <token>
```

Para criar novos clients (rota pública):

```http
POST /clients
Content-Type: application/json

{ "clientId": "meu-app", "name": "Meu App" }
```

O secret é gerado pelo servidor e retornado apenas na criação.


## 🧪 Rodando os Testes
   ```bash
   ./mvnw test
   ```

## 🤝 Contribuindo

1. Faça um fork

2. Crie uma branch: `git checkout -b feature/nova-feature`

3. Commit: `git commit -m 'feat: nova feature'`

4. Push: `git push origin feature/nova-feature`

5. Abra um Pull Request

## 📄 Licença

Distribuído sob a licença MIT. Veja LICENSE para mais informações.

> Feito com 💚 por @FelipeRodrigues05
