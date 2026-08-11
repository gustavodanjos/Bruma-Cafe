# Bruma Café — Projeto AEM

O projeto "Bruma Café" é uma simulação de projeto real desenvolvida como o desafio final (Sprint 5) do Programa de Estágio em AEM. A marca foi criada como um cenário prático para testar a capacidade do time de trabalhar de forma colaborativa em um único repositório durante as Semanas 9 e 10 do programa.

Nesta simulação, os estagiários recebem um briefing de negócio e um manual de marca de um "cliente", em vez de telas desenhadas prontas ou tutoriais a serem seguidos. O objetivo do trabalho é transformar essas diretrizes na fundação de um site institucional e em um hub de conteúdo focado em contar a história por trás da produção de cafés especiais.

Mais do que apenas escrever código, o projeto avalia habilidades exigidas no mercado de trabalho, como a capacidade de tomar decisões técnicas e visuais a partir de pedidos de negócio, priorizar entregas em um prazo curto, integrar diferentes frentes de desenvolvimento (como componentes, APIs, formulários e consultas) e saber justificar tecnicamente as escolhas e cortes de escopo durante a apresentação final.

## Regras do Time

### 1. Frentes de Trabalho
* **Cada frente manda na sua própria pasta:** Isso evita que duas pessoas editem o mesmo arquivo ao mesmo tempo.
* **Divisão das frentes:**

| Frente | Tarefas | Responsável | Suplente |
| :--- | :--- | :--- | :--- |
| **Frente 1** | Header, footer e identidade | Samuel | Antonni |
| **Frente 2** | Cafés e integração | Antonni | Jonathan |
| **Frente 3** | Componentes | Emiliano | Gustavo |
| **Frente 4** | Hub de conteúdo | Jonathan | Emiliano |
| **Frente 5** | Formulário e entrega | Gustavo | Samuel |

### 2. Fluxo de Branch e Pull Request (PR)
* A branch main é protegida e exige revisão em PR. Ninguém commita direto nela.

* É obrigatório atualizar sua branch com a main toda manhã antes de escrever qualquer linha de código.

* O fluxo de trabalho será:
    * faz o trabalho em branches separadas por tarefa;
    * Depois, abre-se um PR para a branch development, onde o código é testado;
    * Após a validação, fazer um backup da main;
    * Abre-se o PR final da development para a main.

* Formato Padrão de Nomenclatura de Branch
    ``` bash
        <tipo>/<id-da-tarefa>-<descricao-curta>
    ```
    1. `<tipo>` (O que esta branch faz?)
    * Use os prefixos do Conventional Commits (em inglês, seguindo suas regras globais de desenvolvimento):
        * feat/ (Feature): Para o desenvolvimento de uma nova funcionalidade ou componente (ex: criar o componente de formulário).

        * fix/ (Fix): Para correção de bugs (ex: consertar a validação do formulário que quebrou).

        * chore/ (Chore): Para tarefas de configuração, dependências ou coisas que não afetam o código de produção (ex: configurar o pom.xml, setup inicial).

        * docs/ (Docs): Para alterações exclusivas em documentação (ex: atualizar o README.md).

        * test/ (Test): Para adição ou correção de testes (ex: testes unitários com AEM Mocks).

        * refactor/ (Refactor): Para melhorias de código que não adicionam funcionalidades nem corrigem bugs (ex: reescrever um Sling Model para ficar mais limpo).

    2. `<id-da-tarefa>` (De onde vem essa demanda?)
    * Como o seu projeto é dividido em Frentes (F0, F1, F2...) e tarefas específicas (F1.1, F5.2), inclua esse ID logo após o tipo. Isso facilita muito na hora do Code Review para os colegas saberem o que estão revisando.

    3. `<descricao-curta>` (Qual o contexto?)
    * Uma descrição de 3 a 5 palavras separadas por hífen (`-` ou kebab-case), escrita preferencialmente em inglês (seguindo a sua regra global de usar inglês para nomenclaturas de arquivos e código).

* Exemplos Práticos aplicados à Sprint 5
    * Para a Frente 0 (Fundação):
        * chore/f0.1-project-initial-setup (Setup do projeto via archetype)

        * feat/f0.2-base-page-template (Criação do template base)

    * Para a Frente 1 (Header e Identidade):

        * feat/f1.1-header-experience-fragment (Criando o header)

        * fix/f1.1-header-mobile-layout (Consertando um bug no layout do header no celular)

    * Para a Frente 4 (Hub de Conteúdo):

        * feat/f4.9-reading-time-sling-model (Lógica de tempo de leitura no artigo)

    * Para a Frente 5 (Testes e Qualidade):

        * test/f5.2-junit-base-configuration (Configuração inicial dos testes)

        * docs/f5.6-project-readme-architecture (Escrita da documentação)

* Por que usar esse padrão?
    * Rastreabilidade: Quando alguém olhar o histórico do Git (ou o GitHub), saberá exatamente qual tarefa do PDF está sendo resolvida naquela branch (ex: f4.9).

    * Revisão de Código (PRs): Facilita bater a branch com o Critério de Aceite da tarefa na hora que você for revisar o PR do seu colega.

    * Prevenção de Conflitos: Evita que duas pessoas criem branches com nomes genéricos como meu-componente ou ajuste-header.

    * Profissionalismo: O cliente e os avaliadores verão um histórico de versionamento limpo e corporativo.

---

## Como Executar o Projeto AEM

Para garantir que o projeto funciona de verdade (e não apenas na máquina de quem escreveu), o teste objetivo é executá-lo em uma instância limpa. Siga o passo a passo:

### Passo 1: Preparar e rodar o AEM (Author)
1. Crie uma pasta vazia no seu computador.
2. Coloque dentro dela o arquivo `.jar` do AEM (nomeado como `aem-author-p4502.jar`).
3. Inicie o servidor abrindo o terminal nessa pasta e executando o comando:
   ```bash
   java -jar aem-author-p4502.jar

4. Aguarde a inicialização (pode levar alguns minutos). O ambiente Author estará rodando em http://localhost:4502, onde você poderá acessar o conteúdo deste projeto.

### Passo 2: Clonar e Buildar o Código

1. Clone o repositório do zero:

```Bash
    git clone https://github.com/gustavodanjos/Bruma-Cafe.git
    cd Bruma-Cafe
``` 
2. Com o AEM rodando, execute o build completo utilizando o perfil do Maven que instala os pacotes básicos (ui.apps e ui.content) diretamente no AEM:

```Bash
    mvn clean install -PautoInstallSinglePackage
```
