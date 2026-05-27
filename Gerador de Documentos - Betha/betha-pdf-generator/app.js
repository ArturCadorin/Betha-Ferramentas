/* ──────────────────────────────────────────
   Betha Doc Generator – app.js
   ────────────────────────────────────────── */

let sectionCounter = 0;
let stepCounter = 0;
let entryCounter = 0;
let currentDocType = 'guide';
let logoBase64 = null;

// ── Paleta de marca compartilhada ─────────
const BRAND = {
  WORDMARK:  [95, 118, 180],
  SLOGAN:    [148, 165, 210],
  GUIDE_ACC: [88, 110, 172],
  GUIDE_BG:  [88, 110, 172],
  TECH_ACC:  [13, 148, 120],
  TECH_BG:   [30, 41, 59],
};

// ── Registro de tipos de documento ────────
// Para adicionar um novo tipo: crie uma entrada aqui + uma função builder.
// Nenhum outro código precisa ser alterado.
const DOC_REGISTRY = {
  guide: {
    label:        'GUIA PASSO A PASSO',
    accentRGB:    BRAND.GUIDE_ACC,
    bgRGB:        BRAND.GUIDE_BG,
    accentHex:    '586EAC',
    wordTypeLabel:'GUIA PASSO A PASSO • BETHA SISTEMAS',
    alertCfg: {
      info:    { fill: [243, 248, 255], border: [37, 99, 235],  label: 'Dica' },
      warning: { fill: [255, 253, 242], border: [217, 119, 6],  label: 'Atenção' },
      danger:  { fill: [255, 245, 245], border: [220, 38, 38],  label: 'Crítico' },
    },
    alertWordCfg: {
      info:    { fill: 'EFF6FF', border: '2563EB', label: 'DICA' },
      warning: { fill: 'FFFBEB', border: 'D97706', label: 'ATENÇÃO' },
      danger:  { fill: 'FFF5F5', border: 'DC2626', label: 'CRÍTICO' },
    },
    buildPDF:  (data) => buildSectionedDocPDF(data),
    buildWord: (data) => buildSectionedDocWord(data),
  },
  technical: {
    label:        'DOCUMENTAÇÃO TÉCNICA',
    accentRGB:    BRAND.TECH_ACC,
    bgRGB:        BRAND.TECH_BG,
    accentHex:    '0D9478',
    wordTypeLabel:'DOCUMENTAÇÃO TÉCNICA • BETHA SISTEMAS',
    alertCfg: {
      info:    { fill: [243, 253, 251], border: [13, 148, 136], label: 'Nota' },
      warning: { fill: [255, 253, 242], border: [217, 119, 6],  label: 'Aviso' },
      danger:  { fill: [255, 249, 240], border: [234, 88, 12],  label: 'Importante' },
    },
    alertWordCfg: {
      info:    { fill: 'F0FDF9', border: '0D9488', label: 'NOTA' },
      warning: { fill: 'FFFBEB', border: 'D97706', label: 'AVISO' },
      danger:  { fill: 'FFF7F0', border: 'EA580C', label: 'IMPORTANTE' },
    },
    buildPDF:  (data) => buildSectionedDocPDF(data),
    buildWord: (data) => buildSectionedDocWord(data),
  },
  changelog: {
    label:     'REGISTRO DE ALTERAÇÕES',
    accentRGB: BRAND.GUIDE_ACC,
    bgRGB:     BRAND.GUIDE_BG,
    accentHex: '586EAC',
    buildPDF:  (data) => buildChangelogDocPDF(data),
    buildWord: (data) => buildChangelogDocWord(data),
  },
};

// ── PDF: contexto de renderização ─────────
// Centraliza constantes e helpers do jsPDF.
// Uso: const ctx = createPDFCtx(doc); const { ML, CW } = ctx;
function createPDFCtx(doc) {
  const PW = 210, PH = 297, ML = 15, MR = 15, MB = 22, CW = PW - ML - MR;
  const TEXT   = [26, 29, 46];
  const MUTED  = [107, 114, 128];
  const WHITE  = [255, 255, 255];
  const BORDER = [229, 231, 235];
  const self = {
    PW, PH, ML, MR, MB, CW, TEXT, MUTED, WHITE, BORDER,
    setColor(rgb, type = 'fill') {
      if (type === 'fill') doc.setFillColor(...rgb);
      if (type === 'text') doc.setTextColor(...rgb);
      if (type === 'draw') doc.setDrawColor(...rgb);
    },
    safeText(str) {
      return (str || '').replace(/['']/g, "'").replace(/[""]/g, '"');
    },
    wrappedLines(text, maxW, size) {
      doc.setFontSize(size);
      return doc.splitTextToSize(self.safeText(text), maxW);
    },
    lineHeight(size) { return size * 0.3528 * 1.4; },
  };
  return self;
}

// ── PDF: rodapé compartilhado ─────────────
function drawPDFFooter(doc, ctx, data, accentRGB, page) {
  if (!data.settings.showFooter && !data.settings.showPageNumbers) return;
  const { ML, MR, PW, PH, MUTED } = ctx;
  const fy = PH - 20;

  ctx.setColor(accentRGB, 'draw');
  doc.setLineWidth(0.3);
  doc.line(ML, fy, PW - MR, fy);

  if (data.settings.showFooter) {
    ctx.setColor(accentRGB, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7.5);
    doc.text('Matriz Betha Sistemas', ML, fy + 5);

    ctx.setColor(MUTED, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7);
    doc.text('Rua Júlio Gaidzinski, 320,', ML, fy + 9.5);
    doc.text('88811-000, Pio Corrêa / Criciúma - SC', ML, fy + 13.5);

    ctx.setColor(accentRGB, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7.5);
    doc.text('48 3431-0733', PW - MR, fy + 5, { align: 'right' });

    ctx.setColor(MUTED, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7);
    doc.text('Atendimento técnico', PW - MR, fy + 9.5, { align: 'right' });

    ctx.setColor(accentRGB, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7);
    doc.text('0800 600 0735', PW - MR, fy + 13.5, { align: 'right' });
  }

  if (data.settings.showPageNumbers) {
    ctx.setColor(MUTED, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7);
    const pnY = data.settings.showFooter ? fy + 9.5 : fy + 5;
    doc.text(`Pagina ${page}`, PW / 2, pnY, { align: 'center' });
  }
}

