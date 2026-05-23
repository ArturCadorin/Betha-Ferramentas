if (!TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
    suspender "Este evento é calculado apenas em processamentos mensais"
}
if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para aposentados e pensionistas"
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender "A matrícula não tem direito a receber décimo terceiro"
}
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorCalculado = vvar
    Bases.compor(valorCalculado,
            Bases.IRRF13,
            Bases.INSS13,
            Bases.FGTS13,
            Bases.PREVEST13,
            Bases.FUNDASS13,
            Bases.FUNDPREV13,
            Bases.FUNDFIN13)
}

