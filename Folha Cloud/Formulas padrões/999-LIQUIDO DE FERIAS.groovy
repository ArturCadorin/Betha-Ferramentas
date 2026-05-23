double liquidoFerias
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    liquidoFerias = vvar
} else {
    liquidoFerias = Funcoes.getTotalFerias().liquido - Eventos.valor(900) + Eventos.valor(901)
}
if (liquidoFerias > 0) {
    valorCalculado = liquidoFerias
    evento.replicado(true)
}

