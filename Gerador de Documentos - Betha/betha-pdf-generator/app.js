/* ──────────────────────────────────────────
   Betha Doc Generator – app.js
   ────────────────────────────────────────── */

let sectionCounter = 0;
let stepCounter = 0;
let entryCounter = 0;
let currentDocType = 'guide';
let logoBase64 = null;

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
      <div class="entry-field-group">
        <label>Data</label>
        <div class="date-input-wrap">
          <input type="date" class="entry-date">
          <svg class="date-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
            <line x1="16" y1="2" x2="16" y2="6"/>
            <line x1="8" y1="2" x2="8" y2="6"/>
            <line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
        </div>
      </div>
      <div class="entry-field-group entry-type-group">
        <label>Tipo</label>
        <select class="entry-type">
          <option value="addition">✅ Adição</option>
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
        date:  el.querySelector('.entry-date').value,
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
    if (currentDocType === 'changelog') {
      await buildChangelogDocPDF(data);
    } else {
      await buildSectionedDocPDF(data);
    }
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

// ── PDF: Guide + Technical ────────────────

async function buildSectionedDocPDF(data) {
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ orientation: 'p', unit: 'mm', format: 'a4' });

  const PW = 210, PH = 297, ML = 15, MR = 15, MB = 18, CW = PW - ML - MR;

  const isTech = data.doc.type === 'technical';

  const PALETTE = isTech ? {
    HEADER_BG: [30, 41, 59],
    ACCENT:    [14, 165, 133],
    HEADER_LABEL: 'DOCUMENTAÇÃO TÉCNICA',
    ALERTS: {
      info:    { fill: [243, 253, 251], border: [13, 148, 136], label: 'Nota' },
      warning: { fill: [255, 253, 242], border: [217, 119, 6],  label: 'Aviso' },
      danger:  { fill: [255, 249, 240], border: [234, 88, 12],  label: 'Importante' },
    },
  } : {
    HEADER_BG: [61, 90, 241],
    ACCENT:    [61, 90, 241],
    HEADER_LABEL: null,
    ALERTS: {
      info:    { fill: [243, 248, 255], border: [37, 99, 235],  label: 'Dica' },
      warning: { fill: [255, 253, 242], border: [217, 119, 6],  label: 'Atencao'  },
      danger:  { fill: [255, 245, 245], border: [220, 38, 38],  label: 'Critico'  },
    },
  };

  const TEXT  = [26, 29, 46];
  const MUTED = [107, 114, 128];
  const WHITE = [255, 255, 255];
  const BORDER = [229, 231, 235];

  let page = 1, y = 0;

  function setColor(rgb, type = 'fill') {
    if (type === 'fill') doc.setFillColor(...rgb);
    if (type === 'text') doc.setTextColor(...rgb);
    if (type === 'draw') doc.setDrawColor(...rgb);
  }

  function safeText(str) {
    return (str || '').replace(/['']/g, "'").replace(/[""]/g, '"');
  }

  function wrappedLines(text, maxW, size) {
    doc.setFontSize(size);
    return doc.splitTextToSize(safeText(text), maxW);
  }

  function lineHeight(size) { return size * 0.3528 * 1.4; }

  function ensureSpace(needed) {
    if (y + needed > PH - MB) {
      drawFooter();
      doc.addPage();
      page++;
      drawContinuationHeader();
    }
  }

  function drawMainHeader() {
    setColor(PALETTE.HEADER_BG, 'fill');
    doc.rect(0, 0, PW, 52, 'F');

    if (data.settings.showLogo && logoBase64) {
      doc.addImage(logoBase64, 'PNG', ML, 14, 14, 14);
    }

    const xText = data.settings.showLogo && logoBase64 ? ML + 20 : ML;

    if (PALETTE.HEADER_LABEL) {
      setColor([180, 220, 220], 'text');
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(7);
      doc.text(PALETTE.HEADER_LABEL, xText, 13);
    }

    setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    const titleY = PALETTE.HEADER_LABEL ? 21 : 18;
    const titleLines = wrappedLines(data.doc.title || 'Documento', CW - 22, 16);
    doc.text(titleLines, xText, titleY);

    const entityPart = data.doc.entity ? `Entidade: ${data.doc.entity}` : null;
    const row2Parts  = [
      data.doc.module  && `Módulo: ${data.doc.module}`,
      data.doc.author  && `Responsável: ${data.doc.author}`,
      data.doc.date    && `Data: ${formatDate(data.doc.date)}`,
      data.doc.ticket  && `Chamado: ${data.doc.ticket}`,
    ].filter(Boolean);

    if (entityPart || row2Parts.length) {
      let metaY = Math.max(titleY + titleLines.length * lineHeight(16) + 4, 36);
      if (entityPart) {
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(8.5);
        setColor([230, 235, 255], 'text');
        doc.text(entityPart, xText, metaY);
        metaY += 6;
      }
      if (row2Parts.length) {
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(7.5);
        setColor([190, 205, 250], 'text');
        doc.text(row2Parts.join('   |   '), xText, metaY, { maxWidth: CW - 22 });
      }
    }

    y = 60;
  }

  function drawContinuationHeader() {
    setColor(PALETTE.HEADER_BG, 'fill');
    doc.rect(0, 0, PW, 11, 'F');

    if (data.settings.showLogo && logoBase64) {
      doc.addImage(logoBase64, 'PNG', ML, 1.5, 8, 8);
    }

    setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    const xT = data.settings.showLogo && logoBase64 ? ML + 11 : ML;
    doc.text(safeText(data.doc.title || ''), xT, 7.5, { maxWidth: CW - 12 });

    y = 18;
  }

  function drawFooter() {
    if (!data.settings.showFooter && !data.settings.showPageNumbers) return;

    const fy = PH - MB + 5;
    setColor(BORDER, 'draw');
    doc.setLineWidth(0.3);
    doc.line(ML, fy, PW - MR, fy);
    doc.setFontSize(7.5);
    doc.setFont('helvetica', 'normal');

    if (data.settings.showFooter) {
      setColor(MUTED, 'text');
      doc.text('Betha Sistemas', ML, fy + 5);
    }

    if (data.settings.showPageNumbers) {
      setColor(MUTED, 'text');
      doc.text(`Página ${page}`, PW - MR, fy + 5, { align: 'right' });
    }
  }

  function drawSection(section, index) {
    ensureSpace(16);

    setColor(PALETTE.ACCENT, 'fill');
    doc.rect(ML, y, 3, 7, 'F');

    setColor(PALETTE.ACCENT, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(11);
    const sTitle = `${index + 1}. ${(section.title || 'Seção').toUpperCase()}`;
    doc.text(safeText(sTitle), ML + 5, y + 5.5);

    y += 10;

    setColor([200, 220, 220], 'draw');
    doc.setLineWidth(0.4);
    doc.line(ML, y, PW - MR, y);

    y += 6;

    section.steps.forEach((step, si) => {
      if (step.text || step.image) drawStep(step, si + 1);
    });

    y += 4;
  }

  function drawStep(step, num) {
    const lines = wrappedLines(step.text || '', CW - 14, 10);
    const textH = lines.length * lineHeight(10) + 2;
    ensureSpace(textH + 8);

    setColor(PALETTE.ACCENT, 'fill');
    doc.circle(ML + 3.5, y + 3.5, 3.5, 'F');
    setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    doc.text(String(num), ML + 3.5, y + 4.3, { align: 'center' });

    setColor(TEXT, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    doc.text(lines, ML + 10, y + 4);

    y += textH + 2;

    if (step.alertType && step.alertText) {
      drawAlert(step.alertType, step.alertText);
    }

    if (step.image) {
      const maxImgW = CW - 10;
      const maxImgH = 70;
      const imgEl = new Image();
      imgEl.src = step.image;
      const ratio = imgEl.naturalWidth ? imgEl.naturalHeight / imgEl.naturalWidth : 0.6;
      const imgH = Math.min(maxImgH, maxImgW * ratio);

      ensureSpace(imgH + 6);

      setColor(BORDER, 'draw');
      doc.setLineWidth(0.3);
      doc.rect(ML + 8, y, maxImgW, imgH);
      doc.addImage(step.image, 'PNG', ML + 8, y, maxImgW, imgH, undefined, 'FAST');
      y += imgH + 6;
    }

    y += 3;
  }

  function drawAlert(type, text) {
    const cfg = PALETTE.ALERTS[type];
    if (!cfg) return;

    const lines = wrappedLines(text, CW - 26, 9);
    const textH = lines.length * lineHeight(9);
    const h     = textH + 14;
    ensureSpace(h + 4);

    // Fundo sutil
    setColor(cfg.fill, 'fill');
    doc.roundedRect(ML + 8, y, CW - 8, h, 2.5, 2.5, 'F');

    // Borda esquerda fina
    setColor(cfg.border, 'fill');
    doc.roundedRect(ML + 8, y, 2, h, 1.5, 1.5, 'F');

    // Badge pill com o tipo
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(6.5);
    const pillW = doc.getTextWidth(cfg.label) + 8;
    setColor(cfg.border, 'fill');
    doc.roundedRect(ML + 13.5, y + 3.5, pillW, 5.2, 2, 2, 'F');
    setColor(WHITE, 'text');
    doc.text(cfg.label, ML + 13.5 + pillW / 2, y + 7.4, { align: 'center' });

    // Texto do alerta
    setColor([75, 85, 99], 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.text(lines, ML + 13.5, y + 12);

    y += h + 3;
  }

  drawMainHeader();
  data.sections.forEach((section, i) => drawSection(section, i));
  drawFooter();

  doc.save(`${pdfFilename(data.doc.title)}.pdf`);
  showToast('PDF gerado com sucesso!', 'success');
}

// ── PDF: Changelog ────────────────────────

async function buildChangelogDocPDF(data) {
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ orientation: 'p', unit: 'mm', format: 'a4' });

  const PW = 210, PH = 297, ML = 15, MR = 15, MB = 18, CW = PW - ML - MR;
  const BLUE   = [61, 90, 241];
  const WHITE  = [255, 255, 255];
  const TEXT   = [26, 29, 46];
  const MUTED  = [107, 114, 128];
  const BORDER = [229, 231, 235];
  const BG_ALT = [248, 250, 252];

  // Data+Tipo(32) | Conteúdo(148) = 180mm
  const COL = {
    date:    { x: ML,      w: 32 },
    content: { x: ML + 32, w: CW - 32 },
  };

  let page = 1, y = 0;

  function setColor(rgb, type = 'fill') {
    if (type === 'fill') doc.setFillColor(...rgb);
    if (type === 'text') doc.setTextColor(...rgb);
    if (type === 'draw') doc.setDrawColor(...rgb);
  }

  function safeText(str) {
    return (str || '').replace(/['']/g, "'").replace(/[""]/g, '"');
  }

  function wrappedLines(text, maxW, size) {
    doc.setFontSize(size);
    return doc.splitTextToSize(safeText(text), maxW);
  }

  function lineHeight(size) { return size * 0.3528 * 1.4; }

  function ensureSpace(needed) {
    if (y + needed > PH - MB) {
      drawFooter();
      doc.addPage();
      page++;
      drawContinuationHeader();
    }
  }

  function drawMainHeader() {
    setColor(BLUE, 'fill');
    doc.rect(0, 0, PW, 52, 'F');

    if (data.settings.showLogo && logoBase64) {
      doc.addImage(logoBase64, 'PNG', ML, 14, 14, 14);
    }

    const xText = data.settings.showLogo && logoBase64 ? ML + 20 : ML;

    setColor([200, 210, 255], 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7.5);
    doc.text('REGISTRO DE ALTERAÇÕES', xText, 13);

    setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(15);
    const titleLines = wrappedLines(data.doc.title || 'Documento', CW - 22, 15);
    doc.text(titleLines, xText, 21);

    const entityPart = data.doc.entity ? `Entidade: ${data.doc.entity}` : null;
    const row2Parts  = [
      data.doc.module  && `Módulo: ${data.doc.module}`,
      data.doc.author  && `Responsável: ${data.doc.author}`,
      data.doc.date    && `Data: ${formatDate(data.doc.date)}`,
      data.doc.ticket  && `Chamado: ${data.doc.ticket}`,
    ].filter(Boolean);

    if (entityPart || row2Parts.length) {
      let metaY = Math.max(21 + titleLines.length * lineHeight(15) + 4, 36);
      if (entityPart) {
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(8.5);
        setColor([230, 235, 255], 'text');
        doc.text(entityPart, xText, metaY);
        metaY += 6;
      }
      if (row2Parts.length) {
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(7.5);
        setColor([190, 205, 250], 'text');
        doc.text(row2Parts.join('   |   '), xText, metaY, { maxWidth: CW - 22 });
      }
    }

    y = 60;
  }

  function drawContinuationHeader() {
    setColor(BLUE, 'fill');
    doc.rect(0, 0, PW, 11, 'F');

    if (data.settings.showLogo && logoBase64) {
      doc.addImage(logoBase64, 'PNG', ML, 1.5, 8, 8);
    }

    setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    const xT = data.settings.showLogo && logoBase64 ? ML + 11 : ML;
    doc.text(safeText(data.doc.title || ''), xT, 7.5, { maxWidth: CW - 12 });

    y = 18;
  }

  function drawFooter() {
    if (!data.settings.showFooter && !data.settings.showPageNumbers) return;
    const fy = PH - MB + 5;
    setColor(BORDER, 'draw');
    doc.setLineWidth(0.3);
    doc.line(ML, fy, PW - MR, fy);
    doc.setFontSize(7.5);
    doc.setFont('helvetica', 'normal');
    if (data.settings.showFooter) {
      setColor(MUTED, 'text');
      doc.text('Betha Sistemas', ML, fy + 5);
    }
    if (data.settings.showPageNumbers) {
      setColor(MUTED, 'text');
      doc.text(`Página ${page}`, PW - MR, fy + 5, { align: 'right' });
    }
  }

  function drawTableHeader() {
    ensureSpace(11);
    setColor(BLUE, 'fill');
    doc.rect(ML, y, CW, 11, 'F');

    setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');

    // "TIPO DE OPERAÇÃO" centralizado na coluna esquerda
    const dateColCx = COL.date.x + COL.date.w / 2;
    doc.setFontSize(7.5);
    doc.text('TIPO DE',  dateColCx, y + 4,   { align: 'center' });
    doc.text('OPERAÇÃO', dateColCx, y + 7.8,  { align: 'center' });

    // "DESCRIÇÃO" alinhado ao início da coluna de conteúdo
    doc.setFontSize(8);
    doc.text('DESCRIÇÃO', COL.content.x + 3, y + 6.5);

    y += 11;

    setColor(BORDER, 'draw');
    doc.setLineWidth(0.2);
    doc.line(ML, y, ML + CW, y);
  }

  const TYPE_CFG = {
    addition:    { bg: [209, 250, 229], text: [6, 95, 70],   label: 'Adição' },
    fix:         { bg: [219, 234, 254], text: [30, 64, 175], label: 'Correção' },
    change:      { bg: [254, 243, 199], text: [146, 64, 14], label: 'Alteração' },
    removal:     { bg: [254, 226, 226], text: [153, 27, 27], label: 'Remoção' },
    improvement: { bg: [237, 233, 254], text: [91, 33, 182], label: 'Melhoria' },
    security:    { bg: [243, 244, 246], text: [55, 65, 81],  label: 'Segurança' },
  };

  function drawTableRow(entry, isAlt) {
    const contentX = COL.content.x + 3;
    const contentW = COL.content.w - 6;

    // Calcular linhas do título e descrição
    const titleLines = wrappedLines(entry.title || '—', contentW, 9.5);
    const titleH     = titleLines.length * lineHeight(9.5);

    const descText  = (entry.desc || '').trim();
    const descLines = descText ? wrappedLines(descText, contentW, 9) : [];
    const descH     = descLines.length * lineHeight(9);

    const contentH = titleH + (descH > 0 ? 3 + descH : 0);
    const dateColH = lineHeight(8.5) + 3 + 6;  // data + gap + badge
    const rowH     = Math.max(dateColH + 6, contentH + 8);

    ensureSpace(rowH + 1);

    // Fundo alternado
    if (isAlt) {
      setColor(BG_ALT, 'fill');
      doc.rect(ML, y, CW, rowH, 'F');
    }

    // Linha separadora inferior
    setColor(BORDER, 'draw');
    doc.setLineWidth(0.2);
    doc.line(ML, y + rowH, ML + CW, y + rowH);

    const startY = y + 5;

    // ── Coluna TIPO DE OPERAÇÃO ───────────────
    const dateColCx = COL.date.x + COL.date.w / 2;

    // Data centralizada
    setColor(MUTED, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(8.5);
    doc.text(entry.date ? formatDate(entry.date) : '—', dateColCx, startY, { align: 'center' });

    // Badge centralizado logo abaixo da data
    const cfg    = TYPE_CFG[entry.type] || TYPE_CFG['change'];
    const badgeY = startY + lineHeight(8.5) + 1.5;
    const labelW = cfg.label.length * 1.85 + 5;
    const badgeX = COL.date.x + (COL.date.w - labelW) / 2;
    setColor(cfg.bg, 'fill');
    doc.roundedRect(badgeX, badgeY, labelW, 5.5, 1.5, 1.5, 'F');
    setColor(cfg.text, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7);
    doc.text(cfg.label, badgeX + labelW / 2, badgeY + 3.8, { align: 'center' });

    // ── Coluna CONTEÚDO ───────────────────────
    // Título em negrito
    setColor(TEXT, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9.5);
    doc.text(titleLines, contentX, startY);

    // Descrição como lista logo abaixo do título
    if (descLines.length > 0) {
      setColor([55, 65, 81], 'text');
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(9);
      doc.text(descLines, contentX, startY + titleH + 3);
    }

    y += rowH + 1;
  }

  drawMainHeader();
  drawTableHeader();
  data.entries.forEach((entry, i) => drawTableRow(entry, i % 2 === 1));
  drawFooter();

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
