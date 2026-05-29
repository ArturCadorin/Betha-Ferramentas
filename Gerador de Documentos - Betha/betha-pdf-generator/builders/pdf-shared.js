/* ──────────────────────────────────────────
   pdf-shared.js — constantes, contexto e helpers PDF compartilhados
   ────────────────────────────────────────── */

// ── Paleta de marca compartilhada ─────────
const BRAND = {
  WORDMARK:  [95, 118, 180],
  SLOGAN:    [148, 165, 210],
  GUIDE_ACC: [88, 110, 172],
  GUIDE_BG:  [88, 110, 172],
  TECH_ACC:  [13, 148, 120],
  TECH_BG:   [30, 41, 59],
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
