# Guia Técnico — Gerador de Documentos Betha

Documentação da estrutura de arquivos, responsabilidades e fluxo do projeto.

---

## Estrutura de Arquivos

```
betha-pdf-generator/
│
├── index.html              # Estrutura HTML da aplicação
├── style.css               # Estilos globais
├── entidades.json          # Lista de municípios para autocomplete
├── guia-tecnico.md         # Este arquivo
│
├── js/                     # JavaScript de suporte e orquestração
│   ├── app.js              # Inicialização, globals e registro de tipos
│   ├── ui.js               # Interações da interface
│   ├── collect.js          # Leitura do formulário e geração
│   ├── rich-text.js        # Editor e renderização de texto rico
│   └── sheet-config.js     # Autocomplete do campo Entidade
│
└── builders/               # Um arquivo por tipo de documento
    ├── pdf-shared.js       # Contexto PDF, cabeçalho e rodapé compartilhados
    ├── pdf-guide.js        # Builder PDF — Guia Passo a Passo / Técnico
    ├── pdf-changelog.js    # Builder PDF — Registro de Alterações
    ├── pdf-operational.js  # Builder PDF — Relatório Operacional
    └── word-builders.js    # Builders Word (.docx) para todos os tipos
```

---

## Responsabilidade de cada arquivo

### `app.js`
Ponto de entrada da aplicação. Contém:
- Variáveis globais de estado (`currentDocType`, `sectionCounter`, `logoBase64` etc.)
- `DOC_REGISTRY` — mapa central dos tipos de documento (guide, changelog, operational). Para adicionar um novo tipo de documento, basta criar uma nova entrada aqui apontando para os builders correspondentes.
- `DOMContentLoaded` — inicializa todos os módulos e registra eventos globais
- `initDarkMode` / `toggleDarkMode`
- `loadLogo`, `selectDocType`, `pdfFilename`, `formatDate`, `formatDateLong`, `showToast`

---

### `ui.js`
Toda a lógica de interação com a interface. Contém:
- `addSection`, `removeSection`, `renumberSteps` — gerenciamento de seções
- `addStep`, `removeStep` — gerenciamento de passos dentro de seções
- `addEntry`, `removeEntry` — gerenciamento de entradas do changelog
- `addOpSection`, `removeOpSection`, `addOpItem`, `addOpSubItem` — gerenciamento do relatório operacional
- `handleContainerClick`, `handleContainerChange` — delegação de eventos das seções
- `handleEntriesClick`, `handleEntriesInput` — delegação de eventos do changelog
- `handleOpContainerClick` — delegação de eventos do operacional
- `handleImageUpload`, `clearImage` — upload de imagens nos passos
- `getAlertOptions` — gera as opções de alerta conforme o tipo de documento ativo

---

### `collect.js`
Lê os dados do formulário e aciona a geração do documento. Contém:
- `collectData()` — percorre todos os campos da tela e retorna um objeto estruturado `{ doc, settings, sections/entries/opSections }`
- `generate()` — valida os dados, chama o builder correto (PDF ou Word) via `DOC_REGISTRY`

---

### `rich-text.js`
Suporte a texto rico (negrito/itálico) na interface e no PDF. Contém:
- `parseRichHTMLSegments(html)` — converte innerHTML de um `contenteditable` em segmentos `[{ text, bold, italic }]`
- `richHTMLToPlain(html)` — extrai texto puro descartando formatação
- `buildRichLines(doc, segments, maxW, size)` — quebra segmentos em linhas que cabem na largura do PDF
- `drawRichLines(doc, lines, x, y, size)` — renderiza as linhas com alternância de fonte bold/italic no jsPDF
- `htmlToWordRuns(D, html, opts)` — converte HTML em objetos `TextRun` do docx para o Word
- `initRichText()` — inicializa o toolbar flutuante de negrito/itálico e atalhos Ctrl+B / Ctrl+I

---

### `pdf-shared.js`
Base compartilhada por todos os builders PDF. Contém:
- `BRAND` — constantes de cor da identidade visual Betha
- `createPDFCtx(doc)` — cria o contexto de renderização com helpers (`setColor`, `wrappedLines`, `lineHeight`, `safeText`) e constantes de layout (`PW`, `PH`, `ML`, `MR`, `MB`, `CW`)
- `drawPDFSharedHeader(doc, data, accentRGB, label)` — renderiza o cabeçalho padrão (logo BETHA, título, entidade, metadados)
- `drawPDFFooter(doc, ctx, data, accentRGB, page)` — renderiza o rodapé (endereço Betha + telefones + número de página)
- `drawPDFContinuationHeader(doc, ctx, data, accentRGB)` — cabeçalho reduzido para páginas de continuação

