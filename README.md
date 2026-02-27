# G DEV FLOW – API

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?logo=postgresql&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![API REST](https://img.shields.io/badge/API-REST-blue)
![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)

Backend da plataforma **G DEV FLOW**, uma aplicação de gestão de equipes e tarefas
voltada para projetos de desenvolvimento de software, com foco em simplicidade,
usabilidade e princípios de **Interação Humano-Computador (IHC)**.

---

##  Sumário
- [Visão Geral](#-visão-geral)
- [Objetivos do Sistema](#-objetivos-do-sistema)
- [Stack Tecnológica](#-stack-tecnológica)
- [Arquitetura](#-arquitetura)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Segurança](#-segurança)
- [Como Executar o Projeto](#-como-executar-o-projeto)
- [Metodologia de Desenvolvimento](#-metodologia-de-desenvolvimento)
- [Status do Projeto](#-status-do-projeto)
- [Autores](#-autores)

---

##  Visão Geral
O **G DEV FLOW** tem como objetivo apoiar equipes de desenvolvimento de software
no planejamento, acompanhamento e validação de tarefas, promovendo organização,
transparência e melhor experiência do usuário.

Esta API fornece serviços REST que são consumidos por um aplicativo mobile
desenvolvido em **React Native**, sendo responsável por autenticação,
gerenciamento de projetos, tarefas, usuários e indicadores de progresso.

---

##  Objetivos do Sistema
- Gerenciar projetos de desenvolvimento de software
- Organizar tarefas com prioridades, prazos e responsáveis
- Controlar acesso por papéis (Gestor, Líder, Desenvolvedor, Tester)
- Acompanhar o progresso dos projetos
- Apoiar o fluxo de validação de tarefas

---

##  Stack Tecnológica
- **Java 21**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security (JWT)**
- **PostgreSQL**
- **Maven**

---

##  Arquitetura
A aplicação segue uma **arquitetura em camadas**, separando responsabilidades e
facilitando manutenção e evolução do sistema.

Camadas principais:
- **Controller**: exposição dos endpoints REST
- **Service**: regras de negócio
- **Repository**: acesso aos dados
- **DTO**: transferência e validação de dados
- **Config**: configurações gerais e de segurança

A API segue o padrão **RESTful** e se comunica com o frontend via HTTP/JSON.

---

##  Estrutura do Projeto

    br.com.gdevflow.api
    ├── controller
    ├── service
    ├── repository
    ├── dto
    ├── model
    ├── config
    └── security

---

## Segurança
- Autenticação baseada em JWT

- Controle de acesso por papéis de usuário

- Validações realizadas no backend

- Endpoints protegidos conforme perfil do usuário

---

## Como Executar o Projeto
**Pré-requisitos:**
- Java 21

- Maven

- PostgreSQL

**Passos:**
- Clonar o repositório

- Criar um banco de dados no PostgreSQL

- Configurar o arquivo application.properties

- Executar o projeto pela IDE ou via Maven: mvn spring-boot:run

---

## Metodologia de Desenvolvimento
**O projeto é desenvolvido utilizando Scrum, com:**

- Sprints de 2 semanas

- Desenvolvimento incremental

- Documentação contínua

- Entregas funcionais a cada sprint

---

## Status do Projeto
- Projeto em desenvolvimento
- Trabalho de Conclusão de Curso (TCC)

---
## 👥 Autores
- Thibor Martin &nbsp;&nbsp; [![LinkedIn](https://img.shields.io/badge/LinkedIn-Perfil-blue?logo=linkedin)](https://www.linkedin.com/in/thibor-martin-ab47081ba/)

- Gabriel  Paulon &nbsp;&nbsp;[![LinkedIn](https://img.shields.io/badge/LinkedIn-Perfil-blue?logo=linkedin)](https://www.linkedin.com/in/gabriel-paulon-3ba6a9192/)