// ── PDF: cabeçalho de continuação compartilhado ──
// Renderiza o mini-header nas páginas 2+. Retorna o novo Y.
function drawPDFContinuationHeader(doc, ctx, data, bgRGB) {
  const { ML, CW, WHITE } = ctx;
  ctx.setColor(bgRGB, 'fill');
  doc.rect(0, 0, 210, 11, 'F');

  if (data.settings.showLogo && logoBase64) {
    doc.addImage(logoBase64, 'PNG', ML, 1.5, 8, 8);
  }

  ctx.setColor(WHITE, 'text');
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(8);
  const xT = data.settings.showLogo && logoBase64 ? ML + 11 : ML;
  doc.text(ctx.safeText(data.doc.title || ''), xT, 7.5, { maxWidth: CW - 12 });

  return 18;
}

// ── Word: metadados compartilhados ────────

// Retorna o array de strings de metadados do documento.
function collectWordMetaItems(data) {
  return [
    data.doc.entity && `Entidade: ${data.doc.entity}`,
    data.doc.module && `Módulo: ${data.doc.module}`,
    data.doc.author && `Responsável: ${data.doc.author}`,
    data.doc.date   && `Data: ${formatDate(data.doc.date)}`,
    data.doc.ticket && `Chamado: ${data.doc.ticket}`,
  ].filter(Boolean);
}

// Retorna os elementos Word da tabela de metadados (array pronto para push).
function makeWordMetaTable(D, accentHex, items) {
  if (!items.length) return [];
  const NONE = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };
  return [
    new D.Table({
      width: { size: 100, type: D.WidthType.PERCENTAGE },
      borders: { top: NONE, bottom: NONE, left: NONE, right: NONE, insideH: NONE, insideV: NONE },
      rows: [new D.TableRow({
        children: [new D.TableCell({
          shading: { fill: 'F8FAFC', type: D.ShadingType.CLEAR, color: 'auto' },
          borders: { top: { style: D.BorderStyle.SINGLE, size: 4, color: accentHex, space: 1 }, bottom: NONE, left: NONE, right: NONE },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: items.map(item => new D.Paragraph({
            children: [new D.TextRun({ text: item, size: 18, color: '374151' })],
            spacing: { before: 40, after: 40 },
          })),
        })],
      })],
    }),
    new D.Paragraph({ spacing: { after: 320 }, children: [] }),
  ];
}

// ── Init ──────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('doc-date').valueAsDate = new Date();
  loadLogo();
  addSection();

  document.getElementById('btn-add-section').addEventListener('click', addSection);
  document.getElementById('btn-add-entry').addEventListener('click', addEntry);
  document.getElementById('btn-generate-nav').addEventListener('click', generate);
  document.getElementById('btn-generate-main').addEventListener('click', generate);

  document.getElementById('sections-container').addEventListener('click', handleContainerClick);
  document.getElementById('sections-container').addEventListener('change', handleContainerChange);
  document.getElementById('entries-container').addEventListener('click', handleEntriesClick);
  document.getElementById('entries-container').addEventListener('input', handleEntriesInput);

  document.querySelectorAll('.type-option').forEach(card => {
    card.addEventListener('click', () => selectDocType(card.dataset.type));
  });
});

function loadLogo() {
  logoBase64 = 'data:image/png;base64,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';
}

// ── Type selector ─────────────────────────

function selectDocType(type) {
  if (type === currentDocType) return;
  currentDocType = type;

  document.querySelectorAll('.type-option').forEach(c =>
    c.classList.toggle('active', c.dataset.type === type)
  );

  const sectionsCard  = document.getElementById('sections-card');
  const changelogCard = document.getElementById('changelog-card');

  if (type === 'changelog') {
    sectionsCard.style.display  = 'none';
    changelogCard.style.display = '';
    if (!document.querySelector('.changelog-entry')) addEntry();
  } else {
    sectionsCard.style.display  = '';
    changelogCard.style.display = 'none';
    if (!document.querySelector('.section-block')) addSection();

    const title = type === 'technical' ? 'Seções e Conteúdo' : 'Seções e Passos';
    const desc  = type === 'technical'
      ? 'Organize o conteúdo técnico em seções com blocos de texto e código'
      : 'Organize o conteúdo em seções com passos numerados';
    document.getElementById('sections-card-title').textContent = title;
    document.getElementById('sections-card-desc').textContent  = desc;

    updateStepAlertLabels(type);
  }
}

function getAlertOptions(type) {
  if (type === 'technical') {
    return `
      <option value="">Sem nota</option>
      <option value="info">📌 Nota</option>
      <option value="warning">⚠️ Aviso</option>
      <option value="danger">⚡ Importante</option>`;
  }
  return `
    <option value="">Sem alerta</option>
    <option value="info">💡 Dica</option>
    <option value="warning">⚠️ Atenção</option>
    <option value="danger">🚨 Crítico</option>`;
}

function updateStepAlertLabels(type) {
  document.querySelectorAll('.step-alert-select').forEach(sel => {
    const val = sel.value;
    sel.innerHTML = getAlertOptions(type);
    sel.value = val;
  });
}

// ── Event delegation – sections ───────────

function handleContainerClick(e) {
  const t = e.target;

  if (t.closest('.btn-toggle-section')) { t.closest('.section-block').classList.toggle('collapsed'); return; }
  if (t.closest('.btn-remove-section')) { removeSection(t.closest('.section-block')); return; }
  if (t.closest('.btn-add-step'))       { addStep(t.closest('.section-block').dataset.sid); return; }
  if (t.closest('.btn-remove-step'))    { removeStep(t.closest('.step-block')); return; }
  if (t.closest('.btn-add-image'))      { t.closest('.step-block').querySelector('.img-input').click(); return; }
  if (t.closest('.btn-remove-img'))     { clearImage(t.closest('.step-block')); return; }
}

