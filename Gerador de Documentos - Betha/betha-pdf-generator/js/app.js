/* ──────────────────────────────────────────
   Betha Doc Generator – app.js
   ────────────────────────────────────────── */

let sectionCounter = 0;
let stepCounter = 0;
let entryCounter = 0;
let opSectionCounter = 0;
let currentDocType = 'guide';
let logoBase64 = null;

// ── Registro de tipos de documento ────────
// Para adicionar um novo tipo: crie uma entrada aqui + uma função builder.
// Nenhum outro código precisa ser alterado.
// NOTA: as funções buildSectionedDocPDF, buildChangelogDocPDF, buildOperationalDocPDF,
//       buildSectionedDocWord, buildChangelogDocWord, buildOperationalDocWord
//       são definidas nos arquivos pdf-guide.js, pdf-changelog.js, pdf-operational.js
//       e word-builders.js, carregados antes deste arquivo no HTML.
const DOC_REGISTRY = {
  guide: {
    label:        'GUIA PASSO A PASSO',
    accentRGB:    BRAND.GUIDE_ACC,
    bgRGB:        BRAND.GUIDE_BG,
    accentHex:    '586EAC',
    wordTypeLabel:'GUIA PASSO A PASSO • BETHA SISTEMAS',
    alertCfg: {
      info:    { fill: [249, 251, 255], border: [70, 110, 210],  label: 'Dica' },
      warning: { fill: [255, 254, 248], border: [180, 100, 10],  label: 'Atenção' },
      danger:  { fill: [255, 250, 250], border: [185, 35, 35],   label: 'Crítico' },
    },
    alertWordCfg: {
      info:    { fill: 'EFF6FF', border: '2563EB', label: 'DICA' },
      warning: { fill: 'FFFBEB', border: 'D97706', label: 'ATENÇÃO' },
      danger:  { fill: 'FFF5F5', border: 'DC2626', label: 'CRÍTICO' },
    },
    buildPDF:  (data) => buildSectionedDocPDF(data),
    buildWord: (data) => buildSectionedDocWord(data),
  },
  operational: {
    label:     'RELATÓRIO OPERACIONAL',
    accentRGB: BRAND.GUIDE_ACC,
    bgRGB:     BRAND.GUIDE_BG,
    accentHex: '586EAC',
    buildPDF:  (data) => buildOperationalDocPDF(data),
    buildWord: (data) => buildOperationalDocWord(data),
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

// ── Dark mode ─────────────────────────────

function initDarkMode() {
  const btn = document.getElementById('btn-darkmode');
  const isDark = localStorage.getItem('betha-theme') === 'dark';
  document.body.classList.toggle('dark', isDark);
  if (btn) btn.addEventListener('click', toggleDarkMode);
}

function toggleDarkMode() {
  const isDark = document.body.classList.toggle('dark');
  localStorage.setItem('betha-theme', isDark ? 'dark' : 'light');
}

// ── Init ──────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('doc-date').valueAsDate = new Date();
  loadLogo();
  addSection();
  initDarkMode();
  initRichText();
  initSuggestions();

  document.getElementById('btn-add-section').addEventListener('click', addSection);
  document.getElementById('btn-add-entry').addEventListener('click', addEntry);
  document.getElementById('btn-add-op-section').addEventListener('click', addOpSection);
  document.getElementById('btn-generate-nav').addEventListener('click', generate);
  document.getElementById('btn-generate-main').addEventListener('click', generate);

  document.getElementById('sections-container').addEventListener('click', handleContainerClick);
  document.getElementById('sections-container').addEventListener('change', handleContainerChange);
  document.getElementById('entries-container').addEventListener('click', handleEntriesClick);
  document.getElementById('entries-container').addEventListener('input', handleEntriesInput);
  document.getElementById('op-sections-container').addEventListener('click', handleOpContainerClick);

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

  const sectionsCard   = document.getElementById('sections-card');
  const changelogCard  = document.getElementById('changelog-card');
  const opSectionsCard = document.getElementById('operational-sections-card');
  const opExtrasCard   = document.getElementById('operational-extras-card');

  sectionsCard.style.display   = type === 'guide'       ? '' : 'none';
  changelogCard.style.display  = type === 'changelog'   ? '' : 'none';
  opSectionsCard.style.display = type === 'operational' ? '' : 'none';
  opExtrasCard.style.display   = type === 'operational' ? '' : 'none';

  if (type === 'guide'       && !document.querySelector('.section-block'))      addSection();
  if (type === 'changelog'   && !document.querySelector('.changelog-entry'))    addEntry();
  if (type === 'operational' && !document.querySelector('.op-section-block'))   addOpSection();
}

// ── PDF helpers ───────────────────────────

function pdfFilename(title) {
  return (title || 'betha-documento')
    .toLowerCase()
    .normalize('NFD').replace(/[̀-ͯ]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '');
}

// ── Utilities ─────────────────────────────

function formatDateLong(dateStr) {
  if (!dateStr) return '';
  const months = [
    'janeiro','fevereiro','março','abril','maio','junho',
    'julho','agosto','setembro','outubro','novembro','dezembro',
  ];
  const [y, m, d] = dateStr.split('-');
  return `${parseInt(d)} de ${months[parseInt(m) - 1]} de ${y}`;
}

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
