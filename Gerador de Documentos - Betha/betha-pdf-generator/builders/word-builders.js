/* ──────────────────────────────────────────
   word-builders.js — construtores de documentos Word (.docx)
   ────────────────────────────────────────── */

// ── Word: metadados compartilhados ────────

// Retorna o array de strings de metadados do documento.
function collectWordMetaItems(data) {
  return [
    data.doc.entity && `Entidade: ${data.doc.entity}`,
    data.doc.module && `Módulo: ${data.doc.module}`,
    data.doc.author && `Responsável: ${data.doc.author}`,
    data.doc.date   && `Data: ${formatDate(data.doc.date)}`,
    data.doc.ticket && `Chamado: ${data.doc.ticket}`,
  ].filter(Boolean);
}

// Retorna os elementos Word da tabela de metadados (array pronto para push).
function makeWordMetaTable(D, accentHex, items) {
  if (!items.length) return [];
  const NONE = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };
  return [
    new D.Table({
      width: { size: 100, type: D.WidthType.PERCENTAGE },
      borders: { top: NONE, bottom: NONE, left: NONE, right: NONE, insideH: NONE, insideV: NONE },
      rows: [new D.TableRow({
        children: [new D.TableCell({
          shading: { fill: 'F8FAFC', type: D.ShadingType.CLEAR, color: 'auto' },
          borders: { top: { style: D.BorderStyle.SINGLE, size: 4, color: accentHex, space: 1 }, bottom: NONE, left: NONE, right: NONE },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: items.map(item => new D.Paragraph({
            children: [new D.TextRun({ text: item, size: 18, color: '374151' })],
            spacing: { before: 40, after: 40 },
          })),
        })],
      })],
    }),
    new D.Paragraph({ spacing: { after: 320 }, children: [] }),
  ];
}

// ── Word: helpers ─────────────────────────

