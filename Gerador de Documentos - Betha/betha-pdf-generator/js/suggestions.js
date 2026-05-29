// ── Sugestões / Feedback ──────────────────────────────────────────────────────
//
// SETUP (fazer uma vez):
// 1. Acesse https://web3forms.com
// 2. Digite seu e-mail → clique "Create Access Key"
// 3. Cole a chave gerada em WEB3FORMS_KEY abaixo
// ─────────────────────────────────────────────────────────────────────────────

const WEB3FORMS_KEY = 'SUA_CHAVE_AQUI';

// ─────────────────────────────────────────────────────────────────────────────

function initSuggestions() {
  _buildSuggestionModal();

  document.getElementById('btn-suggestion-float')
    .addEventListener('click', () => _openSuggestionModal());
}

function _buildSuggestionModal() {
  // Botão flutuante
  const fab = document.createElement('button');
  fab.id        = 'btn-suggestion-float';
  fab.className = 'sug-fab';
  fab.title     = 'Enviar sugestão ou feedback';
  fab.innerHTML = `
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
    </svg>
    <span>Sugestão</span>
  `;
  document.body.appendChild(fab);

  // Modal
  const modal = document.createElement('div');
  modal.id        = 'sug-modal';
  modal.className = 'sug-overlay';
  modal.style.display = 'none';
  modal.innerHTML = `
    <div class="sug-box">
      <div class="sug-header">
        <h3>Enviar sugestão</h3>
        <button type="button" class="sug-close" id="btn-sug-close">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>

      <div class="sug-body" id="sug-body">
        <div class="sug-types">
          <label class="sug-type-opt">
            <input type="radio" name="sug-tipo" value="Solicitação de documento" checked>
            <span>📄 Solicitação de novo documento</span>
          </label>
          <label class="sug-type-opt">
            <input type="radio" name="sug-tipo" value="Melhoria de relatório existente">
            <span>🔧 Melhoria de relatório existente</span>
          </label>
          <label class="sug-type-opt">
            <input type="radio" name="sug-tipo" value="Outro">
            <span>✏️ Outro</span>
          </label>
        </div>

        <div id="sug-relatorio-wrap" style="display:none;margin-top:12px">
          <label style="font-size:13px;font-weight:500;margin-bottom:6px;display:block">Qual relatório? <span style="color:#EF4444">*</span></label>
          <div class="sug-relatorio-opts">
            <label class="sug-rel-opt">
              <input type="radio" name="sug-relatorio" value="Guia Passo a Passo">
              <span>📋 Guia Passo a Passo</span>
            </label>
            <label class="sug-rel-opt">
              <input type="radio" name="sug-relatorio" value="Registro de Alterações">
              <span>📝 Registro de Alterações</span>
            </label>
            <label class="sug-rel-opt">
              <input type="radio" name="sug-relatorio" value="Relatório Operacional">
              <span>🔧 Relatório Operacional</span>
            </label>
          </div>
        </div>

        <div class="form-group" style="margin-top:14px">
          <label>Seu nome <span style="color:var(--text-muted);font-weight:400">(opcional)</span></label>
          <input type="text" id="sug-nome" placeholder="Ex: Artur Cadorin" autocomplete="off">
        </div>

        <div class="form-group" style="margin-top:12px">
          <label>Mensagem <span style="color:#EF4444">*</span></label>
          <textarea id="sug-msg" rows="4" placeholder="Descreva sua sugestão ou solicitação..."></textarea>
        </div>

        <div id="sug-error" class="sug-error" style="display:none"></div>
      </div>

      <div class="sug-success" id="sug-success" style="display:none">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#059669" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
        <p>Sugestão enviada com sucesso!</p>
        <span>Obrigado pela contribuição.</span>
      </div>

      <div class="sug-footer">
        <button type="button" class="btn btn-ghost btn-sm" id="btn-sug-cancel">Cancelar</button>
        <button type="button" class="btn btn-primary btn-sm" id="btn-sug-submit">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
          Enviar
        </button>
      </div>
    </div>
  `;
  document.body.appendChild(modal);

  // Eventos
  document.getElementById('btn-sug-close').addEventListener('click', _closeSuggestionModal);
  document.getElementById('btn-sug-cancel').addEventListener('click', _closeSuggestionModal);
  modal.addEventListener('click', (e) => { if (e.target === modal) _closeSuggestionModal(); });
  document.getElementById('btn-sug-submit').addEventListener('click', _submitSuggestion);

  // Mostrar/ocultar campo de relatório
  modal.addEventListener('change', (e) => {
    if (e.target.name === 'sug-tipo') {
      const wrap = document.getElementById('sug-relatorio-wrap');
      const isMelhoria = e.target.value === 'Melhoria de relatório existente';
      wrap.style.display = isMelhoria ? '' : 'none';
      if (!isMelhoria) {
        modal.querySelectorAll('input[name="sug-relatorio"]').forEach(r => r.checked = false);
      }
    }
  });
}

function _openSuggestionModal() {
  const modal = document.getElementById('sug-modal');
  document.getElementById('sug-body').style.display    = '';
  document.getElementById('sug-success').style.display = 'none';
  document.getElementById('sug-error').style.display   = 'none';
  document.getElementById('sug-nome').value = '';
  document.getElementById('sug-msg').value  = '';
  document.getElementById('sug-relatorio-wrap').style.display = 'none';
  modal.querySelectorAll('input[name="sug-tipo"]')[0].checked = true;
  modal.querySelectorAll('input[name="sug-relatorio"]').forEach(r => r.checked = false);
  document.querySelector('.sug-footer').style.display = '';
  modal.style.display = 'flex';
}

function _closeSuggestionModal() {
  document.getElementById('sug-modal').style.display = 'none';
}

async function _submitSuggestion() {
  const tipo      = document.querySelector('input[name="sug-tipo"]:checked')?.value || '';
  const relatorio = document.querySelector('input[name="sug-relatorio"]:checked')?.value || '';
  const nome      = document.getElementById('sug-nome').value.trim();
  const msg       = document.getElementById('sug-msg').value.trim();
  const err       = document.getElementById('sug-error');

  if (tipo === 'Melhoria de relatório existente' && !relatorio) {
    err.textContent = 'Selecione qual relatório deseja melhorar.';
    err.style.display = '';
    return;
  }
  if (!msg) {
    err.textContent = 'Preencha o campo Mensagem antes de enviar.';
    err.style.display = '';
    return;
  }
  err.style.display = 'none';

  const msgFinal = relatorio ? `[${relatorio}] ${msg}` : msg;

  const btn = document.getElementById('btn-sug-submit');
  btn.disabled = true;
  btn.textContent = 'Enviando...';

  try {
    const res = await fetch('https://api.web3forms.com/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({
        access_key: WEB3FORMS_KEY,
        subject:    `[Sugestão Gerador Betha] ${tipo}`,
        from_name:  nome || 'Anônimo',
        tipo,
        relatorio:  relatorio || '-',
        mensagem:   msgFinal,
      }),
    });
    const json = await res.json();
    if (!json.success) throw new Error(json.message || 'Erro no envio');

    document.getElementById('sug-body').style.display    = 'none';
    document.getElementById('sug-success').style.display = '';
    document.querySelector('.sug-footer').style.display  = 'none';
    setTimeout(_closeSuggestionModal, 2800);

  } catch (e) {
    err.textContent = `Erro ao enviar: ${e.message}`;
    err.style.display = '';
    btn.disabled = false;
    btn.innerHTML = `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg> Enviar`;
  }
}
