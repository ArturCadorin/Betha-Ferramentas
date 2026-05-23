/* ──────────────────────────────────────────
   Betha Doc Generator – app.js
   ────────────────────────────────────────── */

let sectionCounter = 0;
let logoBase64 = null;

// ── Init ──────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('doc-date').valueAsDate = new Date();
  loadLogo();
  addSection(); // start with one section

  document.getElementById('btn-add-section').addEventListener('click', addSection);
  document.getElementById('btn-generate-nav').addEventListener('click', generate);
  document.getElementById('btn-generate-main').addEventListener('click', generate);

  // Event delegation for all dynamic elements inside #sections-container
  document.getElementById('sections-container').addEventListener('click', handleContainerClick);
  document.getElementById('sections-container').addEventListener('change', handleContainerChange);
});

function loadLogo() {
  const img = new Image();
  img.crossOrigin = 'anonymous';
  img.onload = () => {
    const canvas = document.createElement('canvas');
    canvas.width  = img.naturalWidth;
    canvas.height = img.naturalHeight;
    canvas.getContext('2d').drawImage(img, 0, 0);
    logoBase64 = canvas.toDataURL('image/png');
  };
  img.src = '../Logo Betha.png';
}

// ── Event delegation ──────────────────────

function handleContainerClick(e) {
  const t = e.target;

  if (t.closest('.btn-remove-section')) {
    removeSection(t.closest('.section-block'));
    return;
  }
  if (t.closest('.btn-add-step')) {
    addStep(t.closest('.section-block').dataset.sid);
    return;
  }
  if (t.closest('.btn-remove-step')) {
    removeStep(t.closest('.step-block'));
    return;
  }
  if (t.closest('.btn-add-image')) {
    t.closest('.step-block').querySelector('.img-input').click();
    return;
  }
  if (t.closest('.btn-remove-img')) {
    clearImage(t.closest('.step-block'));
    return;
  }
}

function handleContainerChange(e) {
  const t = e.target;

  if (t.classList.contains('step-alert-select')) {
    const stepEl = t.closest('.step-block');
    const alertRow = stepEl.querySelector('.step-alert-row');
    alertRow.style.display = t.value ? 'flex' : 'none';
    return;
  }
  if (t.classList.contains('img-input') && t.files[0]) {
    handleImageUpload(t, t.closest('.step-block'));
    return;
  }
}

// ── Sections ──────────────────────────────

