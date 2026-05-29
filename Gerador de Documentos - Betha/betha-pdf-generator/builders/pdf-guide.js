/* ──────────────────────────────────────────
   pdf-guide.js — PDF para Guia Passo a Passo
   ────────────────────────────────────────── */

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
    ctx.setColor(ACCENT, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(11);
    doc.text(ctx.safeText(`${index + 1}. ${(section.title || 'Seção').toUpperCase()}`), ML, y + 5.5);
    y += 10;
    ctx.setColor([210, 218, 228], 'draw');
    doc.setLineWidth(0.3);
    doc.line(ML, y, PW - MR, y);
    y += 6;
    section.steps.forEach((step, si) => { if (richHTMLToPlain(step.text || '').trim() || step.image) drawStep(step, si + 1); });
    y += 4;
  }

  function drawStep(step, num) {
    const segs   = parseRichHTMLSegments(step.text || '');
    const lines  = richHTMLToPlain(step.text || '').trim() ? buildRichLines(doc, segs, CW - 14, 10) : [];
    const textH  = Math.max(1, lines.length) * ctx.lineHeight(10) + 2;
    ensureSpace(textH + 8);

    ctx.setColor([180, 196, 220], 'fill');
    doc.circle(ML + 3, y + 3, 3, 'F');
    ctx.setColor(WHITE, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7.5);
    doc.text(String(num), ML + 3, y + 3.8, { align: 'center' });

    ctx.setColor(TEXT, 'text');
    drawRichLines(doc, lines, ML + 9, y + 4, 10);
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

    const textX = ML + 10;
    const textW = CW - 14;
    const segs = parseRichHTMLSegments(text);
    const richLines = buildRichLines(doc, segs.length ? segs : [{ text: text, bold: false, italic: false }], textW, 9);
    const textH   = richLines.length * ctx.lineHeight(9);
    const labelLH = ctx.lineHeight(7.5);
    const h = 5 + labelLH + 2 + textH + 6;
    ensureSpace(h + 4);

    // Fundo muito sutil
    ctx.setColor(cfg.fill, 'fill');
    doc.roundedRect(ML + 4, y, CW - 4, h, 2, 2, 'F');

    // Borda fina neutra
    ctx.setColor(BORDER, 'draw');
    doc.setLineWidth(0.2);
    doc.roundedRect(ML + 4, y, CW - 4, h, 2, 2, 'S');

    // Label — texto simples, bold, cor do tipo (sem pill)
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7.5);
    ctx.setColor(cfg.border, 'text');
    doc.text(cfg.label.toUpperCase(), textX, y + 5 + labelLH * 0.72);

    // Texto do corpo
    ctx.setColor([75, 85, 99], 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    if (richLines.length) drawRichLines(doc, richLines, textX, y + 5 + labelLH + 2 + ctx.lineHeight(9) * 0.72, 9);
    y += h + 3;
  }

  y = drawPDFSharedHeader(doc, data, ACCENT, reg.label);
  data.sections.forEach((section, i) => drawSection(section, i));
  drawPDFFooter(doc, ctx, data, ACCENT, page);

  doc.save(`${pdfFilename(data.doc.title)}.pdf`);
  showToast('PDF gerado com sucesso!', 'success');
}
