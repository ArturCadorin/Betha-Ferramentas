boolean recebeDecimoTerceiro = Funcoes.recebeDecimoTerceiro()
if (!recebeDecimoTerceiro) {
    suspender "A matrícula não tem direito a receber décimo terceiro"
}
if (TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para estagiários"
}
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorCalculado = vaux
    valorReferencia = vaux
    Bases.compor(valorCalculado,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.MEDIAUXMAT,
            Bases.FUNDFIN)
}