function handleContainerChange(e) {
  const t = e.target;

  if (t.classList.contains('step-alert-select')) {
    const alertRow = t.closest('.step-block').querySelector('.step-alert-row');
    alertRow.style.display = t.value ? 'flex' : 'none';
    return;
  }
  if (t.classList.contains('img-input') && t.files[0]) {
    handleImageUpload(t, t.closest('.step-block'));
    return;
  }
}

// ── Event delegation – entries ────────────

function handleEntriesInput(e) {
  if (e.target.classList.contains('entry-title')) {
    const summary = e.target.closest('.changelog-entry').querySelector('.entry-collapsed-title');
    if (summary) summary.textContent = e.target.value.trim() || 'Sem título';
  }
}

function handleEntriesClick(e) {
  const t = e.target;
  if (t.closest('.btn-toggle-entry')) {
    t.closest('.changelog-entry').classList.toggle('collapsed');
    return;
  }
  if (t.closest('.btn-remove-entry')) {
    removeEntry(t.closest('.changelog-entry'));
  }
}

// ── Sections ──────────────────────────────

function addSection() {
  document.querySelectorAll('.section-block').forEach(s => s.classList.add('collapsed'));

  sectionCounter++;
  const sid = `s${sectionCounter}`;

  const el = document.createElement('div');
  el.className = 'section-block';
  el.dataset.sid = sid;
  el.innerHTML = `
    <div class="section-head">
      <span class="section-drag" title="Arrastar">⠿</span>
      <input type="text" class="section-title-input" placeholder="Título da Seção (ex: Acesso ao Sistema)">
      <div class="section-actions">
        <button class="btn-icon btn-add-step" title="Adicionar passo">+ Passo</button>
        <button class="btn-icon btn-toggle-section" title="Minimizar / Expandir">
          <svg class="toggle-arrow" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="6 9 12 15 18 9"/></svg>
        </button>
        <button class="btn-icon danger btn-remove-section" title="Remover seção">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
    </div>
    <div class="section-body">
      <div class="steps-wrap" id="steps-${sid}"></div>
      <div class="add-step-row">
        <button class="btn btn-ghost btn-sm btn-add-step">+ Adicionar Passo</button>
      </div>
    </div>
  `;

  const container = document.getElementById('sections-container');
  const empty = document.getElementById('empty-sections');
  if (empty) empty.remove();
  container.appendChild(el);

  addStep(sid);
}

function removeSection(el) {
  el.style.opacity = '0';
  el.style.transform = 'scale(.97)';
  el.style.transition = 'opacity .2s, transform .2s';
  setTimeout(() => {
    el.remove();
    if (!document.querySelector('.section-block')) {
      document.getElementById('sections-container').innerHTML =
        `<div class="empty-state" id="empty-sections">
          <div class="empty-icon">📋</div>
          <p>Nenhuma seção criada ainda.</p>
          <p class="empty-hint">Clique em "Adicionar Seção" para começar.</p>
        </div>`;
    }
  }, 200);
}

// ── Steps ─────────────────────────────────

function addStep(sid) {
  stepCounter++;
  const stepsWrap = document.getElementById(`steps-${sid}`);
  const num = stepsWrap.querySelectorAll('.step-block').length + 1;

  const el = document.createElement('div');
  el.className = 'step-block';
  el.dataset.stepId = `step-${stepCounter}`;
  el.innerHTML = `
    <div class="step-num">${num}</div>
    <div class="step-content">
      <textarea class="step-text" placeholder="Descreva este passo com clareza..." rows="2"></textarea>
      <div class="step-opts">
        <select class="step-alert-select">${getAlertOptions(currentDocType)}</select>
        <button class="btn-icon btn-add-image" type="button">🖼 Imagem</button>
        <button class="btn-icon danger btn-remove-step" type="button" title="Remover passo">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
        <input type="file" class="img-input" accept="image/*" style="display:none">
      </div>
      <div class="step-alert-row" style="display:none">
        <input type="text" class="step-alert-input" placeholder="Texto do alerta...">
      </div>
      <div class="step-img-preview" style="display:none">
        <img src="" alt="Preview">
        <button class="btn-remove-img" type="button">✕ Remover</button>
      </div>
    </div>
  `;

  stepsWrap.appendChild(el);
}

function removeStep(el) {
  const stepsWrap = el.closest('.steps-wrap');
  el.style.opacity = '0';
  el.style.transition = 'opacity .15s';
  setTimeout(() => {
    el.remove();
    renumberSteps(stepsWrap);
  }, 150);
}

function renumberSteps(stepsWrap) {
  stepsWrap.querySelectorAll('.step-block').forEach((s, i) => {
    s.querySelector('.step-num').textContent = i + 1;
  });
}

// ── Images ────────────────────────────────

function handleImageUpload(input, stepEl) {
  const file = input.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = (e) => {
    const b64 = e.target.result;
    stepEl.dataset.img = b64;
    const preview = stepEl.querySelector('.step-img-preview');
    preview.querySelector('img').src = b64;
    preview.style.display = 'block';
  };
  reader.readAsDataURL(file);
}

function clearImage(stepEl) {
  delete stepEl.dataset.img;
  const preview = stepEl.querySelector('.step-img-preview');
  preview.querySelector('img').src = '';
  preview.style.display = 'none';
  stepEl.querySelector('.img-input').value = '';
}

// ── Changelog entries ─────────────────────