function addSection() {
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
        <button class="btn-icon danger btn-remove-section" title="Remover seção">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
    </div>
    <div class="steps-wrap" id="steps-${sid}"></div>
    <div class="add-step-row">
      <button class="btn btn-ghost btn-sm btn-add-step">+ Adicionar Passo</button>
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

let stepCounter = 0;

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
        <select class="step-alert-select">
          <option value="">Sem alerta</option>
          <option value="info">💡 Dica</option>
          <option value="warning">⚠️ Atenção</option>
          <option value="danger">🚨 Crítico</option>
        </select>
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

// ── Data collection ───────────────────────

function collectData() {
  const sections = [];

  document.querySelectorAll('.section-block').forEach(secEl => {
    const steps = [];
    secEl.querySelectorAll('.step-block').forEach(stepEl => {
      steps.push({
        text:       stepEl.querySelector('.step-text').value.trim(),
        alertType:  stepEl.querySelector('.step-alert-select').value,
        alertText:  stepEl.querySelector('.step-alert-input').value.trim(),
        image:      stepEl.dataset.img || null,
      });
    });
    sections.push({
      title: secEl.querySelector('.section-title-input').value.trim(),
      steps,
    });
  });

  return {
    doc: {
      title:   document.getElementById('doc-title').value.trim(),
      module:  document.getElementById('doc-module').value.trim(),
      version: document.getElementById('doc-version').value.trim(),
      author:  document.getElementById('doc-author').value.trim(),
      date:    document.getElementById('doc-date').value,
    },
    sections,
    settings: {
      showLogo:        document.getElementById('show-logo').checked,
      showFooter:      document.getElementById('show-footer').checked,
      showPageNumbers: document.getElementById('show-page-numbers').checked,
    },
  };
}

// ── PDF Generation ────────────────────────

async function generate() {
  const data = collectData();

  if (!data.doc.title) {
    showToast('Informe o título do documento antes de gerar o PDF.', 'error');
    document.getElementById('doc-title').focus();
    return;
  }
  if (data.sections.length === 0) {
    showToast('Adicione pelo menos uma seção com conteúdo.', 'error');
    return;
  }

  showToast('Gerando PDF…');

  try {
    await buildPDF(data);
  } catch (err) {
    console.error(err);
    showToast('Erro ao gerar o PDF. Veja o console para detalhes.', 'error');
  }
}

async function buildPDF(data) {
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ orientation: 'p', unit: 'mm', format: 'a4' });

  // ── Layout constants ──
  const PW  = 210;   // page width
  const PH  = 297;   // page height
  const ML  = 15;    // margin left
  const MR  = 15;    // margin right
  const MB  = 18;    // margin bottom
  const CW  = PW - ML - MR; // content width = 180

  // ── Color palette ──
  const BLUE      = [61, 90, 241];
  const BLUE_DARK = [30, 50, 170];
  const TEXT      = [26, 29, 46];
  const MUTED     = [107, 114, 128];
  const WHITE     = [255, 255, 255];
  const BORDER    = [229, 231, 235];

  let page = 1;
  let y    = 0;

  // ── Helpers ──────────────────────────────

  function setColor(rgb, type = 'fill') {
    if (type === 'fill')   doc.setFillColor(...rgb);
    if (type === 'text')   doc.setTextColor(...rgb);
    if (type === 'draw')   doc.setDrawColor(...rgb);
  }

  function safeText(str) {
    return (str || '').replace(/[‘’]/g, "'").replace(/[“”]/g, '"');
  }

  function wrappedLines(text, maxW, size) {
    doc.setFontSize(size);
    return doc.splitTextToSize(safeText(text), maxW);
  }

  function lineHeight(size) { return size * 0.3528 * 1.4; } // pt to mm × leading

  function ensureSpace(needed) {
    if (y + needed > PH - MB) {
      drawFooter();
      doc.addPage();
      page++;
      drawContinuationHeader();
    }
  }

  // ── Header (first page) ──────────────────

  function drawMainHeader() {
    // Blue background bar
    setColor(BLUE, 'fill');
    doc.rect(0, 0, PW, 42, 'F');

    // Logo
    if (data.settings.showLogo && logoBase64) {
      doc.addImage(logoBase64, 'PNG', ML, 8, 16, 16);
    }

    const xText = data.settings.showLogo && logoBase64 ? ML + 20 : ML;

    // Title
    setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    const titleLines = wrappedLines(data.doc.title || 'Documento', CW - 22, 16);
    doc.text(titleLines, xText, 18);

    // Meta row
    const metaParts = [
      data.doc.module  && `Módulo: ${data.doc.module}`,
      data.doc.author  && `Responsável: ${data.doc.author}`,
      data.doc.date    && `Data: ${formatDate(data.doc.date)}`,
      data.doc.version && `Versão: ${data.doc.version}`,
    ].filter(Boolean);

    if (metaParts.length) {
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(8);
      setColor([200, 210, 255], 'text');
      doc.text(metaParts.join('   |   '), xText, 36, { maxWidth: CW - 22 });
    }

    y = 52;
  }

  // ── Header (continuation pages) ─────────

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

  // ── Footer ───────────────────────────────

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

  // ── Section ──────────────────────────────

  function drawSection(section, index) {
    ensureSpace(16);

    // Blue vertical accent bar
    setColor(BLUE, 'fill');
    doc.rect(ML, y, 3, 7, 'F');

    // Section label
    setColor(BLUE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(11);
    const sTitle = `${index + 1}. ${(section.title || 'Seção').toUpperCase()}`;
    doc.text(safeText(sTitle), ML + 5, y + 5.5);

    y += 10;

    // Underline
    setColor([200, 209, 250], 'draw');
    doc.setLineWidth(0.4);
    doc.line(ML, y, PW - MR, y);

    y += 6;

    section.steps.forEach((step, si) => {
      if (step.text || step.image) drawStep(step, si + 1);
    });

    y += 4;
  }

  // ── Step ─────────────────────────────────

  function drawStep(step, num) {
    const lines = wrappedLines(step.text || '', CW - 14, 10);
    const textH = lines.length * lineHeight(10) + 2;
    ensureSpace(textH + 8);

    // Numbered bubble
    setColor(BLUE, 'fill');
    doc.circle(ML + 3.5, y + 3.5, 3.5, 'F');
    setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    doc.text(String(num), ML + 3.5, y + 4.3, { align: 'center' });

    // Step text
    setColor(TEXT, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    doc.text(lines, ML + 10, y + 4);

    y += textH + 2;

    // Alert block
    if (step.alertType && step.alertText) {
      drawAlert(step.alertType, step.alertText);
    }

    // Image
    if (step.image) {
      const imgData = step.image;
      const maxImgW = CW - 10;
      const maxImgH = 70;

      // Try to get image dimensions to maintain aspect ratio
      const imgEl = new Image();
      imgEl.src = imgData;
      const ratio = imgEl.naturalWidth ? imgEl.naturalHeight / imgEl.naturalWidth : 0.6;
      const imgH = Math.min(maxImgH, maxImgW * ratio);

      ensureSpace(imgH + 6);

      // Border around image
      setColor(BORDER, 'draw');
      doc.setLineWidth(0.3);
      doc.rect(ML + 8, y, maxImgW, imgH);

      doc.addImage(imgData, 'PNG', ML + 8, y, maxImgW, imgH, undefined, 'FAST');
      y += imgH + 6;
    }

    y += 3;
  }

  // ── Alert block ──────────────────────────

  function drawAlert(type, text) {
    const cfg = {
      info:    { fill: [239, 246, 255], border: [37, 99, 235],  label: '💡 Dica' },
      warning: { fill: [255, 251, 235], border: [217, 119, 6],  label: '⚠ Atenção' },
      danger:  { fill: [254, 242, 242], border: [220, 38, 38],  label: '🚨 Crítico' },
    }[type];

    if (!cfg) return;

    const lines = wrappedLines(text, CW - 22, 9);
    const h = lines.length * lineHeight(9) + 10;
    ensureSpace(h + 4);

    // Background
    setColor(cfg.fill, 'fill');
    doc.roundedRect(ML + 8, y, CW - 8, h, 2, 2, 'F');

    // Accent border
    setColor(cfg.border, 'fill');
    doc.roundedRect(ML + 8, y, 2.5, h, 1, 1, 'F');

    // Label
    setColor(cfg.border, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8.5);
    doc.text(cfg.label, ML + 14, y + 6);

    // Text
    setColor([55, 65, 81], 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.text(lines, ML + 14, y + 11);

    y += h + 4;
  }

  // ── Build document ───────────────────────

  drawMainHeader();
  data.sections.forEach((section, i) => drawSection(section, i));
  drawFooter();

  // ── Save ─────────────────────────────────

  const filename = (data.doc.title || 'betha-documento')
    .toLowerCase()
    .normalize('NFD').replace(/[̀-ͯ]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '');

  doc.save(`${filename}.pdf`);
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
  toast._timer = setTimeout(() => {
    toast.className = 'toast';
  }, 3500);
}
