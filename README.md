<div align="center">

# Bruma Café Adobe Experience Manager Project

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Regras do Time](#regras-do-time)
- [Organização de Client Libraries — Frente 3](#organização-de-client-libraries--frente-3)
- [Como Executar o Projeto AEM](#como-executar-o-projeto-aem)
- [Documentação Técnica das Frentes](#documentação-técnica-das-frentes)

---

## Sobre o Projeto

A marca foi criada como um cenário prático para testar a capacidade do time de trabalhar de forma colaborativa em um único repositório durante as Semanas 9 e 10 do programa.

Nesta simulação, os estagiários recebem um briefing de negócio e um manual de marca de um "cliente", em vez de telas desenhadas prontas ou tutoriais a serem seguidos.

Mais do que apenas escrever código, o projeto avalia habilidades exigidas no mercado de trabalho, como a capacidade de tomar decisões técnicas e visuais a partir de pedidos de negócio, priorizar entregas em um prazo curto, integrar diferentes frentes de desenvolvimento (como componentes, APIs, formulários e consultas) e saber justificar tecnicamente as escolhas e cortes de escopo durante a apresentação final.

## Regras do Time

### 1. Frentes de Trabalho

* **Cada frente manda na sua própria pasta:** Isso evita que duas pessoas editem o mesmo arquivo ao mesmo tempo.
* **Divisão das frentes:**

| Frente             | Tarefas                     | Responsável | Suplente |
| :----------------- | :-------------------------- | :----------- | :------- |
| **Frente 1** | Header, footer e identidade | Samuel       | Antonni  |
| **Frente 2** | Cafés e integração       | Antonni      | Jhonatan |
| **Frente 3** | Componentes                 | Emiliano     | Gustavo  |
| **Frente 4** | Hub de conteúdo            | Jhonatan     | Emiliano |
| **Frente 5** | Formulário e entrega       | Gustavo      | Samuel   |

---

## Organização de Client Libraries — Frente 3

A Frente 3 utiliza client libraries específicas por componente e mantém os estilos compartilhados centralizados no frontend do projeto.

### Categorias

- `brumacafe.base` — categoria base do projeto e agregação dos Core Components e do grid responsivo.
- `brumacafe.dependencies` — dependências globais do frontend.
- `brumacafe.grid` — estilos do AEM Responsive Grid.
- `brumacafe.site` — estilos, fontes, design tokens e scripts globais do site.
- `brumacafe.components.main-banner` — estilos específicos do componente Main Banner.
- `brumacafe.components.content-grid` — estilos específicos do componente Content Grid.

### Design Tokens

Os valores visuais compartilhados da marca são centralizados em:

```text
ui.frontend/src/main/webpack/site/_variables.scss
ui.frontend/src/main/webpack/site/_tokens.scss
```

As variáveis SCSS são utilizadas pelos estilos compilados do frontend, enquanto `_tokens.scss` expõe propriedades CSS globais com o prefixo `--bruma-*` para uso pelas client libraries dos componentes.

Exemplos:

```css
var(--bruma-color-grain)
var(--bruma-color-roast)
var(--bruma-color-cream)
var(--bruma-color-border)
var(--bruma-font-sans)
var(--bruma-font-serif)
var(--bruma-space-md)
var(--bruma-radius-card)
```

Os componentes devem manter apenas estilos específicos em suas próprias client libraries, reutilizando os design tokens globais para cores, tipografia, espaçamentos, raios e demais valores compartilhados.

Não devem ser adicionados estilos CSS inline nos arquivos HTL.

### 2. Fluxo de Branch e Pull Request (PR)

* A branch main é protegida e exige revisão em PR. Ninguém commita direto nela.
* É obrigatório atualizar sua branch com a main toda manhã antes de escrever qualquer linha de código.
* O fluxo de trabalho será:

  * faz o trabalho em branches separadas por tarefa;
  * Depois, abre-se um PR para a branch development, onde o código é testado;
  * Após a validação, fazer um backup da main;
  * Abre-se o PR final da development para a main.
* Formato Padrão de Nomenclatura de Branch

  ```bash
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

### Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- **Java 11** (versão suportada oficialmente pelo AEM 6.5)
- **Apache Maven 3.6+** (configurado no seu `PATH`)
- **Node.js (18+) e NPM** (necessários para o build do frontend na Frente 3)

### Passo 1: Preparar e rodar o AEM (Author)

1. Crie uma pasta vazia no seu computador.
2. Coloque dentro dela o arquivo `.jar` do AEM (nomeado como `aem-author-p4502.jar`) e a licença correspondente (`license.properties`).
3. Inicie o servidor abrindo o terminal nessa pasta e executando o comando:
   ```bash
   java -jar aem-author-p4502.jar
   ```
4. O AEM extrairá todos os arquivos necessários para a pasta `crx-quickstart`. Aguarde a inicialização (pode levar alguns minutos).
5. O ambiente Author estará rodando em http://localhost:4502, e pedirá credenciais de login (`admin` / `admin`).

### Passo 2: Clonar e Buildar o Código

1. Clone o repositório do zero em um novo diretório:

```bash
git clone https://github.com/gustavodanjos/Bruma-Cafe.git
cd Bruma-Cafe
```

2. Com o AEM local rodando e logado, execute o build completo. Nós utilizamos o perfil `autoInstallSinglePackage` do Maven, que compila todo o código (backend e frontend) e instala os pacotes estruturais (`ui.apps` e `ui.content`) automaticamente na instância conectada.

```bash
mvn clean install -PautoInstallSinglePackage
```

> [!NOTE]
> O primeiro build pode demorar alguns minutos, pois o Maven fará o download de todas as dependências Java da Adobe e o Node.js/NPM será utilizado pelo `frontend-maven-plugin` para compilar o CSS/JS da Frente 3.

### Passo 3: Instalar os Pacotes de Conteúdo e Assets

O build do Maven instala os templates e as estruturas de componentes, mas o conteúdo real (os 4 cafés, 2 produtores cadastrados e suas respectivas imagens no DAM) reside em pacotes de conteúdo (`.zip`).

Para carregar todo o conteúdo oficial da marca, siga o passo a passo:

1. Acesse o **Package Manager** do AEM Author no seu navegador:
   [http://localhost:4502/crx/packmgr/index.jsp](http://localhost:4502/crx/packmgr/index.jsp)
2. Clique no botão **Upload Package**.
3. Clique em **Browse/Escolher arquivo** e selecione o pacote de conteúdo disponível no repositório:
   * Caminho: `packages/brumacafe-conteudo-1.0.0.zip` *(ou a pasta acordada pelo time)*.
     * Se esse pacote não for encontrado nesse caminho provavelmente o código completo dos pacotes já devem estar versionados no repositório
4. Marque a opção **Force Upload** (se aplicável) e clique em **OK**.
5. Na listagem de pacotes, localize o pacote recém-enviado (`brumacafe-conteudo-1.0.0`) e clique no botão **Install**.
6. Na janela modal de confirmação, clique em **Install** novamente e aguarde o log finalizar com a mensagem `Package installed in ...`.



> [!NOTE]
> Se esse pacote não for encontrado nesse caminho provavelmente o código completo dos pacotes já devem estar versionados no repositório

---

### Passo 4: Validar a Instalação

Após a instalação do pacote, valide se os conteúdos foram carregados corretamente:

1. **Content Fragments e Imagens:** Vá em **Navigation > Assets > Files > brumacafe** e confirme se as pastas de imagens e fragmentos contêm os 4 cafés e os 2 produtores com as referências preenchidas.
2. **Páginas do Site:** Acesse **Sites > Bruma Café** e visualize as páginas no editor para confirmar a renderização dos componentes com os dados reais dos cafés.

---

## Documentação Técnica das Frentes

### Frente 4 — Hub de Conteúdo: Validação de Performance (QueryBuilder)

#### Tarefa F4.4: Validação e Otimização da Consulta de Artigos

Para assegurar a estabilidade do repositório JCR e prevenir gargalos de processamento e consumo excessivo de memória na JVM ao carregar listagens de artigos, a busca automática implementada no `ArticleListModel` foi construída utilizando a API nativa do `QueryBuilder` com paginação estrita (`p.limit`).

#### 1. Parâmetros da Consulta (Query Predicates)

```text
path=/content/brumacafe
type=cq:Page
property=jcr:content/cq:template
property.value=/conf/brumacafe/settings/wcm/templates/pagina-de-artigo
orderby=@jcr:content/cq:lastModified
orderby.sort=desc
p.limit=4
```
