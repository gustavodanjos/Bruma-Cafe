<div align="center">

# Bruma Café — Projeto AEM

[![AEM](https://img.shields.io/badge/AEM-6.5%2B-FF0000?logo=adobe&logoColor=white)](https://experienceleague.adobe.com/docs/experience-manager.html)
[![Java](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk&logoColor=white)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A22?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Node.js](https://img.shields.io/badge/Node.js-18%2B-339933?logo=nodedotjs&logoColor=white)](https://nodejs.org/)
[![Status](https://img.shields.io/badge/Status-Finalizado-brightgreen)]()

<br/>

> O projeto **Bruma Café** é uma simulação de projeto real desenvolvida como o desafio final do Programa de Estágio em AEM da Webjump. O objetivo é transformar diretrizes de negócio na fundação de um site institucional e hub de conteúdo focado em cafés especiais.

</div>

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Stack de Tecnologia](#stack-de-tecnologia)
- [Arquitetura do Projeto](#arquitetura-do-projeto)
- [Padrões de Código e Desenvolvimento](#padrões-de-código-e-desenvolvimento)
- [Decisões Importantes do Time](#decisões-importantes-do-time)
- [Metodologia](#metodologia)
- [Regras do Time](#regras-do-time)
- [Como Executar o Projeto AEM](#como-executar-o-projeto-aem)
- [Documentação Técnica das Frentes](#documentação-técnica-das-frentes)
- [Limitações Conhecidas](#limitações-conhecidas)
- [Equipe](#equipe)

---

## Sobre o Projeto

A marca foi criada como um cenário prático para testar a capacidade do time de trabalhar de forma colaborativa em um único repositório durante as Semanas 9 e 10 do programa.

Nesta simulação, os estagiários recebem um briefing de negócio e um manual de marca de um "cliente", em vez de telas desenhadas prontas ou tutoriais a serem seguidos.

Mais do que apenas escrever código, o projeto avalia habilidades exigidas no mercado de trabalho: tomar decisões técnicas e visuais a partir de pedidos de negócio, priorizar entregas em prazo curto, integrar diferentes frentes de desenvolvimento e justificar tecnicamente as escolhas e cortes de escopo durante a apresentação final.

Para guiar a criação da interface, realizamos um **protótipo no Figma** baseado no manual de marca da Bruma Café, definindo a paleta de cores, tipografia, espaçamentos e layout das páginas principais antes de escrever qualquer linha de código — garantindo alinhamento visual entre as frentes desde o início.

---

## Stack de Tecnologia

| Camada                             | Tecnologia                             | Versão |
| :--------------------------------- | :------------------------------------- | :------ |
| **Plataforma CMS**           | Adobe Experience Manager (AEM)         | 6.5+    |
| **Backend**                  | Java + OSGi (Apache Felix)             | 11      |
| **Build Backend**            | Apache Maven                           | 3.6+    |
| **Templates / Views**        | HTL (HTML Template Language)           | —      |
| **Modelos de Dados**         | Sling Models + Apache Sling            | —      |
| **Frontend (Fonte)**         | SCSS + JavaScript (ES6+)               | —      |
| **Build Frontend**           | Webpack (via`frontend-maven-plugin`) | 5+      |
| **Gerenciador de Pacotes**   | NPM                                    | 9+      |
| **Runtime Frontend**         | Node.js                                | 18+     |
| **Design / Prototipação**  | Figma                                  | —      |
| **Testes**                   | JUnit 5 + AEM Mocks                    | —      |
| **Cobertura de Testes**      | JaCoCo                                 | —      |
| **Versionamento**            | Git + GitHub                           | —      |
| **Gerenciamento de Tarefas** | Trello                                 | —      |

---

## Arquitetura do Projeto

### Módulos do AEM (Estrutura do Repositório)

A estrutura segue o padrão do **AEM Project Archetype**, dividida nos seguintes módulos:

```text
Bruma-Cafe/
├── core/           # Código Java: Sling Models, Servlets OSGi, Services
├── ui.apps/        # Componentes, HTL, dialogs (_cq_dialog) e ClientLibs finais
├── ui.content/     # Conteúdo mutável: /conf, Editable Templates, políticas, Content Fragments
├── ui.frontend/    # Código-fonte frontend: Webpack, SCSS e JS (compilados e injetados no ui.apps)
├── ui.config/      # Configurações OSGi específicas do ambiente
├── all/            # Pacote agregador (instala todos os módulos de uma vez)
├── dispatcher/     # Configurações do dispatcher
└── it.tests/       # Testes de integração (Estrutura gerada, testes não implementados na Sprint)
```

### Fluxo de Build do Frontend

Para garantir otimização de recursos, o frontend (Frente 3) segue este fluxo automatizado:
1. O `frontend-maven-plugin` dispara o Node/NPM durante o build.
2. O Webpack compila e minifica SCSS e JS presentes em `ui.frontend`.
3. Os assets resultantes são automaticamente copiados para a pasta `clientlibs` dentro do módulo `ui.apps`.
4. O AEM empacota o `ui.apps` e serve os arquivos já processados para o site.

### Fluxo de Responsabilidades (Server-Side)

Baseado nas diretrizes do projeto:

```text
HTL (Apresentação)
  ↓
Sling Model (Bridge entre recurso e view)
  ↓
Service (Lógica de negócio / Integrações)
  ↓
AEM API / Repositório JCR / API Externa
```

- **HTL** responde apenas perguntas de exibição: _o que renderizar_, _deve aparecer?_, _qual classe CSS aplicar?_
- **Sling Model** é a ponte entre o conteúdo do repositório e a view: lê propriedades, expõe valores prontos para exibição e delega lógica de negócio ao Service.
- **Service (OSGi)** centraliza lógica reutilizável, integrações externas e operações com responsabilidade independente.

### Estrutura de um Componente

```text
component-name/
├── component-name.html       # Template HTL
├── component-name.js         # JavaScript do componente
├── component-name.scss       # Estilos específicos (compilados via Webpack)
├── _cq_dialog/               # Dialog de edição para o autor
└── _cq_editConfig.xml        # Configuração do editor
```

---

## Padrões de Código e Desenvolvimento

### Idioma e Nomenclatura

Todo o código (variáveis, classes, métodos, nomes de arquivos e mensagens de commit) é mantido **obrigatoriamente em inglês**, seguindo a convenção corporativa e garantindo que qualquer desenvolvedor consiga entender o repositório.

### Clean Code

As seguintes regras foram aplicadas em todo o desenvolvimento:

- **KISS** (*Keep It Simple, Stupid*): Soluções simples e diretas são preferidas a abstrações desnecessárias.
- **DRY** (*Don't Repeat Yourself*): Lógica duplicada foi extraída para Services ou helpers reutilizáveis.
- **YAGNI** (*You Aren't Gonna Need It*): Nenhuma funcionalidade foi adicionada sem demanda real e justificada.
- **Separation of Concerns**: HTL cuida de apresentação, Sling Model de dados, Service de lógica de negócio.
- **Responsabilidade Única**: Cada método ou classe tem um único motivo para existir e mudar.

### Commits (Conventional Commits)

Todos os commits seguem o padrão **Conventional Commits**, com mensagem em inglês:

```
<tipo>(<escopo opcional>): <descrição curta em inglês>
```

| Tipo         | Uso                                                |
| :----------- | :------------------------------------------------- |
| `feat`     | Nova funcionalidade ou componente                  |
| `fix`      | Correção de bug                                  |
| `docs`     | Alteração somente em documentação              |
| `refactor` | Melhoria de código sem alterar comportamento      |
| `test`     | Adição ou correção de testes                   |
| `chore`    | Configuração, build ou tarefas auxiliares        |
| `style`    | Formatação, espaçamento (sem impacto funcional) |

**Exemplos:**

```
feat: add article listing component with querybuilder
fix: correct email validation on contact servlet
docs: document clean installation routine
chore: configure junit5 and aem mocks dependencies
```

---

## Decisões Importantes do Time

### 1. Protótipo no Figma

Antes de qualquer implementação, a equipe criou um protótipo visual no Figma a partir do manual de marca da Bruma Café. Esse protótipo definiu a paleta (`--bruma-color-*`), tipografia, espaçamentos e grid das páginas principais. Isso eliminou ambiguidades visuais entre as frentes e permitiu que o desenvolvimento de componentes fosse iniciado com referências concretas.

### 2. Design System Global (Frontend)

- **Design Tokens centralizados:** Todas as cores, fontes e espaçamentos da marca estão definidos em `_variables.scss` e expostos como Custom Properties CSS em `_tokens.scss` (prefixo `--bruma-*`). Nenhum componente define suas próprias cores ou fontes diretamente — todos consomem os tokens globais.
- **ClientLibs por Componente:** Cada componente possui sua própria ClientLibrary com categoria específica (ex: `brumacafe.components.main-banner`). As páginas carregam apenas os estilos dos componentes que efetivamente utilizam, reduzindo o peso do CSS entregue ao navegador.
- **Proibição de CSS inline no HTL:** Nenhum estilo é escrito diretamente em atributos `style=""` nos arquivos HTL.

### 3. Arquitetura Backend (Separation of Concerns)

Seguindo rigorosamente o fluxo definido:

- A lógica de negócio nunca fica no HTL. O HTL apenas exibe dados prontos expostos pelo Sling Model.
- A lógica de apresentação (ex: esconder o botão quando não há link) fica **no Model**, nunca no template.
- Integrações externas (loja virtual) ficam **no Service OSGi**, isoladas e testáveis independentemente.

### 4. Resiliência e Performance

- **API Real da Loja da Bruma:** A vitrine de produtos consome a API própria da marca (`https://api-bruma-cafe.vercel.app/api/produtos`), retornando os dados reais dos cafés disponíveis. O endpoint é configurável no Console OSGi sem necessidade de novo *build*, permitindo ajustes futuros sem tocar em código.
- **Integração Desacoplada (Loja):** A chamada à API é feita no backend via OSGi Service. Os resultados são mantidos em **cache temporário configurável** para evitar requisições redundantes a cada visita.
- **Fallback da Loja:** Se a API externa falhar ou ficar indisponível, o componente exibe um estado alternativo graciosamente — o site nunca quebra por causa de uma dependência externa.
- **QueryBuilder com Limite:** A listagem de artigos usa `p.limit` estrito no QueryBuilder para evitar varreduras ilimitadas no repositório JCR, protegendo a JVM de picos de consumo de memória.

### 5. Segurança no Formulário de Contato

A validação dos dados (campos obrigatórios, formato de e-mail) foi aplicada em **duas camadas**: no frontend (navegador), para feedback imediato ao usuário, e no backend (Sling Servlet), para garantir que nenhum dado inconsistente ou malicioso seja gravado no repositório mesmo que o formulário seja submetido diretamente via API.

### 6. Reutilização via Proxy Components

Em vez de criar componentes de Navegação e Breadcrumb do zero, utilizamos o padrão **Proxy Component** sobre os Core Components oficiais da Adobe. Isso herda toda a lógica, robustez e acessibilidade já validada pela Adobe, permitindo adicionar apenas as customizações específicas da Bruma Café e minimizando risco e esforço de manutenção futuro.

---

## Metodologia

O time adotou uma metodologia inspirada em **Scrum/Kanban** ao longo das duas semanas de desenvolvimento colaborativo (Sprint 5).

### Organização das Tarefas

- **Trello:** O backlog de tarefas foi gerenciado em um quadro Trello compartilhado com colunas: *Backlog*, *A Fazer*, *Em Andamento*, *Revisão de Código*, *Fase de Teste* e *Concluído*. Cada card representa uma tarefa das frentes e inclui critérios de aceite, prioridade e responsável.
- **Divisão por Frentes:** O projeto foi dividido em 5 frentes de desenvolvimento paralelas, cada uma com responsável e suplente, para minimizar conflitos de merge e maximizar a velocidade de entrega.

### Fluxo de Trabalho (Git Flow Simplificado)

1. Tarefa é retirada do Backlog e atribuída no Trello.
2. Desenvolvedor cria uma branch: `<tipo>/f<X.Y>-<short-description>`.
3. Código desenvolvido com commits atômicos (Conventional Commits).
4. Pull Request aberto para `development` — revisão obrigatória por pelo menos 1 colega.
5. Após validação e testes, PR é mergeado em `development`.
6. Ao fim da Sprint, `development` → `main` com PR final revisado.

---

## Regras do Time

### Frentes de Trabalho

Cada frente é responsável pela sua própria pasta no repositório, evitando que duas pessoas editem o mesmo arquivo simultaneamente.

| Frente             | Tarefas                     | Responsável | Suplente |
| :----------------- | :-------------------------- | :----------- | :------- |
| **Frente 1** | Header, footer e identidade | [@samuelcosta-pd](https://github.com/samuelcosta-pd)       | [@AntonniSMoraes](https://github.com/AntonniSMoraes)  |
| **Frente 2** | Cafés e integração       | [@AntonniSMoraes](https://github.com/AntonniSMoraes)      | [@jhonatanLobo](https://github.com/jhonatanLobo) |
| **Frente 3** | Componentes                 | [@Emiliano-Souza](https://github.com/Emiliano-Souza)     | [@gustavodanjos](https://github.com/gustavodanjos)  |
| **Frente 4** | Hub de conteúdo            | [@jhonatanLobo](https://github.com/jhonatanLobo)     | [@Emiliano-Souza](https://github.com/Emiliano-Souza)  |
| **Frente 5** | Formulário e entrega       | [@gustavodanjos](https://github.com/gustavodanjos)      | [@samuelcosta-pd](https://github.com/samuelcosta-pd)   |

### Nomenclatura de Branch

```bash
<tipo>/<id-da-tarefa>-<short-description-in-english>
```

**Exemplos práticos:**

```
chore/f0.1-project-initial-setup
feat/f1.1-header-experience-fragment
fix/f1.1-header-mobile-layout
feat/f4.9-reading-time-sling-model
test/f5.2-junit-base-configuration
docs/f5.6-project-readme-architecture
```

### Organização de Client Libraries — Frente 3

| Categoria                             | Responsabilidade                                         |
| :------------------------------------ | :------------------------------------------------------- |
| `brumacafe.base`                    | Categoria base, agrega Core Components e grid responsivo |
| `brumacafe.dependencies`            | Dependências globais do frontend                        |
| `brumacafe.grid`                    | Estilos do AEM Responsive Grid                           |
| `brumacafe.site`                    | Estilos, fontes, design tokens e scripts globais do site |
| `brumacafe.components.main-banner`  | Estilos específicos do componente Main Banner           |
| `brumacafe.components.content-grid` | Estilos específicos do componente Content Grid          |

**Design Tokens globais:**

```text
ui.frontend/src/main/webpack/site/_variables.scss  ← Variáveis SCSS
ui.frontend/src/main/webpack/site/_tokens.scss     ← Custom Properties CSS (--bruma-*)
```

```css
var(--bruma-color-grain)
var(--bruma-color-roast)
var(--bruma-color-cream)
var(--bruma-font-sans)
var(--bruma-font-serif)
var(--bruma-space-md)
var(--bruma-radius-card)
```

---

## Como Executar o Projeto AEM

### Pré-requisitos

| Ferramenta   | Versão | Observação                         |
| :----------- | :------ | :----------------------------------- |
| Java (JDK)   | 11      | **Obrigatório.** O AEM 6.5 não inicializa corretamente com Java 17+. |
| Apache Maven | 3.6+    | Deve estar configurado no`PATH`    |
| Node.js      | 18+     | Necessário para o build do frontend |
| NPM          | 9+      | Instalado junto com o Node.js        |

> [!CAUTION]
> **Dependências Proprietárias:** O arquivo `.jar` do AEM e o arquivo de licença (`license.properties`) não estão versionados neste repositório por questões de direitos autorais da Adobe. Você deve obter esses arquivos pelos canais oficiais para executar o projeto.

### Passo 1: Preparar e rodar o AEM (Author)

1. Crie uma pasta vazia no seu computador (ex: `aem-author`).
2. Coloque dentro dela o arquivo `.jar` do AEM e a licença correspondente (`license.properties`).
3. Renomeie o arquivo `.jar` para `aem-author-p4502.jar` (isso define o modo Author e a porta 4502).
4. Inicie o servidor via terminal:
   ```bash
   java -jar aem-author-p4502.jar
   ```
5. O AEM extrairá todos os arquivos para a pasta `crx-quickstart`. Aguarde a inicialização (pode levar até 10 minutos na primeira vez).
6. Acesse http://localhost:4502 e faça login com `admin` / `admin`.

### Passo 2: Clonar e Buildar o Código

1. Clone o repositório:

   ```bash
   git clone https://github.com/gustavodanjos/Bruma-Cafe.git
   cd Bruma-Cafe
   ```
2. Com o AEM rodando, execute o build completo:

   ```bash
   mvn clean install -PautoInstallSinglePackage
   ```

> [!NOTE]
> O primeiro build pode levar alguns minutos, pois o Maven baixará todas as dependências Java da Adobe e o `frontend-maven-plugin` usará o Node.js/NPM para compilar o SCSS/JS da Frente 3.

### Passo 3: Instalar o Conteúdo Base (Desambiguação)

O comando do Maven acima compila o código Java, o Frontend e instala as estruturas de Componentes e Templates diretamente na sua instância AEM.

No entanto, o conteúdo redacional puramente authorável (Páginas prontas, 4 cafés, 2 produtores em Content Fragments e imagens no DAM) é mantido em um pacote de backup congelado, para garantir que novos desenvolvedores tenham a mesma massa de dados sem sujar o repositório principal.

Para instalar essa massa de conteúdo:

1. Acesse o **Package Manager**: [http://localhost:4502/crx/packmgr/index.jsp](http://localhost:4502/crx/packmgr/index.jsp)
2. Clique em **Upload Package** e selecione o arquivo local: `packages/brumacafe-site-final-1.0.0.zip`
3. Localize o pacote na listagem e clique em **Install**.

### Alternativa: Instalar Apenas o Content Package (Sem Clonar o Repositório)

Se quiser testar o site sem clonar o código, é possível instalar apenas o pacote final completo:

1. Inicie o AEM conforme o **Passo 1** acima.
2. Acesse o **Package Manager**: [http://localhost:4502/crx/packmgr/index.jsp](http://localhost:4502/crx/packmgr/index.jsp)
3. Faça upload e instale o arquivo `packages/brumacafe-site-final-1.0.0.zip`.
4. Após a instalação, o site já estará disponível em [http://localhost:4502/content/brumacafe/br/pt.html](http://localhost:4502/content/brumacafe/br/pt.html).

### Passo 4: Validar a Instalação

1. **Content Fragments e Imagens:** Navegue em **Navigation > Assets > Files > brumacafe** e confirme a presença dos 4 cafés e 2 produtores com referências preenchidas.
2. **Páginas do Site:** Acesse **Sites > Bruma Café** e visualize as páginas no editor para confirmar a renderização dos componentes.

## Execução dos Testes

O projeto utiliza **JUnit 5** em conjunto com **AEM Mocks** (`io.wcm.testing.aem-mock`) para testar a lógica dos Sling Models e Services OSGi.

Para rodar todos os testes unitários e gerar o relatório de cobertura, execute na raiz do projeto:

```bash
mvn clean test
```

- A configuração do **JaCoCo** gera relatórios que podem ser encontrados em: `core/target/site/jacoco/index.html`.
- O módulo `it.tests` faz parte da estrutura padrão do Archetype para abrigar testes de integração ponta a ponta, porém não foram implementados nesta Sprint devido a limitações de tempo.

---

## Validação em Instância Limpa (Task F5.3)

Abaixo está o registro de validação da instalação do projeto a partir do zero. O objetivo é garantir que não há hardcodes dependentes da máquina de desenvolvimento original.

| Escopo Validado | Resultado | Detalhes |
| :--- | :---: | :--- |
| Instalação de Templates (`ui.content`) | ✅ | Templates editáveis aplicados corretamente no `/conf`. |
| Instalação de Código (`ui.apps`) | ✅ | Componentes e Clientlibs compiladas e injetadas. |
| Deploy de Lógica (`core`) | ✅ | Bundle `brumacafe.core` ativo sem erros no OSGi Console. |
| Inserção de Massa de Dados (`packages`) | ✅ | Imagens, Content Fragments e Árvore de Páginas importadas via `brumacafe-site-final-1.0.0.zip`. |

### Troubleshooting / Problemas Conhecidos

1. **AEM não sobe ou acusa limite de memória (Out of Memory):**
   - **Causa:** O AEM 6.5 exige Java 11. O uso de Java 17, 21 ou falta de parâmetros de Heap causa falha no `java.lang.Runtime`.
   - **Solução:** Assegure o uso exclusivo do JDK 11 (`java -version`).
2. **Componentes sem estilo no primeiro deploy:**
   - **Causa:** Cache no Dispatcher ou no navegador.
   - **Solução:** Rode `mvn clean install`, limpe o cache do navegador e recarregue a página com `Shift + F5`.
3. **Página exibe apenas código JSON ou não renderiza:**
   - **Causa:** Falha de registro no Bundle `core` ou `ui.apps` ausente.
   - **Solução:** Confirme no OSGi Bundles Console (`/system/console/bundles`) se o `Bruma Cafe - Core` está com status `Active`. Se não, reveja os logs (`error.log`).

---

## Documentação Técnica das Frentes

### Frente 4 — Hub de Conteúdo: Validação de Performance (QueryBuilder)

#### Tarefa F4.4: Validação e Otimização da Consulta de Artigos

Para assegurar a estabilidade do repositório JCR e prevenir gargalos de processamento e consumo excessivo de memória na JVM ao carregar listagens de artigos, a busca automática implementada no `ArticleListModel` foi construída utilizando a API nativa do `QueryBuilder` com paginação estrita (`p.limit`).

**Parâmetros da Consulta (Query Predicates):**

```text
path=/content/brumacafe
type=cq:Page
property=jcr:content/cq:template
property.value=/conf/brumacafe/settings/wcm/templates/pagina-de-artigo
orderby=@jcr:content/cq:lastModified
orderby.sort=desc
p.limit=4
```

---

## Limitações Conhecidas

Para manter a transparência sobre o MVP construído nesta Sprint:

| Limitação | Detalhes |
| :--- | :--- |
| **E-mails do Formulário** | O formulário de contato captura e valida os dados, gravando-os no repositório AEM. Nenhum e-mail real é disparado, pois não há servidor SMTP configurado neste ambiente. |
| **Botão "Comprar" da Vitrine** | O botão de compra dos produtos da vitrine aponta para `#` como placeholder. A URL real da loja da Bruma pode ser configurada futuramente no Console OSGi, sem necessidade de novo *build*. |

## Histórico de Problemas e Soluções

Durante o desenvolvimento e as integrações do projeto, foram identificadas algumas dificuldades que exigiram ajustes para garantir a consistência e a manutenção do código.

### Versionamento e consistência do código

Foram encontradas dificuldades relacionadas ao versionamento e à manutenção de uma mesma base de código entre os desenvolvedores. Algumas alterações e configurações precisaram ser ajustadas para garantir que todos trabalhassem sobre a mesma estrutura e comportamento do projeto.

**Situação atual:** Resolvido. O fluxo de versionamento e a organização do código foram ajustados ao longo do desenvolvimento.

### Padronização de estilização

Também foram encontradas dificuldades para manter uma estilização consistente entre os componentes, principalmente durante a criação e manutenção dos estilos.

Foi necessário realizar ajustes na organização dos estilos e na forma de estilizar os componentes para manter um padrão visual e facilitar a manutenção do frontend.

**Situação atual:** Resolvido. Os estilos foram revisados e padronizados ao final do desenvolvimento.
---

## Equipe

### Discentes

| Nome     | Frente Principal                  | GitHub                                            |
| :------- | :-------------------------------- | :------------------------------------------------ |
| Samuel   | Frente 1 — Header e Identidade   | [@samuelcosta-pd](https://github.com/samuelcosta-pd)                      |
| Antonni  | Frente 2 — Cafés e Integração | [@AntonniSMoraes](https://github.com/AntonniSMoraes)                     |
| Emiliano | Frente 3 — Componentes           | [@Emiliano-Souza](https://github.com/Emiliano-Souza)                    |
| Jhonatan | Frente 4 — Hub de Conteúdo      | [@jhonatanLobo](https://github.com/jhonatanLobo)                    |
| Gustavo  | Frente 5 — Formulário e Entrega | [@gustavodanjos](https://github.com/gustavodanjos) |
