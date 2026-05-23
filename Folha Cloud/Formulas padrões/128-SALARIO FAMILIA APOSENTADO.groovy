int tipo = 1  //1 - Normal, 2 - Por faixa
if (!TipoMatricula.APOSENTADO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para aposentados"
}
int dependentes = servidor.getDependenteSalarioFamilia(TipoSalarioFamilia.APOSENTADO)
if (dependentes < 1) {
    suspender "Não há dependentes de salário família para o aposentado ou não há configuração vigente de salário família do tipo 'Aposentado' na entidade"
}
def afastamentos = Eventos.valorReferencia(4) + Eventos.valorReferencia(5) + Eventos.valorReferencia(7) + Eventos.valorReferencia(8)
if (afastamentos > 0 || Bases.valor(Bases.PAGAPROP) == 0) {
    suspender "Não é possível calcular o salário família aposentado para aposentados sem valor de base 'Paga proporcional' ou com afastamento por 'Auxílio doença previdência', 'Doença do trabalho previdência', 'Acidente de trabalho previdência', 'Licença (SEM vencimentos) - Servidor Público' ou 'Serviço militar' na competência"
}
valorReferencia = dependentes
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
} else {
    double valorTotal
    if (tipo == 1) {
        valorTotal = dependentes * EncargosSociais.SalarioFamilia.Aposentado.buscaMaior(2)
    } else {
        valorReferencia = EncargosSociais.SalarioFamilia.Aposentado.buscaContribuicao(funcionario.salario, 2)
        valorTotal = dependentes * valorReferencia
    }
    if (matricula.possuiMultiploVinculo) {
        double valorCalculadoMultiplosVinculos = Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMAPOS, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
            valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMAPOS, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
        }
        if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
            valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMAPOS, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMAPOS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
            valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMAPOS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
        }
        if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
            valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMAPOS, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMAPOS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
            valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMAPOS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
        }
        valorTotal -= valorCalculadoMultiplosVinculos
        if (valorTotal <= 0) {
            suspender 'Não há valor de salário família a ser pago para esta matrícula pois o valor a ser recebido já foi integralmente creditado para outra matrícula deste servidor'
        }
    }
    valorCalculado = valorTotal
}

