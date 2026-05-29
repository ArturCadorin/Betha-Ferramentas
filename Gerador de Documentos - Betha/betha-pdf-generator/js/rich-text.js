/* ──────────────────────────────────────────
   rich-text.js — parsing e renderização de texto rico
   ────────────────────────────────────────── */

// Parses innerHTML of a contenteditable div into segments [{text, bold, italic}]
function parseRichHTMLSegments(html) {
  if (!html) return [{ text: '', bold: false, italic: false }];
  // Normalize: replace <br> with newline marker
  const normalized = html
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<div>/gi, '\n')
    .replace(/<\/div>/gi, '');
  const segments = [];
  const parser = new DOMParser();
  const frag = parser.parseFromString(`<body>${normalized}</body>`, 'text/html').body;

  function walk(node, bold, italic) {
    if (node.nodeType === Node.TEXT_NODE) {
      const text = node.textContent;
      if (text) segments.push({ text, bold, italic });
      return;
    }
    const tag = node.nodeName.toUpperCase();
    const b = bold  || tag === 'B' || tag === 'STRONG';
    const i = italic || tag === 'I' || tag === 'EM';
    node.childNodes.forEach(child => walk(child, b, i));
  }
  frag.childNodes.forEach(n => walk(n, false, false));
  if (!segments.length) segments.push({ text: '', bold: false, italic: false });
  return segments;
}

// Returns plain text from rich HTML (strip tags)
function richHTMLToPlain(html) {
  return parseRichHTMLSegments(html).map(s => s.text).join('');
}

// Wraps mixed-format segments into lines that fit maxW at given font size (jsPDF)
function buildRichLines(doc, segments, maxW, size) {
  const lines = [];
  let curLine = [];
  let curW = 0;

  function flushLine() {
    lines.push(curLine);
    curLine = [];
    curW = 0;
  }

  function segWidth(text, bold, italic) {
    const style = bold && italic ? 'bolditalic' : bold ? 'bold' : italic ? 'italic' : 'normal';
    doc.setFont('helvetica', style);
    doc.setFontSize(size);
    return doc.getTextWidth(text);
  }

  for (const seg of segments) {
    const parts = seg.text.split('\n');
    for (let pi = 0; pi < parts.length; pi++) {
      if (pi > 0) flushLine();
      const words = parts[pi].split(' ');
      for (let wi = 0; wi < words.length; wi++) {
        const word = (wi > 0 ? ' ' : '') + words[wi];
        const ww = segWidth(word, seg.bold, seg.italic);
        if (curW + ww > maxW && curLine.length > 0) {
          flushLine();
          const trimWord = words[wi];
          const tw = segWidth(trimWord, seg.bold, seg.italic);
          curLine.push({ text: trimWord, bold: seg.bold, italic: seg.italic });
          curW = tw;
        } else {
          curLine.push({ text: word, bold: seg.bold, italic: seg.italic });
          curW += ww;
        }
      }
    }
  }
  if (curLine.length > 0) lines.push(curLine);
  return lines;
}

// Draws rich text lines onto jsPDF canvas, returns final Y
function drawRichLines(doc, lines, x, y, size) {
  const lh = size * 0.3528 * 1.4;
  for (const line of lines) {
    let cx = x;
    for (const seg of line) {
      const style = seg.bold && seg.italic ? 'bolditalic' : seg.bold ? 'bold' : seg.italic ? 'italic' : 'normal';
      doc.setFont('helvetica', style);
      doc.setFontSize(size);
      doc.text(seg.text, cx, y);
      cx += doc.getTextWidth(seg.text);
    }
    y += lh;
  }
  return y;
}

// Converts innerHTML of a contenteditable div to an array of docx TextRun objects
function htmlToWordRuns(D, html, opts = {}) {
  const segments = parseRichHTMLSegments(html);
  return segments.map(seg => new D.TextRun({
    text: seg.text,
    bold: seg.bold || opts.bold || false,
    italics: seg.italic || opts.italics || false,
    size: opts.size || 21,
    color: opts.color || '1A1D2E',
  }));
}

// Floating rich-text toolbar
function initRichText() {
  // Create floating toolbar
  const toolbar = document.createElement('div');
  toolbar.className = 'rte-float';
  toolbar.id = 'rte-float';
  toolbar.innerHTML = `
    <button class="rte-float-btn" data-cmd="bold"    title="Negrito (Ctrl+B)"><b>B</b></button>
    <div class="rte-float-sep"></div>
    <button class="rte-float-btn" data-cmd="italic"  title="Itálico (Ctrl+I)"><i>I</i></button>
  `;
  document.body.appendChild(toolbar);

  toolbar.addEventListener('mousedown', (e) => {
    e.preventDefault(); // don't lose selection
    const btn = e.target.closest('.rte-float-btn');
    if (!btn) return;
    document.execCommand(btn.dataset.cmd, false, null);
    updateToolbarState();
  });

  // Show/hide toolbar based on selection within a .rich-textarea
  document.addEventListener('selectionchange', () => {
    const sel = window.getSelection();
    if (!sel || sel.isCollapsed) {
      toolbar.classList.remove('show');
      return;
    }
    // Check if selection is inside a rich-textarea
    const anchor = sel.anchorNode;
    if (!anchor) { toolbar.classList.remove('show'); return; }
    const el = anchor.nodeType === Node.TEXT_NODE ? anchor.parentElement : anchor;
    if (!el.closest('.rich-textarea')) { toolbar.classList.remove('show'); return; }

    // Position toolbar above the selection
    const range = sel.getRangeAt(0);
    const rect = range.getBoundingClientRect();
    toolbar.classList.add('show');
    toolbar.style.top  = (rect.top  + window.scrollY - 46) + 'px';
    toolbar.style.left = (rect.left + window.scrollX + rect.width / 2 - 42) + 'px';
    updateToolbarState();
  });

  function updateToolbarState() {
    toolbar.querySelectorAll('.rte-float-btn').forEach(btn => {
      btn.classList.toggle('active', document.queryCommandState(btn.dataset.cmd));
    });
  }

  // Keyboard shortcuts in all rich-textareas
  document.addEventListener('keydown', (e) => {
    const el = document.activeElement;
    if (!el || !el.classList.contains('rich-textarea')) return;
    if ((e.ctrlKey || e.metaKey) && e.key === 'b') { e.preventDefault(); document.execCommand('bold',   false, null); }
    if ((e.ctrlKey || e.metaKey) && e.key === 'i') { e.preventDefault(); document.execCommand('italic', false, null); }
    if (e.key === 'Enter' && 'singleLine' in el.dataset) { e.preventDefault(); }
  });
}