function base64ToUint8Array(base64) {
  const b64 = base64.includes(',') ? base64.split(',')[1] : base64;
  const binary = atob(b64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
  return bytes;
}

function getImageDimensions(src) {
  return new Promise(resolve => {
    const img = new Image();
    img.onload  = () => resolve({ width: img.naturalWidth, height: img.naturalHeight });
    img.onerror = () => resolve({ width: 400, height: 300 });
    img.src = src;
  });
}

function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

// ── Word: generate ────────────────────────

async function generateWord() {
  if (!window.docx) {
    showToast('Biblioteca Word não carregada. Verifique a conexão.', 'error');
    return;
  }

  const data = collectData();

  if (!data.doc.title) {
    showToast('Informe o título do documento antes de gerar o Word.', 'error');
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

  showToast('Gerando Word…');

  try {
    await DOC_REGISTRY[currentDocType].buildWord(data);
  } catch (err) {
    console.error(err);
    showToast('Erro ao gerar o Word. Veja o console para detalhes.', 'error');
  }
}

// ── Word: footer helper ───────────────────

function makeWordFooter(D, accentHex) {
  const NONE = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };
  return new D.Table({
    width: { size: 100, type: D.WidthType.PERCENTAGE },
    borders: {
      top:     { style: D.BorderStyle.SINGLE, size: 4, color: 'E5E7EB' },
      bottom:  NONE, left: NONE, right: NONE, insideH: NONE, insideV: NONE,
    },
    rows: [new D.TableRow({
      children: [
        new D.TableCell({
          width: { size: 50, type: D.WidthType.PERCENTAGE },
          borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
          margins: { top: 120 },
          children: [
            new D.Paragraph({
              children: [new D.TextRun({ text: 'Matriz Betha Sistemas', bold: true, size: 16, color: accentHex })],
              spacing: { after: 40 },
            }),
            new D.Paragraph({
              children: [new D.TextRun({ text: 'Rua Júlio Gaidzinski, 320,', size: 14, color: '6B7280' })],
              spacing: { after: 20 },
            }),
            new D.Paragraph({
              children: [new D.TextRun({ text: '88811-000, Pio Corrêa / Criciúma - SC', size: 14, color: '6B7280' })],
            }),
          ],
        }),
        new D.TableCell({
          width: { size: 50, type: D.WidthType.PERCENTAGE },
          borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
          margins: { top: 120 },
          children: [
            new D.Paragraph({
              alignment: D.AlignmentType.RIGHT,
              children: [new D.TextRun({ text: '48 3431-0733', bold: true, size: 16, color: accentHex })],
              spacing: { after: 40 },
            }),
            new D.Paragraph({
              alignment: D.AlignmentType.RIGHT,
              children: [new D.TextRun({ text: 'Atendimento técnico', size: 14, color: '6B7280' })],
              spacing: { after: 20 },
            }),
            new D.Paragraph({
              alignment: D.AlignmentType.RIGHT,
              children: [new D.TextRun({ text: '0800 600 0735', bold: true, size: 16, color: accentHex })],
            }),
          ],
        }),
      ],
    })],
  });
}

// ── Word: header helper ───────────────────

function makeWordHeader(D, accentHex, showLogo) {
  if (!showLogo) return null;
  return new D.Paragraph({
    spacing: { after: 120 },
    children: [
      new D.TextRun({ text: 'BETHA  ', bold: true, italics: true, size: 38, color: '5F76B4' }),
      new D.TextRun({ text: 'Tudo que a sua cidade  •  pode se tornar', size: 14, color: '94A5D2' }),
    ],
  });
}

// ── Word: Guide + Technical ───────────────

async function buildSectionedDocWord(data) {
  const D = window.docx;
  const reg      = DOC_REGISTRY[data.doc.type] || DOC_REGISTRY.guide;
  const ACCENT   = reg.accentHex;
  const TYPE_LBL = reg.wordTypeLabel;
  const ALERTS   = reg.alertWordCfg;
  const NONE     = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };

  function makeAlertTable(alertType, alertText) {
    const cfg = ALERTS[alertType];
    if (!cfg) return [];
    return [
      new D.Table({
        width: { size: 100, type: D.WidthType.PERCENTAGE },
        borders: { top: NONE, bottom: NONE, left: NONE, right: NONE, insideH: NONE, insideV: NONE },
        rows: [new D.TableRow({
          children: [
            new D.TableCell({
              width: { size: 3, type: D.WidthType.PERCENTAGE },
              shading: { fill: cfg.border, type: D.ShadingType.CLEAR, color: 'auto' },
              borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
              children: [new D.Paragraph({ children: [] })],
            }),
            new D.TableCell({
              width: { size: 97, type: D.WidthType.PERCENTAGE },
              shading: { fill: cfg.fill, type: D.ShadingType.CLEAR, color: 'auto' },
              borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
              margins: { top: 60, bottom: 60, left: 120, right: 120 },
              children: [new D.Paragraph({
                children: [
                  new D.TextRun({ text: `${cfg.label}  `, bold: true, size: 17, color: cfg.border }),
                  new D.TextRun({ text: alertText, size: 17, color: '4B5563' }),
                ],
              })],
            }),
          ],
        })],
      }),
      new D.Paragraph({ spacing: { after: 80 }, children: [] }),
    ];
  }

  const children = [];

  // Header: wordmark BETHA + slogan
  const _hdr1 = makeWordHeader(D, ACCENT, data.settings.showLogo);
  if (_hdr1) { children.push(_hdr1); children.push(new D.Paragraph({ spacing: { after: 160 }, children: [] })); }

  // Tipo do documento
  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: TYPE_LBL, size: 16, color: '6B7280' })],
    spacing: { after: 80 },
  }));

  // Título
  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: data.doc.title || 'Documento', bold: true, size: 52, color: ACCENT })],
    spacing: { after: 240 },
  }));

  // Separador
  children.push(new D.Paragraph({
    border: { bottom: { style: D.BorderStyle.SINGLE, size: 8, color: ACCENT, space: 1 } },
    spacing: { after: 200 },
    children: [],
  }));

  // Metadados
  children.push(...makeWordMetaTable(D, ACCENT, collectWordMetaItems(data)));

  // Seções
  for (const [sIdx, section] of data.sections.entries()) {
    children.push(new D.Paragraph({
      border: { left: { style: D.BorderStyle.THICK, size: 14, color: ACCENT, space: 4 } },
      spacing: { before: 360, after: 160 },
      children: [
        new D.TextRun({
          text: `  ${sIdx + 1}. ${(section.title || 'Seção').toUpperCase()}`,
          bold: true, size: 26, color: ACCENT,
        }),
      ],
    }));

    for (const [stIdx, step] of section.steps.entries()) {
      if (!richHTMLToPlain(step.text || '').trim() && !step.image) continue;

      if (richHTMLToPlain(step.text || '').trim()) {
        children.push(new D.Paragraph({
          spacing: { before: 100, after: 80 },
          children: [
            new D.TextRun({ text: `${stIdx + 1}.  `, bold: true, size: 21, color: ACCENT }),
            ...htmlToWordRuns(D, step.text, { size: 21, color: '1A1D2E' }),
          ],
        }));
      }

      if (step.alertType && step.alertText) {
        children.push(...makeAlertTable(step.alertType, step.alertText));
      }

      if (step.image) {
        try {
          const imgData = base64ToUint8Array(step.image);
          const dims    = await getImageDimensions(step.image);
          const maxW    = 550;
          const scale   = Math.min(1, maxW / dims.width);
          const w = Math.round(dims.width * scale);
          const h = Math.round(dims.height * scale);
          children.push(new D.Paragraph({
            spacing: { before: 80, after: 80 },
            children: [new D.ImageRun({ data: imgData, transformation: { width: w, height: h } })],
          }));
        } catch (e) {
          console.warn('Imagem não incluída no Word:', e);
        }
      }
    }
    children.push(new D.Paragraph({ spacing: { after: 80 }, children: [] }));
  }

  // Rodapé
  if (data.settings.showFooter) {
    children.push(new D.Paragraph({ spacing: { before: 480 }, children: [] }));
    children.push(makeWordFooter(D, ACCENT));
  }

  const wordDoc = new D.Document({ sections: [{ properties: {}, children }] });
  const blob    = await D.Packer.toBlob(wordDoc);
  downloadBlob(blob, `${pdfFilename(data.doc.title)}.docx`);
  showToast('Word gerado com sucesso!', 'success');
}

