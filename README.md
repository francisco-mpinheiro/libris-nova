# 📚 LibrisNova

> Sistema de Controle de Empréstimos de Livros



![Catalago LibrisNova](/docs/img/Captura%20de%20tela%202026-02-03%20161909.png)



## 📌 Sobre o Projeto

O **LibrisNova** é uma aplicação desenvolvida para organizar e controlar o empréstimo de livros em bibliotecas. O sistema garante que cada obra esteja sempre com seu status atualizado, permitindo que usuários consultem o catálogo disponível enquanto perfis operacionais (Apoio e Admin) realizam o controle de empréstimos e devoluções de forma simples e segura.

---

## ✨ Funcionalidades

- [x] Consulta ao catálogo de livros com status em tempo real
- [x] Cadastro, edição e exclusão de livros
- [x] Registro e controle de empréstimos
- [x] Registro de devoluções com atualização automática de status
- [x] Controle de acesso baseado em perfis (Usuário, Apoio, Administrador)
- [x] Autenticação por login e senha
- [x] Histórico de empréstimos por usuário 
- [x] Histórico de todos os livros vistos por ADMIN e APOIO
- [x] Painel do Admin: Visualização, criação, edição e exclusão de usuários
- [x] Painel do Admin: Função de promover ou alterar o perfil (role) de um usuário
- [x] Página de gestão de emprestimos, aprovação de emprestimos, devolução de emprestimos e historico geral (ADMIN e APOIO)
- [x] Aprimoramento das telas de visualização de empréstimos (visões para Usuário, Apoio e Admin)
- [x] Página para ediçaõ de dados do usuário, como, senha, email e nome
- [x] Registro automático de data de cada empréstimo realizado
- [x] Registro de auditoria: Identificar qual operador liberou o livro e quem registrou a devolução
- [x] Modal de Detalhes: Permitir que o usuário veja informações do livro, como: Título, Autor, ISBN e Ano de Publicação
- [x] Logs de operações críticas do sistema

---

## 👥 Perfis e Permissões

| Permissão                        | Usuário | Apoio | Administrador |
|----------------------------------|:-------:|:-----:|:-------------:|
| Visualizar catálogo              | ✅      | ✅    | ✅            |
| Ver detalhes do livro            | ✅      | ✅    | ✅            |
| Liberar empréstimo               | ❌      | ✅    | ✅            |
| Registrar devolução              | ❌      | ✅    | ✅            |
| Cadastrar livro                  | ❌      | ❌    | ✅            |
| Editar livro                     | ❌      | ❌    | ✅            |
| Excluir livro                    | ❌      | ❌    | ✅            |

---

## 🛠️ Tecnologias Usadas

- **Linguagem:** Java 25
- **Framework:** Spring Boot 4.0.1
- **Interface:** Thymeleaf
- **Banco de Dados:** PostgreSQL
- **Segurança:** Spring Security + JWT (Autenticação via Tokens)
- **Containerização:** Docker & Docker Compose
- **Build Tool:** Maven


---



## ⚙️ Instalação

### 1. Clone o repositório
```bash
 git clone https://github.com/francisco-mpinheiro/libris-nova.git
```
### 2. Acesse a pasta do projeto
```
cd francisco-pinheiro
```

### 3. Acesse a pasta Infra
```
cd infra
```
### 4. Subindo o banco de dados
```bash
docker compose up -d
```

### 4. Execute a apliacação na raiz do diretório 
```bash
mvn clean spring-boot:run
```
### 5. Acessando a apliacação
```bash
http://localhost:8080
```


---



## Evolução do projeto

### 1. Mudança na lógica de autenticação

Anteriormente, a lógica de autenticação do usuário era feita via JWT, seguindo a abordagem stateless, o que tornava a aplicação suscetível a falhas de segurança, pois uma pessoa mal-intencionada poderia capturar o token de outro usuário e se passar por ele.  

Com a migração, a segurança passou a ser de responsabilidade total do Spring Security, que agora gerencia a autenticação por meio de cookies de sessão, adotando a abordagem stateful e tornando o sistema mais seguro contra esse tipo de vulnerabilidade.

### 2. Criação de novas telas

O sistema possui mais 3 novas telas: empréstimos, usuários e profile.

* *Empréstimos* - Serve para aprovar ou recusar empréstimos, confirmar devolução e visualizar o histórico geral. Somente ADMIN e APOIO podem acessar.

* *Usuários* - Serve para a gestão de usuários, sendo possível visualizar todos os usuários do sistema, criar um novo, editar um usuário já existente e apagar usuários.

* *Profile* - Serve para edição de dados cadastrais dos usuários de todos os níveis, podendo alterar nome, senha e e-mail.

### 3. Auditoria e Rastreabilidade de Operações

Anteriormente, o sistema apenas registravas as datas de aprovação e devolução de emprestimos, mas agora ele mostra quem aprovou a solicitação do emprestimo, e também quem recebeu o livro no momento da entrega, garantindo uma rastreabilidade sobre a lógica de aprovação

### 4. Registro de Logs de operações Críticas 

Foram implementados na aplicação logs estratégicos utilizando o framework Log4j 2 para monitorar o ciclo de operações críticas do sistema, facilitando o acompanhamento de possíveis erros de fluxo e garantindo o registro de todas as operações realizadas.

---


## 📁 Estrutura do Projeto

```
LibrisNova/
├── src/
│   ├── main/
│   │   ├── java/br/jus/tse/administrativo/librisnova/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── infra/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   └── js/
│   │       └── templates/
│   └── test/
├── compose.yaml
└── pom.xml

```

A organização segue o padrão de arquitetura em camadas do Spring:

- **controller/:** Camada de exposição dos endpoints e rotas Web.

- **service/:** Regras de negócio da aplicação.

- **repository/:** Interfaces de comunicação com o banco de dados (JPA).

- **entity/:** Modelagem das tabelas do banco.

- **dto/:** Objetos de transferência de dados para segurança e validação.

- **infra/:** Configurações de segurança e beans do sistema.
---

## 🆘 Suporte
Para dúvidas ou sugestões, entre em contato através do e-mail  ou abra uma *Issue* no repositório oficial do Github.



