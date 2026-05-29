/* ──────────────────────────────────────────
   ui.js — interações de UI: seções, passos, entradas, operacional
   ────────────────────────────────────────── */

function getAlertOptions() {
  return `
    <option value="">Sem alerta</option>
    <option value="info">💡 Dica</option>
    <option value="warning">⚠️ Atenção</option>
    <option value="danger">🚨 Crítico</option>`;
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
    if (summary) summary.textContent = e.target.textContent.trim() || 'Sem título';
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
      <div class="rich-textarea section-title-input" contenteditable="true" data-single-line data-placeholder="Título da Seção (ex: Acesso ao Sistema)"></div>
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
      <div class="rich-textarea step-text" contenteditable="true" data-placeholder="Descreva este passo com clareza..."></div>
      <div class="step-opts">
        <select class="step-alert-select">${getAlertOptions()}</select>
        <button class="btn-icon btn-add-image" type="button">🖼 Imagem</button>
        <button class="btn-icon danger btn-remove-step" type="button" title="Remover passo">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
        <input type="file" class="img-input" accept="image/*" style="display:none">
      </div>
      <div class="step-alert-row" style="display:none">
        <div class="rich-textarea step-alert-input" contenteditable="true" data-placeholder="Texto do alerta..."></div>
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
        <div class="rich-textarea entry-title" contenteditable="true" data-single-line data-placeholder="Ex: Evento 75 — Horas Férias"></div>
      </div>
      <div class="entry-bottom-row">
        <label>Descrição</label>
        <div class="rich-textarea entry-desc" contenteditable="true" data-placeholder="Descreva a alteração realizada..."></div>
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

// ── Operational sections ──────────────────

function addOpSection() {
  document.querySelectorAll('.op-section-block').forEach(s => s.classList.add('collapsed'));

  opSectionCounter++;
  const sid = `ops${opSectionCounter}`;

  const el = document.createElement('div');
  el.className = 'op-section-block';
  el.dataset.sid = sid;
  el.innerHTML = `
    <div class="op-section-head">
      <div class="rich-textarea op-section-title-input" contenteditable="true" data-single-line data-placeholder="Título da categoria (ex: Ajustes em Eventos)"></div>
      <div class="op-section-actions">
        <button class="btn-icon btn-add-op-item" title="Adicionar item">+ Item</button>
        <button class="btn-icon btn-toggle-op-section" title="Minimizar / Expandir">
          <svg class="toggle-arrow" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="6 9 12 15 18 9"/></svg>
        </button>
        <button class="btn-icon danger btn-remove-op-section" title="Remover categoria">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
    </div>
    <div class="op-section-body">
      <div class="op-items-wrap" id="op-items-${sid}"></div>
      <div><button class="btn btn-ghost btn-sm btn-add-op-item">+ Adicionar Item</button></div>
    </div>
  `;

  const container = document.getElementById('op-sections-container');
  const empty = document.getElementById('empty-op-sections');
  if (empty) empty.remove();
  container.appendChild(el);

  addOpItem(sid);
}

function removeOpSection(el) {
  el.style.opacity = '0';
  el.style.transform = 'scale(.97)';
  el.style.transition = 'opacity .2s, transform .2s';
  setTimeout(() => {
    el.remove();
    if (!document.querySelector('.op-section-block')) {
      document.getElementById('op-sections-container').innerHTML =
        `<div class="empty-state" id="empty-op-sections">
          <div class="empty-icon">🔧</div>
          <p>Nenhuma categoria criada ainda.</p>
          <p class="empty-hint">Clique em "Adicionar Categoria" para começar.</p>
        </div>`;
    }
  }, 200);
}

function addOpItem(sid) {
  const itemsWrap = document.getElementById(`op-items-${sid}`);
  const el = document.createElement('div');
  el.className = 'op-item-row';
  el.innerHTML = `
    <div class="op-item-main">
      <div class="op-item-bullet"></div>
      <div class="rich-textarea op-item-input" contenteditable="true" data-placeholder="Descreva o item ajustado..."></div>
      <div class="op-item-row-actions">
        <button class="btn-icon btn-add-op-subitem" title="Adicionar subitem">+ Sub</button>
        <button class="btn-icon danger btn-remove-op-item" title="Remover item">
          <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
    </div>
    <div class="op-subitems-wrap"></div>
  `;
  itemsWrap.appendChild(el);
  el.querySelector('.op-item-input').focus();
}

function addOpSubItem(itemRow) {
  const subWrap = itemRow.querySelector('.op-subitems-wrap');
  const el = document.createElement('div');
  el.className = 'op-subitem-row';
  el.innerHTML = `
    <div class="op-subitem-bullet"></div>
    <div class="rich-textarea op-subitem-input" contenteditable="true" data-single-line data-placeholder="Subitem..."></div>
    <button class="btn-icon danger btn-remove-op-subitem" title="Remover subitem">
      <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
    </button>
  `;
  subWrap.appendChild(el);
  el.querySelector('.op-subitem-input').focus();
}

function handleOpContainerClick(e) {
  const t = e.target;
  if (t.closest('.btn-toggle-op-section')) {
    t.closest('.op-section-block').classList.toggle('collapsed');
    return;
  }
  if (t.closest('.btn-remove-op-section')) {
    removeOpSection(t.closest('.op-section-block'));
    return;
  }
  if (t.closest('.btn-add-op-item')) {
    addOpItem(t.closest('.op-section-block').dataset.sid);
    return;
  }
  if (t.closest('.btn-add-op-subitem')) {
    addOpSubItem(t.closest('.op-item-row'));
    return;
  }
  if (t.closest('.btn-remove-op-subitem')) {
    t.closest('.op-subitem-row').remove();
    return;
  }
  if (t.closest('.btn-remove-op-item')) {
    t.closest('.op-item-row').remove();
    return;
  }
}