// ── Word: Changelog ───────────────────────

async function buildChangelogDocWord(data) {
  const D    = window.docx;
  const BLUE = '586EAC';

  const TYPE_CFG = {
    addition:    { fill: 'D1FAE5', text: '065F46', label: 'Criação' },
    fix:         { fill: 'DBEAFE', text: '1E40AF', label: 'Correção' },
    change:      { fill: 'FEF3C7', text: '92400E', label: 'Alteração' },
    removal:     { fill: 'FEE2E2', text: '991B1B', label: 'Remoção' },
    improvement: { fill: 'EDE9FE', text: '5B21B6', label: 'Melhoria' },
    security:    { fill: 'F3F4F6', text: '374151', label: 'Segurança' },
  };

  const NONE = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };
  const BDR  = { style: D.BorderStyle.SINGLE, size: 4, color: 'E5E7EB' };

  const children = [];

  // Header: wordmark BETHA + slogan
  const _hdr2 = makeWordHeader(D, BLUE, data.settings.showLogo);
  if (_hdr2) { children.push(_hdr2); children.push(new D.Paragraph({ spacing: { after: 160 }, children: [] })); }

  // Tipo + título
  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: 'REGISTRO DE ALTERAÇÕES • BETHA SISTEMAS', size: 16, color: '6B7280' })],
    spacing: { after: 80 },
  }));

  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: data.doc.title || 'Documento', bold: true, size: 52, color: '1A1D2E' })],
    spacing: { after: 240 },
  }));

  children.push(new D.Paragraph({
    border: { bottom: { style: D.BorderStyle.SINGLE, size: 8, color: BLUE, space: 1 } },
    spacing: { after: 200 },
    children: [],
  }));

  // Metadados
  children.push(...makeWordMetaTable(D, BLUE, collectWordMetaItems(data)));

  // Tabela
  const tableRows = [];

  tableRows.push(new D.TableRow({
    tableHeader: true,
    children: [
      new D.TableCell({
        width: { size: 25, type: D.WidthType.PERCENTAGE },
        shading: { fill: BLUE, type: D.ShadingType.CLEAR, color: 'auto' },
        borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new D.Paragraph({
          alignment: D.AlignmentType.CENTER,
          children: [new D.TextRun({ text: 'TIPO', bold: true, size: 18, color: 'FFFFFF' })],
        })],
      }),
      new D.TableCell({
        width: { size: 75, type: D.WidthType.PERCENTAGE },
        shading: { fill: BLUE, type: D.ShadingType.CLEAR, color: 'auto' },
        borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new D.Paragraph({
          children: [new D.TextRun({ text: 'DESCRIÇÃO', bold: true, size: 18, color: 'FFFFFF' })],
        })],
      }),
    ],
  }));

  data.entries.forEach((entry, i) => {
    const cfg     = TYPE_CFG[entry.type] || TYPE_CFG.change;
    const altFill = i % 2 === 1 ? 'F8FAFC' : 'FFFFFF';

    tableRows.push(new D.TableRow({
      children: [
        new D.TableCell({
          width: { size: 25, type: D.WidthType.PERCENTAGE },
          shading: { fill: altFill, type: D.ShadingType.CLEAR, color: 'auto' },
          borders: { top: BDR, bottom: BDR, left: BDR, right: BDR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [
            new D.Paragraph({
              alignment: D.AlignmentType.CENTER,
              children: [new D.TextRun({ text: `● ${cfg.label}`, bold: true, size: 17, color: cfg.text })],
            }),
          ],
        }),
        new D.TableCell({
          width: { size: 75, type: D.WidthType.PERCENTAGE },
          shading: { fill: altFill, type: D.ShadingType.CLEAR, color: 'auto' },
          borders: { top: BDR, bottom: BDR, left: BDR, right: BDR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [
            new D.Paragraph({
              children: [new D.TextRun({ text: entry.title || '—', bold: true, size: 20, color: '1A1D2E' })],
              spacing: { after: 80 },
            }),
            ...(entry.desc ? [new D.Paragraph({
              children: htmlToWordRuns(D, entry.desc, { size: 18, color: '374151' }),
            })] : []),
          ],
        }),
      ],
    }));
  });

  children.push(new D.Table({
    width: { size: 100, type: D.WidthType.PERCENTAGE },
    borders: { top: BDR, bottom: BDR, left: BDR, right: BDR, insideH: BDR, insideV: BDR },
    rows: tableRows,
  }));

  // Rodapé
  if (data.settings.showFooter) {
    children.push(new D.Paragraph({ spacing: { before: 480 }, children: [] }));
    children.push(makeWordFooter(D, BLUE));
  }

  const wordDoc = new D.Document({ sections: [{ properties: {}, children }] });
  const blob    = await D.Packer.toBlob(wordDoc);
  downloadBlob(blob, `registro-${pdfFilename(data.doc.title)}.docx`);
  showToast('Word gerado com sucesso!', 'success');
}

// ── Word: Relatório Operacional ───────────

async function buildOperationalDocWord(data) {
  const D    = window.docx;
  const reg  = DOC_REGISTRY.operational;
  const BLUE = reg.accentHex;
  const NONE = { style: D.BorderStyle.NONE, size: 0, color: 'FFFFFF' };
  const BDR_BLUE = { style: D.BorderStyle.SINGLE, size: 4, color: BLUE, space: 1 };

  const children = [];

  const _hdr = makeWordHeader(D, BLUE, data.settings.showLogo);
  if (_hdr) { children.push(_hdr); children.push(new D.Paragraph({ spacing: { after: 160 }, children: [] })); }

  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: 'RELATÓRIO OPERACIONAL • BETHA SISTEMAS', size: 16, color: '6B7280' })],
    spacing: { after: 80 },
  }));

  children.push(new D.Paragraph({
    children: [new D.TextRun({ text: data.doc.title || 'Documento', bold: true, size: 52, color: BLUE })],
    spacing: { after: 240 },
  }));

  children.push(new D.Paragraph({
    border: { bottom: { style: D.BorderStyle.SINGLE, size: 8, color: BLUE, space: 1 } },
    spacing: { after: 200 },
    children: [],
  }));

  children.push(...makeWordMetaTable(D, BLUE, collectWordMetaItems(data)));

  // Seções de ajuste
  data.opSections.forEach((section, sIdx) => {
    children.push(new D.Paragraph({
      border: { left: { style: D.BorderStyle.THICK, size: 14, color: BLUE, space: 4 } },
      spacing: { before: 360, after: 160 },
      children: [new D.TextRun({ text: `  ${sIdx + 1}. ${(section.title || 'Ajustes').toUpperCase()}`, bold: true, size: 24, color: BLUE })],
    }));
    section.items.forEach(item => {
      if (!item) return;
      const itemText = typeof item === 'string' ? item : item.text;
      const subItems = typeof item === 'string' ? [] : (item.subItems || []);
      if (!itemText) return;
      children.push(new D.Paragraph({
        bullet: { level: 0 },
        spacing: { before: 60, after: 60 },
        children: [new D.TextRun({ text: itemText, size: 21, color: '1A1D2E' })],
      }));
      subItems.forEach(subItem => {
        if (!subItem) return;
        children.push(new D.Paragraph({
          bullet: { level: 1 },
          spacing: { before: 40, after: 40 },
          children: [new D.TextRun({ text: subItem, size: 20, color: '374151' })],
        }));
      });
    });
    children.push(new D.Paragraph({ spacing: { after: 80 }, children: [] }));
  });

  // Conclusão (sem caixa — apenas texto)
  const conclusionPlain = richHTMLToPlain(data.conclusion || '').trim();
  if (conclusionPlain) {
    children.push(new D.Paragraph({
      border: { left: { style: D.BorderStyle.THICK, size: 14, color: BLUE, space: 4 } },
      spacing: { before: 360, after: 200 },
      children: [new D.TextRun({ text: '  CONCLUSÃO', bold: true, size: 24, color: BLUE })],
    }));
    children.push(new D.Paragraph({
      children: htmlToWordRuns(D, data.conclusion, { size: 21, color: '1A1D2E' }),
      spacing: { before: 40, after: 320 },
    }));
  }

  // Assinaturas (sem caixa — apenas linhas e nomes)
  const { name1, name2, city } = data.signatures;
  if (name1 || name2 || city) {
    children.push(new D.Paragraph({ spacing: { before: 600 }, children: [] }));
    const SIG_BDR = { style: D.BorderStyle.SINGLE, size: 6, color: '9CA3AF', space: 1 };
    children.push(new D.Table({
      width: { size: 100, type: D.WidthType.PERCENTAGE },
      borders: { top: NONE, bottom: NONE, left: NONE, right: NONE, insideH: NONE, insideV: NONE },
      rows: [
        new D.TableRow({
          children: [
            new D.TableCell({
              width: { size: 45, type: D.WidthType.PERCENTAGE },
              borders: { top: NONE, left: NONE, right: NONE, bottom: SIG_BDR },
              margins: { bottom: 80, left: 60, right: 60 },
              children: [new D.Paragraph({ children: [] })],
            }),
            new D.TableCell({
              width: { size: 10, type: D.WidthType.PERCENTAGE },
              borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
              children: [new D.Paragraph({ children: [] })],
            }),
            new D.TableCell({
              width: { size: 45, type: D.WidthType.PERCENTAGE },
              borders: { top: NONE, left: NONE, right: NONE, bottom: SIG_BDR },
              margins: { bottom: 80, left: 60, right: 60 },
              children: [new D.Paragraph({ children: [] })],
            }),
          ],
        }),
        new D.TableRow({
          children: [
            new D.TableCell({
              width: { size: 45, type: D.WidthType.PERCENTAGE },
              borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
              margins: { top: 80, left: 60, right: 60 },
              children: [new D.Paragraph({ alignment: D.AlignmentType.CENTER, children: [new D.TextRun({ text: name1 || '', size: 18, color: '374151' })] })],
            }),
            new D.TableCell({
              width: { size: 10, type: D.WidthType.PERCENTAGE },
              borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
              children: [new D.Paragraph({ children: [] })],
            }),
            new D.TableCell({
              width: { size: 45, type: D.WidthType.PERCENTAGE },
              borders: { top: NONE, bottom: NONE, left: NONE, right: NONE },
              margins: { top: 80, left: 60, right: 60 },
              children: [new D.Paragraph({ alignment: D.AlignmentType.CENTER, children: [new D.TextRun({ text: name2 || '', size: 18, color: '374151' })] })],
            }),
          ],
        }),
      ],
    }));

    const cityDate = [city, data.doc.date ? formatDateLong(data.doc.date) : ''].filter(Boolean).join(', ');
    if (cityDate) {
      children.push(new D.Paragraph({
        alignment: D.AlignmentType.CENTER,
        spacing: { before: 200 },
        children: [new D.TextRun({ text: cityDate, size: 18, color: '6B7280' })],
      }));
    }
  }

  if (data.settings.showFooter) {
    children.push(new D.Paragraph({ spacing: { before: 480 }, children: [] }));
    children.push(makeWordFooter(D, BLUE));
  }

  const wordDoc = new D.Document({ sections: [{ properties: {}, children }] });
  const blob    = await D.Packer.toBlob(wordDoc);
  downloadBlob(blob, `relatorio-${pdfFilename(data.doc.title)}.docx`);
  showToast('Word gerado com sucesso!', 'success');
}
