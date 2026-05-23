Funcoes.somenteFuncionarios()
if (!TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento é calculado apenas em processamentos rescisórios'
}
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'O funcionário não é optante de FGTS'
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'O funcionário não tem direito a receber décimo terceiro'
}
if (funcionario.categoriaSefipVinculo.toString() == 'MENOR_APRENDIZ') {
    valorReferencia = 2
} else {
    if (evento.taxa <= 0) {
        suspender 'Para calcular este evento é necessário definir na configuração do mesmo uma taxa para o cálculo'
    }
    valorReferencia = evento.taxa
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    double base = Bases.valor(Bases.FGTS13)
    double fgtsAux
    if (calculo.recolherFgtsPelaSefip && !calculo.recalcularMensal) {
        double baseFgtsMensal = Bases.valorCalculado(Bases.FGTS, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL) +
                Bases.valorCalculado(Bases.FGTS, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
        if (baseFgtsMensal > 0) {
            base += baseFgtsMensal
            fgtsAux += Eventos.valorCalculado(ClassificacaoEvento.FGTS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                    Eventos.valorCalculado(ClassificacaoEvento.FGTS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
        }
    }
    vaux = base * valorReferencia / 100
    vaux -= fgtsAux
    valorCalculado = Numeros.trunca(vaux,2)
}