function addEntry() {
  document.querySelectorAll('.changelog-entry').forEach(e => e.classList.add('collapsed'));

  entryCounter++;
  const eid = `e${entryCounter}`;

  const el = document.createElement('div');
  el.className = 'changelog-entry';
  el.dataset.eid = eid;
  el.innerHTML = `
    <div class="entry-top-row">
      <div class="entry-field-group entry-type-group">
        <label>Tipo</label>
        <select class="entry-type">
          <option value="addition">✅ Criação</option>
          <option value="fix">🔧 Correção</option>
          <option value="change">📝 Alteração</option>
          <option value="removal">🗑️ Remoção</option>
          <option value="improvement">⚡ Melhoria</option>
          <option value="security">🔒 Segurança</option>
        </select>
      </div>
      <div class="entry-collapsed-title"></div>
      <div class="entry-row-actions">
        <button class="btn-icon btn-toggle-entry" title="Minimizar / Expandir">
          <svg class="toggle-arrow" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="6 9 12 15 18 9"/></svg>
        </button>
        <button class="btn-icon danger btn-remove-entry" title="Remover entrada">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
    </div>
    <div class="entry-body">
      <div class="entry-title-row">
        <label>Título da Alteração</label>
        <input type="text" class="entry-title" placeholder="Ex: Evento 75 — Horas Férias" autocomplete="off">
      </div>
      <div class="entry-bottom-row">
        <label>Descrição</label>
        <textarea class="entry-desc" placeholder="Descreva a alteração realizada..." rows="2"></textarea>
      </div>
    </div>
  `;

  const container = document.getElementById('entries-container');
  const empty = document.getElementById('empty-entries');
  if (empty) empty.remove();
  container.appendChild(el);
}

function removeEntry(el) {
  el.style.opacity = '0';
  el.style.transition = 'opacity .15s';
  setTimeout(() => {
    el.remove();
    if (!document.querySelector('.changelog-entry')) {
      document.getElementById('entries-container').innerHTML =
        `<div class="empty-state" id="empty-entries">
          <div class="empty-icon">📝</div>
          <p>Nenhuma entrada criada ainda.</p>
          <p class="empty-hint">Clique em "Adicionar Entrada" para começar.</p>
        </div>`;
    }
  }, 150);
}

// ── Data collection ───────────────────────

function collectData() {
  const doc = {
    title:   document.getElementById('doc-title').value.trim(),
    entity:  document.getElementById('doc-entity').value.trim(),
    module:  document.getElementById('doc-module').value.trim(),
    ticket:  document.getElementById('doc-ticket').value.trim(),
    author:  document.getElementById('doc-author').value.trim(),
    date:    document.getElementById('doc-date').value,
    type:    currentDocType,
  };

  const settings = {
    showLogo:        document.getElementById('show-logo').checked,
    showFooter:      document.getElementById('show-footer').checked,
    showPageNumbers: document.getElementById('show-page-numbers').checked,
  };

  if (currentDocType === 'changelog') {
    const entries = [];
    document.querySelectorAll('.changelog-entry').forEach(el => {
      entries.push({
        type:  el.querySelector('.entry-type').value,
        title: el.querySelector('.entry-title').value.trim(),
        desc:  el.querySelector('.entry-desc').value.trim(),
      });
    });
    return { doc, settings, entries };
  }

  const sections = [];
  document.querySelectorAll('.section-block').forEach(secEl => {
    const steps = [];
    secEl.querySelectorAll('.step-block').forEach(stepEl => {
      steps.push({
        text:      stepEl.querySelector('.step-text').value.trim(),
        alertType: stepEl.querySelector('.step-alert-select').value,
        alertText: stepEl.querySelector('.step-alert-input').value.trim(),
        image:     stepEl.dataset.img || null,
      });
    });
    sections.push({
      title: secEl.querySelector('.section-title-input').value.trim(),
      steps,
    });
  });

  return { doc, settings, sections };
}

// ── Generate ──────────────────────────────

async function generate() {
  const data = collectData();

  if (!data.doc.title) {
    showToast('Informe o título do documento antes de gerar o PDF.', 'error');
    document.getElementById('doc-title').focus();
    return;
  }

  if (currentDocType === 'changelog') {
    if (!data.entries || data.entries.length === 0) {
      showToast('Adicione pelo menos uma entrada de alteração.', 'error');
      return;
    }
  } else {
    if (!data.sections || data.sections.length === 0) {
      showToast('Adicione pelo menos uma seção com conteúdo.', 'error');
      return;
    }
  }

  showToast('Gerando PDF…');

  try {
    await DOC_REGISTRY[currentDocType].buildPDF(data);
  } catch (err) {
    console.error(err);
    showToast('Erro ao gerar o PDF. Veja o console para detalhes.', 'error');
  }
}

// ── PDF helpers ───────────────────────────

function pdfFilename(title) {
  return (title || 'betha-documento')
    .toLowerCase()
    .normalize('NFD').replace(/[̀-ͯ]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '');
}

// ── PDF: header compartilhado ─────────────
// Renderiza o cabeçalho principal em todos os tipos de documento.
// Retorna o Y inicial do corpo (após metadados).
function drawPDFSharedHeader(doc, data, accentRGB, headerLabel) {
  const ML = 15, MR = 15, PW = 210, CW = PW - ML - MR;
  const MUTED = [107, 114, 128];
  const leftZoneW = 65;
  const logoY = 8;

  function sc(rgb, t) {
    if (t === 'text') doc.setTextColor(...rgb);
    if (t === 'draw') doc.setDrawColor(...rgb);
  }
  function st(s) { return (s || '').replace(/['']/g, "'").replace(/[""]/g, '"'); }
  function lh(sz)  { return sz * 0.3528 * 1.4; }

  if (data.settings.showLogo) {
    // BETHA: bold italic, alinhado à margem esquerda
    doc.setFont('helvetica', 'bolditalic');
    doc.setFontSize(18);
    sc(BRAND.WORDMARK, 'text');
    doc.text('BETHA', ML, logoY + 13);

    // Slogan: à direita do wordmark, centralizado verticalmente com ele
    const bethaW = doc.getTextWidth('BETHA') + 3;
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(6.5);
    sc(BRAND.SLOGAN, 'text');
    doc.text('Tudo que a sua cidade', ML + bethaW, logoY + 9.5);
    doc.text('pode se tornar',        ML + bethaW, logoY + 13);
  }

  // Lado direito: apenas o rótulo do tipo
  const rX = ML + leftZoneW + 5;
  if (headerLabel) {
    sc(MUTED, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7.5);
    doc.text(headerLabel, rX, logoY + 13);
  }

  // Linha separadora
  const sepY = logoY + 20;
  sc(accentRGB, 'draw');
  doc.setLineWidth(0.4);
  doc.line(ML, sepY, PW - MR, sepY);

  // Título — abaixo da linha, largura total
  const titleY = sepY + 6;
  doc.setTextColor(20, 30, 70);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(13);
  const titleLines = doc.splitTextToSize(st(data.doc.title || 'Documento'), CW);
  doc.text(titleLines, ML, titleY);
  const afterTitle = titleY + titleLines.length * lh(13);

  // Metadados — mais próximos do título, fonte maior
  const metaParts = [
    data.doc.entity && `Entidade: ${data.doc.entity}`,
    data.doc.module && `Módulo: ${data.doc.module}`,
    data.doc.author && `Responsável: ${data.doc.author}`,
    data.doc.date   && `Data: ${formatDate(data.doc.date)}`,
    data.doc.ticket && `Chamado: ${data.doc.ticket}`,
  ].filter(Boolean);

  if (metaParts.length) {
    const metaY = afterTitle + 1.5;
    doc.setTextColor(...MUTED);
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(8.5);
    doc.text(metaParts.join('   |   '), ML, metaY, { maxWidth: CW });
    return metaY + 9;
  }
  return afterTitle + 7;
}

