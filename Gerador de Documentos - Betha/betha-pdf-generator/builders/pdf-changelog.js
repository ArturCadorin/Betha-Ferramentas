/* ──────────────────────────────────────────
   pdf-changelog.js — PDF para Registro de Alterações
   ────────────────────────────────────────── */

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

    const titleSegs  = parseRichHTMLSegments(entry.title || '—');
    const titleRich  = buildRichLines(doc, titleSegs.length ? titleSegs : [{ text: '—', bold: false, italic: false }], contentW, 9.5);
    const titleH     = titleRich.length * ctx.lineHeight(9.5);
    const descSegs   = parseRichHTMLSegments(entry.desc || '');
    const descRich   = buildRichLines(doc, descSegs, contentW, 9);
    const hasDesc    = descRich.length > 0 && descRich[0].length > 0;
    const rowH       = Math.max(16, titleH + (hasDesc ? 3 + descRich.length * ctx.lineHeight(9) : 0) + 8);

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
    drawRichLines(doc, titleRich, contentX, y + 5, 9.5);

    if (hasDesc) {
      ctx.setColor([55, 65, 81], 'text');
      drawRichLines(doc, descRich, contentX, y + 5 + titleH + 3, 9);
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
