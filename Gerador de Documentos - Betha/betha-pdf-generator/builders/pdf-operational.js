/* ──────────────────────────────────────────
   pdf-operational.js — PDF para Relatório Operacional
   ────────────────────────────────────────── */

// ── PDF: Relatório Operacional ────────────

async function buildOperationalDocPDF(data) {
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ orientation: 'p', unit: 'mm', format: 'a4' });
  const ctx = createPDFCtx(doc);
  const { PW, PH, ML, MR, MB, CW, TEXT, WHITE, BORDER, MUTED } = ctx;

  const reg    = DOC_REGISTRY.operational;
  const ACCENT = reg.accentRGB;

  let page = 1, y = 0;

  function ensureSpace(needed) {
    if (y + needed > PH - MB) {
      drawPDFFooter(doc, ctx, data, ACCENT, page);
      doc.addPage(); page++;
      y = drawPDFContinuationHeader(doc, ctx, data, reg.bgRGB);
    }
  }

  function drawOpSection(section, index) {
    ensureSpace(18);
    ctx.setColor(ACCENT, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(11);
    doc.text(ctx.safeText(`${index + 1}. ${(section.title || 'Ajustes').toUpperCase()}`), ML, y + 5.5);
    y += 10;
    ctx.setColor([210, 218, 228], 'draw');
    doc.setLineWidth(0.3);
    doc.line(ML, y, PW - MR, y);
    y += 5;

    section.items.forEach(item => {
      if (!item) return;
      const itemText = typeof item === 'string' ? item : item.text;
      const subItems = typeof item === 'string' ? [] : (item.subItems || []);
      if (!itemText) return;

      const segs  = parseRichHTMLSegments(itemText);
      const lines = segs.length ? buildRichLines(doc, segs, CW - 9, 10) : [];
      const h     = Math.max(1, lines.length) * ctx.lineHeight(10);
      ensureSpace(h + 5);
      ctx.setColor([190, 205, 225], 'fill');
      doc.circle(ML + 2.5, y + 2.5, 1.3, 'F');
      ctx.setColor(TEXT, 'text');
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(10);
      if (lines.length) drawRichLines(doc, lines, ML + 6, y + 3.8, 10);
      y += h + 3.5;

      subItems.forEach(subItem => {
        if (!subItem) return;
        const subSegs  = parseRichHTMLSegments(subItem);
        const subLines = subSegs.length ? buildRichLines(doc, subSegs, CW - 19, 9.5) : [];
        const subH     = Math.max(1, subLines.length) * ctx.lineHeight(9.5);
        ensureSpace(subH + 4);
        ctx.setColor([200, 210, 225], 'fill');
        doc.circle(ML + 10, y + 2.2, 0.9, 'F');
        ctx.setColor([55, 65, 81], 'text');
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(9.5);
        if (subLines.length) drawRichLines(doc, subLines, ML + 14, y + 3.5, 9.5);
        y += subH + 3;
      });
    });
    y += 5;
  }

  function drawConclusion(html) {
    const plain = richHTMLToPlain(html).trim();
    if (!plain) return;
    ensureSpace(30);
    ctx.setColor(ACCENT, 'text');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(11);
    doc.text('CONCLUSÃO', ML, y + 5.5);
    y += 10;
    ctx.setColor([210, 218, 228], 'draw');
    doc.setLineWidth(0.3);
    doc.line(ML, y, PW - MR, y);
    y += 6;

    const segs  = parseRichHTMLSegments(html);
    const lines = buildRichLines(doc, segs, CW, 10);
    const textH = lines.length * ctx.lineHeight(10);
    ensureSpace(textH + 6);

    ctx.setColor(TEXT, 'text');
    drawRichLines(doc, lines, ML, y + 4, 10);
    y += textH + 12;
  }

  function drawSignatures(signatures) {
    const lineW  = 74;
    const leftX  = ML + 6;
    const rightX = PW - MR - lineW - 6;

    ensureSpace(46);
    y += 8;

    ctx.setColor([148, 158, 178], 'draw');
    doc.setLineWidth(0.5);
    doc.line(leftX,  y + 16, leftX  + lineW, y + 16);
    doc.line(rightX, y + 16, rightX + lineW, y + 16);

    ctx.setColor(MUTED, 'text');
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(8.5);
    if (signatures.name1) {
      doc.text(ctx.safeText(signatures.name1), leftX + lineW / 2, y + 21.5, { align: 'center' });
    }
    if (signatures.name2) {
      doc.text(ctx.safeText(signatures.name2), rightX + lineW / 2, y + 21.5, { align: 'center' });
    }

    const cityDate = [
      signatures.city && ctx.safeText(signatures.city),
      data.doc.date ? formatDateLong(data.doc.date) : '',
    ].filter(Boolean).join(', ');
    if (cityDate) {
      doc.setFontSize(8);
      doc.text(cityDate, PW / 2, y + 30, { align: 'center' });
    }
    y += 36;
  }

  y = drawPDFSharedHeader(doc, data, ACCENT, reg.label);
  data.opSections.forEach((section, i) => drawOpSection(section, i));
  drawConclusion(data.conclusion);
  const sig = data.signatures;
  if (sig.name1 || sig.name2 || sig.city) drawSignatures(sig);
  drawPDFFooter(doc, ctx, data, ACCENT, page);

  doc.save(`relatorio-${pdfFilename(data.doc.title)}.pdf`);
  showToast('PDF gerado com sucesso!', 'success');
}