// ── PDF: Guide + Technical ────────────────

async function buildSectionedDocPDF(data) {
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ orientation: 'p', unit: 'mm', format: 'a4' });
  const ctx = createPDFCtx(doc);
  const { PW, PH, ML, MR, MB, CW, TEXT, WHITE, BORDER } = ctx;

  const reg    = DOC_REGISTRY[data.doc.type] || DOC_REGISTRY.guide;
  const ACCENT = reg.accentRGB;
  const ALERTS = reg.alertCfg;

  let page = 1, y = 0;

  function ensureSpace(needed) {
    if (y + needed > PH - MB) {
      drawPDFFooter(doc, ctx, data, ACCENT, page);
      doc.addPage(); page++;
      y = drawPDFContinuationHeader(doc, ctx, data, reg.bgRGB);
    }
  }

  function drawSection(section, index) {
    ensureSpace(16);
    ctx.setColor(ACCENT, 'fill');
    doc.rect(ML, y, 3, 7, 'F');
    ctx.setColor(ACCENT, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(11);
    doc.text(ctx.safeText(`${index + 1}. ${(section.title || 'Seção').toUpperCase()}`), ML + 5, y + 5.5);
    y += 10;
    ctx.setColor([200, 220, 220], 'draw');
    doc.setLineWidth(0.4);
    doc.line(ML, y, PW - MR, y);
    y += 6;
    section.steps.forEach((step, si) => { if (step.text || step.image) drawStep(step, si + 1); });
    y += 4;
  }

  function drawStep(step, num) {
    const lines = ctx.wrappedLines(step.text || '', CW - 14, 10);
    const textH = lines.length * ctx.lineHeight(10) + 2;
    ensureSpace(textH + 8);

    ctx.setColor(ACCENT, 'fill');
    doc.circle(ML + 3.5, y + 3.5, 3.5, 'F');
    ctx.setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    doc.text(String(num), ML + 3.5, y + 4.3, { align: 'center' });

    ctx.setColor(TEXT, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    doc.text(lines, ML + 10, y + 4);
    y += textH + 2;

    if (step.alertType && step.alertText) drawAlert(step.alertType, step.alertText);

    if (step.image) {
      const maxImgW = CW - 10, maxImgH = 70;
      const imgEl = new Image();
      imgEl.src = step.image;
      const ratio = imgEl.naturalWidth ? imgEl.naturalHeight / imgEl.naturalWidth : 0.6;
      const imgH = Math.min(maxImgH, maxImgW * ratio);
      ensureSpace(imgH + 6);
      ctx.setColor(BORDER, 'draw');
      doc.setLineWidth(0.3);
      doc.rect(ML + 8, y, maxImgW, imgH);
      doc.addImage(step.image, 'PNG', ML + 8, y, maxImgW, imgH, undefined, 'FAST');
      y += imgH + 6;
    }
    y += 3;
  }

  function drawAlert(type, text) {
    const cfg = ALERTS[type];
    if (!cfg) return;
    const lines = ctx.wrappedLines(text, CW - 26, 9);
    const h = lines.length * ctx.lineHeight(9) + 14;
    ensureSpace(h + 4);

    ctx.setColor(cfg.fill, 'fill');
    doc.roundedRect(ML + 8, y, CW - 8, h, 2.5, 2.5, 'F');
    ctx.setColor(cfg.border, 'fill');
    doc.roundedRect(ML + 8, y, 2, h, 1.5, 1.5, 'F');

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(6.5);
    const pillW = doc.getTextWidth(cfg.label) + 8;
    ctx.setColor(cfg.border, 'fill');
    doc.roundedRect(ML + 13.5, y + 3.5, pillW, 5.2, 2, 2, 'F');
    ctx.setColor(WHITE, 'text');
    doc.text(cfg.label, ML + 13.5 + pillW / 2, y + 7.4, { align: 'center' });

    ctx.setColor([75, 85, 99], 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.text(lines, ML + 13.5, y + 12);
    y += h + 3;
  }

  y = drawPDFSharedHeader(doc, data, ACCENT, reg.label);
  data.sections.forEach((section, i) => drawSection(section, i));
  drawPDFFooter(doc, ctx, data, ACCENT, page);

  doc.save(`${pdfFilename(data.doc.title)}.pdf`);
  showToast('PDF gerado com sucesso!', 'success');
}

// ── PDF: Changelog ────────────────────────

async function buildChangelogDocPDF(data) {
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ orientation: 'p', unit: 'mm', format: 'a4' });
  const ctx = createPDFCtx(doc);
  const { PW, PH, ML, MR, MB, CW, TEXT, WHITE, BORDER } = ctx;

  const reg    = DOC_REGISTRY.changelog;
  const BLUE   = reg.accentRGB;
  const BG_ALT = [248, 250, 252];

  const COL = { date: { x: ML, w: 32 }, content: { x: ML + 32, w: CW - 32 } };

  const TYPE_CFG = {
    addition:    { bg: [209, 250, 229], text: [6, 95, 70],   label: 'Criação' },
    fix:         { bg: [219, 234, 254], text: [30, 64, 175], label: 'Correção' },
    change:      { bg: [254, 243, 199], text: [146, 64, 14], label: 'Alteração' },
    removal:     { bg: [254, 226, 226], text: [153, 27, 27], label: 'Remoção' },
    improvement: { bg: [237, 233, 254], text: [91, 33, 182], label: 'Melhoria' },
    security:    { bg: [243, 244, 246], text: [55, 65, 81],  label: 'Segurança' },
  };

  let page = 1, y = 0;

  function ensureSpace(needed) {
    if (y + needed > PH - MB) {
      drawPDFFooter(doc, ctx, data, BLUE, page);
      doc.addPage(); page++;
      y = drawPDFContinuationHeader(doc, ctx, data, reg.bgRGB);
    }
  }

  function drawTableHeader() {
    ensureSpace(11);
    ctx.setColor(BLUE, 'fill');
    doc.rect(ML, y, CW, 11, 'F');
    ctx.setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7.5);
    doc.text('TIPO', COL.date.x + COL.date.w / 2, y + 6, { align: 'center' });
    doc.setFontSize(8);
    doc.text('DESCRIÇÃO', COL.content.x + 3, y + 6.5);
    y += 11;
    ctx.setColor(BORDER, 'draw');
    doc.setLineWidth(0.2);
    doc.line(ML, y, ML + CW, y);
  }

  function drawTableRow(entry, isAlt) {
    const contentX = COL.content.x + 3;
    const contentW = COL.content.w - 6;

    const titleLines = ctx.wrappedLines(entry.title || '—', contentW, 9.5);
    const titleH     = titleLines.length * ctx.lineHeight(9.5);
    const descText   = (entry.desc || '').trim();
    const descLines  = descText ? ctx.wrappedLines(descText, contentW, 9) : [];
    const rowH       = Math.max(16, titleH + (descLines.length > 0 ? 3 + descLines.length * ctx.lineHeight(9) : 0) + 8);

    ensureSpace(rowH + 1);

    if (isAlt) { ctx.setColor(BG_ALT, 'fill'); doc.rect(ML, y, CW, rowH, 'F'); }

    ctx.setColor(BORDER, 'draw');
    doc.setLineWidth(0.2);
    doc.line(ML, y + rowH, ML + CW, y + rowH);

    const cfg    = TYPE_CFG[entry.type] || TYPE_CFG.change;
    const labelW = cfg.label.length * 1.85 + 5;
    const badgeX = COL.date.x + (COL.date.w - labelW) / 2;
    const badgeY = y + (rowH - 5.5) / 2;
    ctx.setColor(cfg.bg, 'fill');
    doc.roundedRect(badgeX, badgeY, labelW, 5.5, 1.5, 1.5, 'F');
    ctx.setColor(cfg.text, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7);
    doc.text(cfg.label, badgeX + labelW / 2, badgeY + 3.8, { align: 'center' });

    ctx.setColor(TEXT, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9.5);
    doc.text(titleLines, contentX, y + 5);

    if (descLines.length > 0) {
      ctx.setColor([55, 65, 81], 'text');
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(9);
      doc.text(descLines, contentX, y + 5 + titleH + 3);
    }
    y += rowH + 1;
  }

  y = drawPDFSharedHeader(doc, data, BLUE, reg.label);
  drawTableHeader();
  data.entries.forEach((entry, i) => drawTableRow(entry, i % 2 === 1));
  drawPDFFooter(doc, ctx, data, BLUE, page);

  doc.save(`registro-${pdfFilename(data.doc.title)}.pdf`);
  showToast('PDF gerado com sucesso!', 'success');
}

