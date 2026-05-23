Funcoes.somenteFuncionarios()
valorReferencia = 1 //1 dia trabalhado
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorCalculado = vvar
} else {
    if (funcionario.sindicato == null) {
        suspender "Não há sindicato informado para o funcionário"
    }
    if (funcionario.mesContribuicaoSindical == null) {
        suspender "O mês da contribuição sindical não foi informado no sindicato informado para o funcionário. Acesse a configuração da empresa e informe uma data de contribuição"
    }
    def mescontrib = Funcoes.buscaMes(funcionario.mesContribuicaoSindical)
    if (Datas.mes(calculo.competencia).equals(mescontrib)) {
        if (Funcoes.cedidocomonus() > 0 && Bases.valor(Bases.PAGAPROP) == 0) {
            suspender "O funcionário está cedido, com ônus, durante toda a competência"
        }
        def diasTrabOuAfastMes = Funcoes.diastrab() + Funcoes.afasaborto() + Funcoes.afasadocao() + Funcoes.afasacidtrab() +
                Funcoes.afasacidtrabemp() + Funcoes.afasauxdoenc() + Funcoes.afasauxdoencemp() + Funcoes.afasdirinteg() +
                Funcoes.afaslicmat() + Funcoes.afaslicsvenc() + Funcoes.afasservmil() + Funcoes.afasprorroglicmatlei11770() +
                Funcoes.afasprorroglicmatlei15156()
        if (diasTrabOuAfastMes <= 0 && TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
            suspender "Não há dias trabalhados ou em afastamento com classificações válidas para o cálculo da contribuição sindical do funcionário na competência"
        }
        if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
            if (diasTrabOuAfastMes > 0) {
                suspender "O evento deve ser calculado no processamento mensal pois há dias trabalhados ou em afastamento com classificações válidas para o cálculo da contribuição sindical na competência"
            }
            if (Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL) > 0) {
                suspender "O evento já foi calculado em processamento de férias anterior"
            }
        }
        valorCalculado = funcionario.salario + Bases.valor(Bases.CONTSIND)
        if (UnidadePagamento.DIARISTA.equals(funcionario.unidadePagamento) ||
            UnidadePagamento.HORISTA.equals(funcionario.unidadePagamento)) {
            valorCalculado /= calculo.quantidadeDiasCompetencia
        } else {
            valorCalculado /= 30
        }
    }
}