---

### `pdf-guide.js`
Builder PDF para os tipos **Guia Passo a Passo** e **Técnico**. Contém:
- `buildSectionedDocPDF(data)` — renderiza cabeçalho, seções numeradas, passos com círculo numerado, alertas e imagens

---

### `pdf-changelog.js`
Builder PDF para o tipo **Registro de Alterações**. Contém:
- `buildChangelogDocPDF(data)` — renderiza cabeçalho, tabela com colunas TIPO (badge colorido) e DESCRIÇÃO (título + texto)

---

### `pdf-operational.js`
Builder PDF para o tipo **Relatório Operacional**. Contém:
- `buildOperationalDocPDF(data)` — renderiza cabeçalho, categorias com itens e subitens em lista, seção de conclusão e bloco de assinaturas

---

### `word-builders.js`
Builders Word (.docx) para todos os tipos de documento. Usa a biblioteca `docx.js`. Contém:
- `buildSectionedDocWord(data)` — Word do guia/técnico
- `buildChangelogDocWord(data)` — Word do changelog
- `buildOperationalDocWord(data)` — Word do operacional
- Helpers internos: `makeWordHeader`, `makeWordFooter`, `makeWordMetaTable`, `collectWordMetaItems`, `downloadBlob`, `getImageDimensions`, `base64ToUint8Array`

---

### `sheet-config.js`
Autocomplete do campo **Entidade**. Contém:
- `loadSheetEntidades()` — carrega `entidades.json` via fetch
- `initEntityAutocomplete()` — inicializa o dropdown de sugestões com busca por substring, navegação por teclado e seleção por clique

---

### `entidades.json`
Lista estática de municípios/entidades usada pelo autocomplete.  
**Para atualizar:** edite este arquivo e faça um novo commit/deploy.  
Formato:
```json
{
  "entities": ["NOME DO MUNICÍPIO", "..."]
}
```

---

## Fluxo de execução

```
Usuário preenche o formulário
        ↓
generate() [collect.js]
        ↓
collectData() lê todos os campos
        ↓
DOC_REGISTRY[tipo].buildPDF(data) [app.js]
        ↓
buildXxxDocPDF(data) [pdf-guide / pdf-changelog / pdf-operational]
        ↓
drawPDFSharedHeader + conteúdo + drawPDFFooter [pdf-shared.js]
        ↓
drawRichLines / buildRichLines [rich-text.js]
        ↓
doc.save() — download do PDF
```

---

## Como adicionar um novo tipo de documento

1. Crie `builders/pdf-novodoc.js` com a função `buildNovoDocPDF(data)`
2. Crie entrada em `word-builders.js` (ou `builders/word-novodoc.js`) com `buildNovoDocWord(data)`
3. Adicione uma entrada em `DOC_REGISTRY` no `js/app.js`:
```js
novodoc: {
  label:     'NOME DO TIPO',
  accentRGB: BRAND.GUIDE_ACC,
  accentHex: '586EAC',
  buildPDF:  (data) => buildNovoDocPDF(data),
  buildWord: (data) => buildNovoDocWord(data),
}
```
4. Adicione o script no `index.html` antes do `js/app.js`:
```html
<script src="builders/pdf-novodoc.js"></script>
```
5. Adicione a opção visual no seletor de tipos em `index.html`

---

## Dependências externas

| Biblioteca | Versão | Uso |
|---|---|---|
| jsPDF | 2.5.1 | Geração de PDF no browser |
| docx.js | CDN | Geração de Word (.docx) no browser |
| Inter (Google Fonts) | — | Tipografia da interface |

---

## Deploy

O projeto é hospedado no **GitHub Pages** (site estático, sem servidor).  
Repositório: [github.com/ArturCadorin/gerador-documentos-betha](https://github.com/ArturCadorin/gerador-documentos-betha)  
URL pública: [arturcadorin.github.io/gerador-documentos-betha](https://arturcadorin.github.io/gerador-documentos-betha)

Para publicar uma atualização:
```bash
git add .
git commit -m "descrição da mudança"
git push
```
O GitHub Pages atualiza automaticamente em ~1 minuto.