// ── Utilities ─────────────────────────────

function formatDate(dateStr) {
  if (!dateStr) return '';
  const [y, m, d] = dateStr.split('-');
  return `${d}/${m}/${y}`;
}

function showToast(msg, type = '') {
  const toast = document.getElementById('toast');
  toast.textContent = msg;
  toast.className = `toast${type ? ' ' + type : ''} show`;
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => { toast.className = 'toast'; }, 3500);
}

// ── Word: helpers ─────────────────────────

function base64ToUint8Array(base64) {
  const b64 = base64.includes(',') ? base64.split(',')[1] : base64;
  const binary = atob(b64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
  return bytes;
}

function getImageDimensions(src) {
  return new Promise(resolve => {
    const img = new Image();
    img.onload  = () => resolve({ width: img.naturalWidth, height: img.naturalHeight });
    img.onerror = () => resolve({ width: 400, height: 300 });
    img.src = src;
  });
}

function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

// ── Word: generate ────────────────────────

async function generateWord() {
  if (!window.docx) {
    showToast('Biblioteca Word não carregada. Verifique a conexão.', 'error');
    return;
  }

  const data = collectData();

  if (!data.doc.title) {
    showToast('Informe o título do documento antes de gerar o Word.', 'error');
    document.getElementById('doc-title').focus();
    return;
  }

  if (currentDocType === 'changelog') {
    if (!data.entries || data.entries.length === 0) {
      showToast('Adicione pelo menos uma entrada de alteração.', 'error');
      return;
    }
  } else {
    if (!data.sections || data.sections.length === 0) {
      showToast('Adicione pelo menos uma seção com conteúdo.', 'error');
      return;
    }
  }

  showToast('Gerando Word…');

  try {
    await DOC_REGISTRY[currentDocType].buildWord(data);
  } catch (err) {
    console.error(err);
    showToast('Erro ao gerar o Word. Veja o console para detalhes.', 'error');
  }
}

// ── Word: footer helper ───────────────────

function makeWordFooter(D, accentHex) {
  const NONE = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };
  return new D.Table({
    width: { size: 100, type: D.WidthType.PERCENTAGE },
    borders: {
      top:     { style: D.BorderStyle.SINGLE, size: 4, color: 'E5E7EB' },
      bottom:  NONE, left: NONE, right: NONE, insideH: NONE, insideV: NONE,
    },
    rows: [new D.TableRow({
      children: [
        new D.TableCell({
          width: { size: 50, type: D.WidthType.PERCENTAGE },
          borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
          margins: { top: 120 },
          children: [
            new D.Paragraph({
              children: [new D.TextRun({ text: 'Matriz Betha Sistemas', bold: true, size: 16, color: accentHex })],
              spacing: { after: 40 },
            }),
            new D.Paragraph({
              children: [new D.TextRun({ text: 'Rua Júlio Gaidzinski, 320,', size: 14, color: '6B7280' })],
              spacing: { after: 20 },
            }),
            new D.Paragraph({
              children: [new D.TextRun({ text: '88811-000, Pio Corrêa / Criciúma - SC', size: 14, color: '6B7280' })],
            }),
          ],
        }),
        new D.TableCell({
          width: { size: 50, type: D.WidthType.PERCENTAGE },
          borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
          margins: { top: 120 },
          children: [
            new D.Paragraph({
              alignment: D.AlignmentType.RIGHT,
              children: [new D.TextRun({ text: '48 3431-0733', bold: true, size: 16, color: accentHex })],
              spacing: { after: 40 },
            }),
            new D.Paragraph({
              alignment: D.AlignmentType.RIGHT,
              children: [new D.TextRun({ text: 'Atendimento técnico', size: 14, color: '6B7280' })],
              spacing: { after: 20 },
            }),
            new D.Paragraph({
              alignment: D.AlignmentType.RIGHT,
              children: [new D.TextRun({ text: '0800 600 0735', bold: true, size: 16, color: accentHex })],
            }),
          ],
        }),
      ],
    })],
  });
}

