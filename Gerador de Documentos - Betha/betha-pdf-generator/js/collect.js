/* ──────────────────────────────────────────
   collect.js — coleta de dados do formulário e geração de documentos
   ────────────────────────────────────────── */

// ── Data collection ───────────────────────

function collectData() {
  const rteText = (id) => (document.getElementById(id)?.textContent || '').trim();
  const doc = {
    title:   rteText('doc-title'),
    entity:  rteText('doc-entity'),
    module:  rteText('doc-module'),
    ticket:  rteText('doc-ticket'),
    author:  rteText('doc-author'),
    date:    document.getElementById('doc-date').value,
    type:    currentDocType,
  };

  const settings = {
    showLogo:        true,
    showFooter:      true,
    showPageNumbers: true,
  };

  if (currentDocType === 'changelog') {
    const entries = [];
    document.querySelectorAll('.changelog-entry').forEach(el => {
      entries.push({
        type:  el.querySelector('.entry-type').value,
        title: el.querySelector('.entry-title').innerHTML || '',
        desc:  el.querySelector('.entry-desc').innerHTML || '',
      });
    });
    return { doc, settings, entries };
  }

  if (currentDocType === 'operational') {
    const opSections = [];
    document.querySelectorAll('.op-section-block').forEach(secEl => {
      const items = [];
      secEl.querySelectorAll('.op-item-row').forEach(itemRow => {
        const html = itemRow.querySelector('.op-item-input').innerHTML || '';
        const text = html === '<br>' ? '' : html;
        if (!text.trim()) return;
        const subItems = [];
        itemRow.querySelectorAll('.op-subitem-input').forEach(sub => {
          const sh = sub.innerHTML || '';
          const st = sh === '<br>' ? '' : sh;
          if (st.trim()) subItems.push(st);
        });
        items.push({ text, subItems });
      });
      opSections.push({
        title: (secEl.querySelector('.op-section-title-input').textContent || '').trim(),
        items,
      });
    });
    return {
      doc, settings,
      opSections,
      conclusion: document.getElementById('op-conclusion').innerHTML || '',
      signatures: {
        name1: (document.getElementById('sig-name1')?.textContent || '').trim(),
        name2: (document.getElementById('sig-name2')?.textContent || '').trim(),
        city:  doc.entity,   // reutiliza o campo Entidade do cabeçalho
      },
    };
  }

  const sections = [];
  document.querySelectorAll('.section-block').forEach(secEl => {
    const steps = [];
    secEl.querySelectorAll('.step-block').forEach(stepEl => {
      steps.push({
        text:      stepEl.querySelector('.step-text').innerHTML || '',
        alertType: stepEl.querySelector('.step-alert-select').value,
        alertText: (() => { const el = stepEl.querySelector('.step-alert-input'); const h = el?.innerHTML || ''; return h === '<br>' ? '' : h; })(),
        image:     stepEl.dataset.img || null,
      });
    });
    sections.push({
      title: (secEl.querySelector('.section-title-input').textContent || '').trim(),
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
  } else if (currentDocType === 'operational') {
    const hasItems = data.opSections && data.opSections.some(s => s.items.length > 0);
    if (!hasItems) {
      showToast('Adicione pelo menos um item de ajuste.', 'error');
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
