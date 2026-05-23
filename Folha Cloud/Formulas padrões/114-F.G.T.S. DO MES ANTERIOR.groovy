Funcoes.somenteFuncionarios()
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'O funcionário não é optante de FGTS'
}
if (!TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos rescisórios"
}
if (calculo.fgtsMesAnterior) {
    suspender "Conforme parâmetro do cálculo da rescisão, o valor do depósito do FGTS referente ao mês anterior já foi efetuado"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    def cptAnterior = Datas.removeMeses(Funcoes.inicioCompetencia(), 1)
    double fgtsCptAnterior = Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL, cptAnterior) +
            Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO, cptAnterior) +
            Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, cptAnterior) +
            Eventos.valorCalculado(ClassificacaoEvento.FGTS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, cptAnterior) +
            Eventos.valorCalculado(ClassificacaoEvento.FGTS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, cptAnterior)
    if (fgtsCptAnterior > 0) {
        valorCalculado = fgtsCptAnterior
    }
}

