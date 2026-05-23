Funcoes.somenteFuncionarios()
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'O funcionário não é optante de FGTS'
}
if (!TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos rescisórios"
}
def percentual = evento.taxa
if (percentual <= 0) {
    suspender 'Para calcular este evento é necessário definir na configuração do mesmo uma taxa para o cálculo'
}
if (calculo.motivoRescisao.classificacao.equals('ACORDO_ENTRE_PARTES')) {
    //Rescisão por acordo: conforme legislação, o valor da referência deve ser dividido por 2
    valorReferencia = percentual / 2
} else {
    valorReferencia = percentual
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    double valorSaldoFgts = calculo.saldoFgts
    double valorFgtsRescisorio = valorSaldoFgts + Eventos.valor(36) + Eventos.valor(37) + Eventos.valor(113) + Eventos.valor(114) + Eventos.valor(284) - Eventos.valor(251) - Eventos.valor(262)
    if (calculo.recolherFgtsPelaSefip) {
        valorFgtsRescisorio += Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
            Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO, calculo.competencia)
    }
    valorCalculado = valorFgtsRescisorio * valorReferencia / 100
}

