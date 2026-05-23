Funcoes.somenteFuncionarios()
if (!TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos de férias"
}
if (funcionario.possuiPrevidenciaFederal) {
    if (folha.folhaPagamento) {
        def vaux = Lancamentos.valor(evento)
        if (vaux >= 0) {
            valorReferencia = vaux
            valorCalculado = vaux
        } else {
            double baseOutrosVinculos
            double inssOutrosVinculos
            if (funcionario.possuiMultiploVinculo) {
                baseOutrosVinculos = Funcoes.getValorBaseMultiplosVinculos(Bases.INSSFER, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
                inssOutrosVinculos = Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            }
            double baseInssFerias = Bases.valor(Bases.INSSFER) + baseOutrosVinculos + Bases.valor(Bases.INSSOUTRA) + Bases.valorCalculado(Bases.INSSFER, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL) + Bases.valorCalculado(Bases.INSSOUTRA, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            if (baseInssFerias <= 0) {
                suspender "Não há valor de base de INSS de férias ou INSS de outras empresas para cálculo"
            }
            double tetoInss = EncargosSociais.RGPS.buscaMaior(1)
            if (baseInssFerias > tetoInss) {
                double excedenteInss = baseInssFerias - tetoInss
                Bases.compor(excedenteInss, Bases.EXCEINSSFER)
                baseInssFerias = tetoInss
            }
            double calculaContribuicao = Numeros.trunca(baseInssFerias, 2)
            if (funcionario.conselheiroTutelar) {
                valorReferencia = 11
            } else {
                valorReferencia = EncargosSociais.RGPS.buscaContribuicao(calculaContribuicao, 2)
            }
            double valorDescontadoInssFerias = Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            double valorRetencaoInss = Eventos.valor(169) + Eventos.valorCalculado(169, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            if (Funcoes.inicioCompetencia() >= Datas.data(2020, 3, 1) && !funcionario.conselheiroTutelar) {
                vaux = Funcoes.calculoProgressivoINSS(baseInssFerias)
                valorCalculado = Numeros.arredonda(vaux, 2) - inssOutrosVinculos - valorDescontadoInssFerias - valorRetencaoInss
            } else {
                vaux = (baseInssFerias * valorReferencia) / 100
                valorCalculado = Numeros.trunca(vaux, 2) - inssOutrosVinculos - valorDescontadoInssFerias - valorRetencaoInss
            }
        }
        Bases.compor(valorCalculado, Bases.DEDUCIRRFMES, Bases.ABATIRRF, Bases.MARGECONSIG)
    }
}

