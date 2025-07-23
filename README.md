# FeBank Backend 🏦

![Java](https://img.shields.io/badge/Java-21-blue?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.3-success?style=flat&logo=springboot)
![License](https://img.shields.io/github/license/FelipeRodrigues05/febank-backend)
![MySQL](https://img.shields.io/badge/MySQL-8.0-informational?style=flat&logo=mysql)
![OAuth2](https://img.shields.io/badge/Auth-OAuth2.0-yellow?style=flat&logo=keycloak)

Backend oficial do **FeBank**, um sistema bancário digital criado com foco em segurança, escalabilidade e arquitetura moderna. Desenvolvido com **Java 21**, **Spring Boot**, **OAuth 2.0** (Client Credentials) e **MySQL**.

---

## 🚀 Tecnologias

- Java 21  
- Spring Boot  
- Spring Security  
- OAuth 2.0 (Client Credentials)  
- MySQL  
- Spring Data JPA  
- Maven  
- Docker & Docker Compose (opcional)

---

## 📦 Funcionalidades

- ✅ Autenticação via OAuth 2.0 (Client Credentials)  
- ✅ Criação e gerenciamento de contas bancárias  
- ✅ Registro e consulta de transações financeiras  
- ✅ Transferências entre contas  
- ✅ Segurança com JWT e proteção de rotas  
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

2. Configure as variáveis de ambiente no application.properties ou .yml
   ```bash
   spring.datasource.url=jdbc:mysql://localhost:3306/febank
   spring.datasource.username=root
   spring.datasource.password=senha
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Execute o projeto:
   ```bash
   ./mvnw spring-boot:run
   ```

## 🔐 Autenticação OAuth2 (Client Credentials)
Este projeto implementa OAuth 2.0 utilizando o Client Credentials Grant — ideal para comunicação entre serviços (machine-to-machine).

Exemplo de token request:

   ```http
   POST /oauth2/token
   Authorization: Basic base64(client-id:client-secret)
   Content-Type: application/x-www-form-urlencoded
   grant_type=client_credentials
   ```


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
