double base = Bases.valor(Bases.SALBASE)
if (base <= 0) {
    suspender 'Não há valor de base de Salário Base informado para o cálculo'
}
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorReferencia = (vvar * 100) / base
    valorCalculado = vvar
} else {
    valorReferencia = evento.taxa
    valorCalculado = base * valorReferencia / 100
}
Bases.compor(valorCalculado, Bases.IRRF)

