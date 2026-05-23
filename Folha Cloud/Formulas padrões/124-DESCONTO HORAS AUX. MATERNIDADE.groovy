Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidenciaFederal) {
    int diasafast = Funcoes.afasadocao()
    if (servidor.sexo.equals(Sexo.MASCULINO) || (calculo.competencia > Datas.data(1999, 11, 1) && calculo.competencia <= Datas.data(2003, 8, 1))) {
        diasafast += Funcoes.afaslicmat()
        if (diasafast <= 0) {
            suspender "Não há afastamentos com as classificações 'Licença maternidade - Adoção/guarda judicial de criança' ou 'Licença maternidade - 120 dias' na competência"
        }
    } else {
        if (diasafast <= 0) {
            suspender "Não há afastamentos com as classificações 'Licença maternidade - Adoção/guarda judicial de criança' na competência"
        }
    }
    def dias
    def diasMes
    boolean horistaOuDiarista = [UnidadePagamento.HORISTA,UnidadePagamento.DIARISTA].contains(funcionario.unidadePagamento)
    if (horistaOuDiarista) {
        diasMes = calculo.quantidadeDiasCompetencia
    } else {
        diasMes = 30
    }
    double baseConsolidada = Bases.valor(Bases.INSS) - Eventos.valor(40)
    if (calculo.dataRescisao) {
        def dataAdmissao = funcionario.dataAdmissao
        dias = Datas.dia(calculo.dataRescisao)
        if (dataAdmissao >= Funcoes.inicioCompetencia()) {
            dias = dias - Datas.dia(dataAdmissao) + 1
        }
        baseConsolidada = (baseConsolidada * diasMes) / dias
        baseConsolidada = Numeros.arredonda(baseConsolidada, 2)
        valorCalculado = Funcoes.calcprop(baseConsolidada, diasafast)
    } else {
        valorReferencia = Funcoes.cnvdpbase(diasafast)
        if (diasafast == 30) {
            valorCalculado = baseConsolidada
        } else {
            dias = Funcoes.diastrab() + diasafast
            if (dias < 30 || (horistaOuDiarista && dias < calculo.quantidadeDiasCompetencia)) {
                baseConsolidada = (baseConsolidada * diasMes) / Bases.valor(Bases.PAGAPROP)
                baseConsolidada = Numeros.arredonda(baseConsolidada, 2)
            }
            valorCalculado = Funcoes.calcprop(baseConsolidada, diasafast)
        }
    }
    Bases.compor(valorCalculado, Bases.DEDUCIRRFMES, Bases.EXCEINSS)
}

