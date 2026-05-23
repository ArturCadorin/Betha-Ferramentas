int dependentesIrrf = servidor.dependentesIrrf
if (dependentesIrrf == 0) {
    suspender 'Não há dependentes de imposto de renda configurados para o servidor ou os mesmos já ultrapassaram os critérios válidos para efetuar o desconto por dependente'
}
if (calculo.rra && TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado na folha de pagamento anterior com RRA'
}
if (!calculo.rra && TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado na folha de pagamento anterior. O valor deve ser lançado diretamente em cada folha complementar vinculada ao pagamento anterior selecionado'
}
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorCalculado = vaux
} else {
    def valorAnterior
    double valor13 = Eventos.valor(25) + Eventos.valor(28) + Eventos.valor(29) + Eventos.valor(30) + Eventos.valor(300) + Eventos.valor(301) + Eventos.valor(302) + Eventos.valor(303) + Eventos.valor(304)
    if (valor13 <= 0 && (Bases.valor(Bases.PAGAPROP) <= 0 && !TipoMatricula.AUTONOMO.equals(matricula.tipo)) || (Bases.valor(Bases.SALBASE) <= 0 && TipoMatricula.AUTONOMO.equals(matricula.tipo))) {
        suspender 'O desconto por dependente não será calculado para a matrícula nesta folha. Este evento será calculado quando houverem bases de cálculo válidas, ou eventos de 13º salário lançados'
    }
    if (matricula.possuiMultiploVinculo){
        def deducaoOutrosVinculos = Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
            deducaoOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
        }
        if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
            deducaoOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
            deducaoOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
        }
        if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
            deducaoOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
            deducaoOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
        }
        if (deducaoOutrosVinculos > 0) {
            suspender 'O desconto por dependente já foi calculado em outra matrícula desta mesma pessoa'
        }
    }
    //Na complementar de RRA não é calculado desconto por dependente e com isso precisamos zerar as diferenças vindas de processamentos anteriores, por isso abaixo está sendo somado o valor calculado dos eventos com classificacao DESCDEP
    if(TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && calculo.rra){
        valorAnterior = Eventos.valorCalculado(ClassificacaoEvento.DESCDEP, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                Eventos.valorCalculado(ClassificacaoEvento.DESCDEP, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
    }
    if(TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && calculo.rra){
        valorAnterior = Eventos.valorCalculado(ClassificacaoEvento.DESCDEP, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                Eventos.valorCalculado(ClassificacaoEvento.DESCDEP, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
    }
    valorReferencia = dependentesIrrf
    if(valorAnterior > 0){
        valorCalculado = valorAnterior
    } else {
        valorCalculado = dependentesIrrf * EncargosSociais.IRRF.deducaoPorDependente
    }
}
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    Bases.compor(valorCalculado, Bases.DEDUCIRRFMES)
} else if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
    Bases.compor(valorCalculado, Bases.DEDUCIRRF13)
} else {
    Bases.compor(valorCalculado, Bases.DEDUCIRRFMES, Bases.DEDUCIRRF13)
}
valorCalculado -= Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

