Funcoes.somenteFuncionarios()
Datas.configurar([usarCorrecaoCalculo: true])

if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento não deve ser calculado em processamentos de férias"
}

int forma = 1 //1-Normal , 2-Composto

boolean buscouNoRH
int mesesAfastados
int qntAnosAdicional

def dtbase = funcionario.dataBase
if (dtbase == null) {
    dtbase = funcionario.dataAdmissao
}

def vvar = Lancamentos.valor(evento)
if (vvar < 0) {
    qntAnosAdicional = AdicionaisTempoServico.busca(TipoAdicional.QUANTIDADE_ADICIONAIS, ClassificacaoAdicional.QUADRIENIO)
    buscouNoRH = true
} else {
    def dfin = Datas.data(Datas.ano(calculo.competencia), Datas.mes(calculo.competencia), calculo.quantidadeDiasCompetencia)

    def afastamentos = [ClassificacaoTipoAfastamento.AUXILIO_DOENCA_PREVIDENCIA, ClassificacaoTipoAfastamento.LICENCA_SEM_VENCIMENTOS]

    // Inicia o período de calamidade
    def cptInicial = dtbase
    def cptFinal = calculo.competencia

    if (Datas.formatar(cptInicial, "yyyyMM").toInteger() < 202005) {
        cptInicial = Datas.data(2020,5,1)
    }
    if (Datas.formatar(cptFinal, "yyyyMM").toInteger() > 202112) {
        cptFinal = Datas.data(2021,12,31)
    }

    // O período de calamidade deve desconsiderar os afastamentos existentes dentro do período
    if (cptFinal >= cptInicial) {
        // Para profissionais de saúde e segurança pública, deve desconsiderar a contagem do período de calamidade apenas dentro do período, depois deve ser reposto
        if (!funcionario.profissionalSaudeSegurancaPublica || calculo.competencia <= cptFinal) {
            mesesAfastados = Datas.diferencaMeses(cptInicial, cptFinal) + 1
        }

        cptInicial = Datas.removeMeses(cptInicial, 1)
        cptInicial = Datas.data(Datas.ano(cptInicial), Datas.mes(cptInicial), calculo.quantidadeDias(Datas.mes(cptInicial), Datas.ano(cptInicial)))

        cptFinal = Datas.adicionaMeses(cptFinal, 1)
        cptFinal = Datas.data(Datas.ano(cptFinal), Datas.mes(cptFinal), 1)

        // Afastamentos anteriores ao período de calamidade
        mesesAfastados += Funcoes.mesesafast(dtbase, cptInicial, afastamentos)
        // Afastamentos poteriores ao período de calamidade
        mesesAfastados += Funcoes.mesesafast(cptFinal, dfin, afastamentos)
    } else {
        mesesAfastados = Funcoes.mesesafast(dtbase, dfin, afastamentos)
    }
    // Finaliza o período de calamidade

    if (mesesAfastados > 0) {
        def cptproc = Datas.data(Datas.ano(calculo.competencia), Datas.mes(calculo.competencia), 1)
        dfin = Datas.removeMeses(cptproc, mesesAfastados)
        int dia = calculo.quantidadeDias(Datas.mes(dfin), Datas.ano(dfin))
        if (Datas.mes(dfin).equals(2)) {
            if (Datas.ano(dfin)) {
                if (Numeros.resto(Datas.ano(dfin), 4) == 0) {
                    dia = 29
                } else {
                    dia = 28
                }
            }
        }
        dfin = Datas.data(Datas.ano(dfin), Datas.mes(dfin), dia)
    }
    qntAnosAdicional = Datas.diferencaAnos(dtbase, dfin, true) / 4
    qntAnosAdicional = Numeros.trunca(qntAnosAdicional, 0)
}

if (qntAnosAdicional < 1) {
    suspender 'O funcionário ainda não adquiriu o adicional'
}

if (vvar > 0) {
    valorReferencia = vvar
} else {
    if (buscouNoRH) {
        valorReferencia = AdicionaisTempoServico.busca(TipoAdicional.PERCENTUAL_TEMPO_SERVICO, ClassificacaoAdicional.QUADRIENIO)
    } else {
        def taxa = evento.taxa
        def valorPercentual
        if (forma == 1 || qntAnosAdicional == 1) {
            valorPercentual = qntAnosAdicional * taxa
        } else {
            taxa /= 100
            valorPercentual = 1 + taxa
            valorPercentual = ((valorPercentual ** qntAnosAdicional) - 1) / taxa
            valorPercentual *= taxa * 100
        }
        if (valorPercentual > 35) {
            valorPercentual = 35 //Vai parar de gerar em 35%
        }
        valorReferencia = valorPercentual
    }
}

double valBasePropCalc
double valorProporcional = (funcionario.salario * valorReferencia) / 100
if (valorProporcional > 0) {
    double valBaseFgts
    if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
        valorProporcional *= Funcoes.avos13(12) / 12
        if (SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
            valorProporcional *= evento.getTaxa(26) / 100
        }

        valBasePropCalc = valorProporcional
        valorCalculado = valBasePropCalc
        valBaseFgts = valorProporcional
    } else {
        double baseCalculo
        if (Eventos.valor(195) > 0) {
            baseCalculo = Bases.valor(Bases.PAGAPROP) - Bases.valor(Bases.MEDAUXMATPR)
        } else {
            baseCalculo = Bases.valor(Bases.PAGAPROP)
        }
        if (baseCalculo > 0) {
            Bases.compor(valorProporcional, Bases.HORAEXTRA, Bases.SALAFAM)
        }
        valBasePropCalc = Funcoes.calcprop(valorProporcional, baseCalculo)
        valorCalculado = valBasePropCalc

        int diasDirIntFGTS = Funcoes.afasacidtrab() + Funcoes.afasservmil()
        baseCalculo += Funcoes.cnvdpbase(diasDirIntFGTS)
        valBaseFgts = Funcoes.calcprop(valorProporcional, baseCalculo)
    }

    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.PERIC,
            Bases.IRRF,
            Bases.INSS,
            Bases.MEDIAUXMAT,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.FUNDFIN)

    Bases.compor(valBaseFgts, Bases.FGTS)
    double valEsocialFgts = valBaseFgts - valBasePropCalc
    Bases.compor(valEsocialFgts, Bases.FGTSAFASES)
}
