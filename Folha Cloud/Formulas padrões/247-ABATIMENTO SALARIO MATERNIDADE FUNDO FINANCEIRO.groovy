Funcoes.somenteFuncionarios()
if (!servidor.sexo.equals(Sexo.FEMININO)) {
    suspender "Este cálculo é realizado apenas para funcionários do sexo feminino"
}
if (!funcionario.possuiPrevidencia(TipoPrevidencia.FUNDO_FINANCEIRO)) {
    suspender "Este cálculo é realizado apenas para funcionários contribuintes do fundo financeiro"
}
def datainicial = Datas.data(Datas.ano(calculo.competencia), Datas.mes(calculo.competencia), 1)
def datafinal = Datas.data(Datas.ano(calculo.competencia), Datas.mes(calculo.competencia), calculo.quantidadeDiasCompetencia)
def classificacoes = [ClassificacaoTipoAfastamento.LICENCA_MATERNIDADE,
                      ClassificacaoTipoAfastamento.PRORROGACAO_DA_LICENCA_MATERNIDADE,
                      ClassificacaoTipoAfastamento.PRORROGACAO_DA_LICENCA_MATERNIDADE_15156_2025,
                      ClassificacaoTipoAfastamento.ABORTO_NAO_CRIMINOSO]
def dias = Funcoes.diasafast(datainicial, datafinal, classificacoes)
if (dias <= 0) {
    suspender "Não há afastamento com as classificações 'Licença maternidade', 'Licença maternidade - Antecipação ou prorrogação', 'Licença maternidade - Prorrogação 60 dias, Lei 15.156/2025 - deficiência associada ao Zica vírus' ou 'Licença maternidade - Aborto não criminoso' na competência"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    def vsmataux = 0
    def ultdia = datafinal
    def diaaf = Funcoes.diasafast(ultdia, ultdia, classificacoes)
    if (diaaf == 0 && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
        suspender "Não há afastamento com as classificações 'Licença maternidade', 'Licença maternidade - Antecipação ou prorrogação', 'Licença maternidade - Prorrogação 60 dias, Lei 15.156/2025 - deficiência associada ao Zica vírus' ou 'Licença maternidade - Aborto não criminoso' a serem calculados na subtipo de processamento complementar"
    }
    def base = (Bases.valor(Bases.FUNDFIN) + Eventos.valor(40)) -
               (Eventos.valor(22) +
                Eventos.valor(23) +
                Eventos.valor(24) +
                Eventos.valor(178) +
                Eventos.valor(179) +
                Eventos.valor(180))
    def diasLicencaMaternidade = Funcoes.afaslicmat() + Funcoes.afasprorroglicmat() + Funcoes.afasprorroglicmatlei15156() + Funcoes.afasaborto()
    def diasHorasLicMat = Funcoes.cnvdpbase(diasLicencaMaternidade)
    def pagaProporcional = Bases.valor(Bases.PAGAPROP)
    if (base == funcionario.salario) {
        vsmataux = funcionario.salario
    } else {
        def baseaux
        def hrsmesaux
        if (UnidadePagamento.DIARISTA.equals(funcionario.unidadePagamento)) {
            baseaux = (funcionario.quantidadeHorasMes / 30) * pagaProporcional
            baseaux = Numeros.arredonda(baseaux, 2)
            hrsmesaux = Numeros.arredonda(funcionario.quantidadeHorasMes, 2)
        } else {
            baseaux = pagaProporcional
            hrsmesaux = funcionario.quantidadeHorasMes
        }
        if (baseaux != hrsmesaux) {
            vsmataux = Eventos.valor(3) + Eventos.valor(161) + Eventos.valor(162)
        }
    }
    if ((vsmataux != funcionario.salario) && (Eventos.valor(195) == 0)) {
        vsmataux = Eventos.valor(3) + Eventos.valor(161) + Eventos.valor(162)
        if (dias < calculo.quantidadeDiasCompetencia) {
            def diafin = pagaProporcional
            if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                int diaRescisao = calculo.dataRescisao.dia
                if (Funcoes.cnvdpbase(diaRescisao) < diafin) {
                    diafin = Funcoes.cnvdpbase(diaRescisao)
                }
            } else {
                if (UnidadePagamento.HORISTA.equals(funcionario.unidadePagamento) || UnidadePagamento.DIARISTA.equals(funcionario.unidadePagamento)) {
                    diafin = diafin + (funcionario.quantidadeHorasMes / 30)
                }
            }
            base = base - Bases.valor(Bases.COMPHORAMES)
            base = ((base * diasHorasLicMat) / diafin) + vsmataux
        }
        valorCalculado = base
    } else {
        valorCalculado = Eventos.valor(3) + Eventos.valor(161) + Eventos.valor(162) + Eventos.valor(195) + Eventos.valor(319)
    }
}