// ── Word: header helper ───────────────────

function makeWordHeader(D, accentHex, showLogo) {
  if (!showLogo) return null;
  return new D.Paragraph({
    spacing: { after: 120 },
    children: [
      new D.TextRun({ text: 'BETHA  ', bold: true, italics: true, size: 38, color: '5F76B4' }),
      new D.TextRun({ text: 'Tudo que a sua cidade  •  pode se tornar', size: 14, color: '94A5D2' }),
    ],
  });
}

// ── Word: Guide + Technical ───────────────

async function buildSectionedDocWord(data) {
  const D = window.docx;
  const reg      = DOC_REGISTRY[data.doc.type] || DOC_REGISTRY.guide;
  const ACCENT   = reg.accentHex;
  const TYPE_LBL = reg.wordTypeLabel;
  const ALERTS   = reg.alertWordCfg;
  const NONE     = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };

  function makeAlertTable(alertType, alertText) {
    const cfg = ALERTS[alertType];
    if (!cfg) return [];
    return [
      new D.Table({
        width: { size: 100, type: D.WidthType.PERCENTAGE },
        borders: { top: NONE, bottom: NONE, left: NONE, right: NONE, insideH: NONE, insideV: NONE },
        rows: [new D.TableRow({
          children: [
            new D.TableCell({
              width: { size: 3, type: D.WidthType.PERCENTAGE },
              shading: { fill: cfg.border, type: D.ShadingType.CLEAR, color: 'auto' },
              borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
              children: [new D.Paragraph({ children: [] })],
            }),
            new D.TableCell({
              width: { size: 97, type: D.WidthType.PERCENTAGE },
              shading: { fill: cfg.fill, type: D.ShadingType.CLEAR, color: 'auto' },
              borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
              margins: { top: 60, bottom: 60, left: 120, right: 120 },
              children: [new D.Paragraph({
                children: [
                  new D.TextRun({ text: `${cfg.label}  `, bold: true, size: 17, color: cfg.border }),
                  new D.TextRun({ text: alertText, size: 17, color: '4B5563' }),
                ],
              })],
            }),
          ],
        })],
      }),
      new D.Paragraph({ spacing: { after: 80 }, children: [] }),
    ];
  }

  const children = [];

  // Header: wordmark BETHA + slogan
  const _hdr1 = makeWordHeader(D, ACCENT, data.settings.showLogo);
  if (_hdr1) { children.push(_hdr1); children.push(new D.Paragraph({ spacing: { after: 160 }, children: [] })); }

  // Tipo do documento
  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: TYPE_LBL, size: 16, color: '6B7280' })],
    spacing: { after: 80 },
  }));

  // Título
  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: data.doc.title || 'Documento', bold: true, size: 52, color: ACCENT })],
    spacing: { after: 240 },
  }));

  // Separador
  children.push(new D.Paragraph({
    border: { bottom: { style: D.BorderStyle.SINGLE, size: 8, color: ACCENT, space: 1 } },
    spacing: { after: 200 },
    children: [],
  }));

  // Metadados
  children.push(...makeWordMetaTable(D, ACCENT, collectWordMetaItems(data)));

  // Seções
  for (const [sIdx, section] of data.sections.entries()) {
    children.push(new D.Paragraph({
      border: { left: { style: D.BorderStyle.THICK, size: 14, color: ACCENT, space: 4 } },
      spacing: { before: 360, after: 160 },
      children: [
        new D.TextRun({
          text: `  ${sIdx + 1}. ${(section.title || 'Seção').toUpperCase()}`,
          bold: true, size: 26, color: ACCENT,
        }),
      ],
    }));

    for (const [stIdx, step] of section.steps.entries()) {
      if (!step.text && !step.image) continue;

      if (step.text) {
        children.push(new D.Paragraph({
          spacing: { before: 100, after: 80 },
          children: [
            new D.TextRun({ text: `${stIdx + 1}.  `, bold: true, size: 21, color: ACCENT }),
            new D.TextRun({ text: step.text, size: 21, color: '1A1D2E' }),
          ],
        }));
      }

      if (step.alertType && step.alertText) {
        children.push(...makeAlertTable(step.alertType, step.alertText));
      }

      if (step.image) {
        try {
          const imgData = base64ToUint8Array(step.image);
          const dims    = await getImageDimensions(step.image);
          const maxW    = 550;
          const scale   = Math.min(1, maxW / dims.width);
          const w = Math.round(dims.width * scale);
          const h = Math.round(dims.height * scale);
          children.push(new D.Paragraph({
            spacing: { before: 80, after: 80 },
            children: [new D.ImageRun({ data: imgData, transformation: { width: w, height: h } })],
          }));
        } catch (e) {
          console.warn('Imagem não incluída no Word:', e);
        }
      }
    }
    children.push(new D.Paragraph({ spacing: { after: 80 }, children: [] }));
  }

  // Rodapé
  if (data.settings.showFooter) {
    children.push(new D.Paragraph({ spacing: { before: 480 }, children: [] }));
    children.push(makeWordFooter(D, ACCENT));
  }

  const wordDoc = new D.Document({ sections: [{ properties: {}, children }] });
  const blob    = await D.Packer.toBlob(wordDoc);
  downloadBlob(blob, `${pdfFilename(data.doc.title)}.docx`);
  showToast('Word gerado com sucesso!', 'success');
}

// ── Word: Changelog ───────────────────────

async function buildChangelogDocWord(data) {
  const D    = window.docx;
  const BLUE = '586EAC';

  const TYPE_CFG = {
    addition:    { fill: 'D1FAE5', text: '065F46', label: 'Criação' },
    fix:         { fill: 'DBEAFE', text: '1E40AF', label: 'Correção' },
    change:      { fill: 'FEF3C7', text: '92400E', label: 'Alteração' },
    removal:     { fill: 'FEE2E2', text: '991B1B', label: 'Remoção' },
    improvement: { fill: 'EDE9FE', text: '5B21B6', label: 'Melhoria' },
    security:    { fill: 'F3F4F6', text: '374151', label: 'Segurança' },
  };

  const NONE = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };
  const BDR  = { style: D.BorderStyle.SINGLE, size: 4, color: 'E5E7EB' };

  const children = [];

  // Header: wordmark BETHA + slogan
  const _hdr2 = makeWordHeader(D, BLUE, data.settings.showLogo);
  if (_hdr2) { children.push(_hdr2); children.push(new D.Paragraph({ spacing: { after: 160 }, children: [] })); }

  // Tipo + título
  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: 'REGISTRO DE ALTERAÇÕES • BETHA SISTEMAS', size: 16, color: '6B7280' })],
    spacing: { after: 80 },
  }));

  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: data.doc.title || 'Documento', bold: true, size: 52, color: '1A1D2E' })],
    spacing: { after: 240 },
  }));

  children.push(new D.Paragraph({
    border: { bottom: { style: D.BorderStyle.SINGLE, size: 8, color: BLUE, space: 1 } },
    spacing: { after: 200 },
    children: [],
  }));

  // Metadados
  children.push(...makeWordMetaTable(D, BLUE, collectWordMetaItems(data)));

  // Tabela
  const tableRows = [];

  tableRows.push(new D.TableRow({
    tableHeader: true,
    children: [
      new D.TableCell({
        width: { size: 25, type: D.WidthType.PERCENTAGE },
        shading: { fill: BLUE, type: D.ShadingType.CLEAR, color: 'auto' },
        borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new D.Paragraph({
          alignment: D.AlignmentType.CENTER,
          children: [new D.TextRun({ text: 'TIPO', bold: true, size: 18, color: 'FFFFFF' })],
        })],
      }),
      new D.TableCell({
        width: { size: 75, type: D.WidthType.PERCENTAGE },
        shading: { fill: BLUE, type: D.ShadingType.CLEAR, color: 'auto' },
        borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new D.Paragraph({
          children: [new D.TextRun({ text: 'DESCRIÇÃO', bold: true, size: 18, color: 'FFFFFF' })],
        })],
      }),
    ],
  }));

  data.entries.forEach((entry, i) => {
    const cfg     = TYPE_CFG[entry.type] || TYPE_CFG.change;
    const altFill = i % 2 === 1 ? 'F8FAFC' : 'FFFFFF';

    tableRows.push(new D.TableRow({
      children: [
        new D.TableCell({
          width: { size: 25, type: D.WidthType.PERCENTAGE },
          shading: { fill: altFill, type: D.ShadingType.CLEAR, color: 'auto' },
          borders: { top: BDR, bottom: BDR, left: BDR, right: BDR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [
            new D.Paragraph({
              alignment: D.AlignmentType.CENTER,
              children: [new D.TextRun({ text: `● ${cfg.label}`, bold: true, size: 17, color: cfg.text })],
            }),
          ],
        }),
        new D.TableCell({
          width: { size: 75, type: D.WidthType.PERCENTAGE },
          shading: { fill: altFill, type: D.ShadingType.CLEAR, color: 'auto' },
          borders: { top: BDR, bottom: BDR, left: BDR, right: BDR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [
            new D.Paragraph({
              children: [new D.TextRun({ text: entry.title || '—', bold: true, size: 20, color: '1A1D2E' })],
              spacing: { after: 80 },
            }),
            ...(entry.desc ? [new D.Paragraph({
              children: [new D.TextRun({ text: entry.desc, size: 18, color: '374151' })],
            })] : []),
          ],
        }),
      ],
    }));
  });

  children.push(new D.Table({
    width: { size: 100, type: D.WidthType.PERCENTAGE },
    borders: { top: BDR, bottom: BDR, left: BDR, right: BDR, insideH: BDR, insideV: BDR },
    rows: tableRows,
  }));

  // Rodapé
  if (data.settings.showFooter) {
    children.push(new D.Paragraph({ spacing: { before: 480 }, children: [] }));
    children.push(makeWordFooter(D, BLUE));
  }

  const wordDoc = new D.Document({ sections: [{ properties: {}, children }] });
  const blob    = await D.Packer.toBlob(wordDoc);
  downloadBlob(blob, `registro-${pdfFilename(data.doc.title)}.docx`);
  showToast('Word gerado com sucesso!', 'success');
}
