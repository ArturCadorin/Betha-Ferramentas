Funcoes.somenteFuncionarios()
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (valorFerias.valor <= 0) {
    if (folha.folhaPagamento) {
        if (funcionario.conselheiroTutelar) {
            suspender"Este cálculo não é realizado para conselheiros tutelares"
        }
        if (!funcionario.possuiPrevidenciaFederal) {
            suspender "Este cálculo é realizado apenas para funcionários contribuintes da previdência federal"
        }
        int dependentes = servidor.getDependenteSalarioFamilia(TipoSalarioFamilia.CELETISTA)
        if (dependentes < 1) {
            suspender "Não há dependentes de salário família para o funcionário ou não há configuração vigente de salário família do tipo 'Celetista' na entidade"
        }
        if (Funcoes.cedidocomonus() > 0 && Bases.valor(Bases.PAGAPROP) == 0) {
            suspender "A matrícula está cedida, com ônus, durante toda a competência"
        }
        double base = Funcoes.remuneracao(matricula.tipo).valor + Bases.valor(Bases.SALAFAM) + Bases.valorCalculado(Bases.SALAFAM, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
        int diastrab = Funcoes.diastrab()
        def classificacoes
        if (Funcoes.diasgozo() == 0 && !TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
            classificacoes = [
                    3 : ClassificacaoTipoAfastamento.ACIDENTE_DE_TRABALHO_PREVIDENCIA,
                    4 : ClassificacaoTipoAfastamento.SERVICO_MILITAR,
                    6 : ClassificacaoTipoAfastamento.AUXILIO_DOENCA_PREVIDENCIA,
                    7 : ClassificacaoTipoAfastamento.LICENCA_SEM_VENCIMENTOS,
                    8 : ClassificacaoTipoAfastamento.DEMITIDO,
                    9 : ClassificacaoTipoAfastamento.APOSENTADO,
                    11: ClassificacaoTipoAfastamento.ACIDENTE_DE_TRAJETO_PREVIDENCIA,
                    12: ClassificacaoTipoAfastamento.DOENCA_DO_TRABALHO_PREVIDENCIA
            ]
            for (classificacao in classificacoes) {
                def i = classificacao.key
                def afastadoPrimeiroDiaCompetencia = false
                def primeiroDiaCompetencia = Funcoes.inicioCompetencia()
                Afastamentos.buscaPorPeriodo(primeiroDiaCompetencia, primeiroDiaCompetencia, classificacoes[i]).each {
                    afastadoPrimeiroDiaCompetencia = true
                }
                Afastamentos.buscaPorPeriodo(classificacoes[i]).each { afast ->
                    def dataAfastamento = afast.inicio
                    if ((dataAfastamento == null) ||
                            ((dataAfastamento != null) & (dataAfastamento <= primeiroDiaCompetencia)) &
                            (afastadoPrimeiroDiaCompetencia)) {
                        suspender "Não é possível calcular o salário família pois a matrícula possui um tipo de afastamento iniciado em competencia anterior ou atual que não é permitido para o cálculo"
                    }
                }
                if (i == 7 && [UnidadePagamento.DIARISTA, UnidadePagamento.HORISTA].contains(Funcoes.remuneracao(matricula.tipo).unidade)) {
                    if (Funcoes.afaslicsvenc() == calculo.quantidadeDiasCompetencia) {
                        suspender "Não é possível calcular o salário família para matrículas com afastamento por 'Licença (SEM vencimentos) - Servidor Público', 'Licença (NÃO remunerada)' ou 'Suspensão de pagamento de servidor público por não recadastramento' durante toda a competência"
                    }
                }
                if (i == 7 && UnidadePagamento.MENSALISTA == Funcoes.remuneracao(matricula.tipo).unidade) {
                    if (Funcoes.afaslicsvenc().equals(30)) {
                        suspender "Não é possível calcular o salário família para matrículas com afastamento por 'Licença (SEM vencimentos) - Servidor Público', 'Licença (NÃO remunerada)' ou 'Suspensão de pagamento de servidor público por não recadastramento' durante toda a competência"
                    }
                }
            }
        } else {
            int cedidosemonus = Funcoes.cedidosemonus()
            classificacoes = [
                    ClassificacaoTipoAfastamento.LICENCA_COM_VENCIMENTOS,
                    ClassificacaoTipoAfastamento.LICENCA_MATERNIDADE,
                    ClassificacaoTipoAfastamento.ABORTO_NAO_CRIMINOSO,
                    ClassificacaoTipoAfastamento.ADOCAO_GUARDA_JUDICIAL_DE_CRIANCA,
                    ClassificacaoTipoAfastamento.PRORROGACAO_DA_LICENCA_MATERNIDADE,
                    ClassificacaoTipoAfastamento.PRORROGACAO_DA_LICENCA_MATERNIDADE_11_770,
                    ClassificacaoTipoAfastamento.PRORROGACAO_DA_LICENCA_MATERNIDADE_15156_2025
            ]
            int diasafast = Funcoes.diasafastcalc30(calculo.competencia, classificacoes)
            int diasferiasant
            if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
                if (!periodoConcessao.diasGozo || periodoConcessao.diasGozo <= 0) {
                    suspender "Este evento deve ser calculado apenas em lançamentos de férias com gozo"
                } else {
                    diasferiasant = Funcoes.diasFeriasAnteriores(periodoConcessao.dataInicioGozo)
                }
            }
            int dias = diastrab + cedidosemonus + diasafast - diasferiasant
            if (dias > 0) {
                suspender "O evento será calculado no processamento mensal pois há dias trabalhados na competência"
            }
        }
        valorReferencia = dependentes
        def vvar = Lancamentos.valor(evento)
        if (vvar > 0) {
            valorCalculado = vvar
        } else {
            double contribuicaoSalarioFamilia = EncargosSociais.SalarioFamilia.Celetista.buscaContribuicao(base.trunc(2), 2)
            if (contribuicaoSalarioFamilia <= 0) {
                suspender 'Não há valor de salário família a ser pago para esta matrícula pois a base de cálculo ultrapassa o limite máximo estabelecido para concessão do benefício'
            }
            double valorTotal = contribuicaoSalarioFamilia * valorReferencia
            def dataAdmissao = Funcoes.dadosMatricula().dataInicio
            def dataRescisao = Funcoes.dtrescisao()
            boolean mesAdmissaoDemissao = (Funcoes.inicioCompetencia() < dataAdmissao)
            if (!mesAdmissaoDemissao && dataRescisao != null) {
                if (Datas.ano(dataRescisao) == Datas.ano(calculo.competencia) && Datas.mes(dataRescisao) == Datas.mes(calculo.competencia) && calculo.quantidadeDiasCompetencia > Datas.dia(dataRescisao)) {
                    mesAdmissaoDemissao = true
                }
            }
            int diaTrab
            if (mesAdmissaoDemissao) {
                diaTrab = diastrab
                valorTotal *= diastrab
            }
            if (diaTrab != 0) {
                if (UnidadePagamento.HORISTA.equals(funcionario.unidadePagamento) || UnidadePagamento.DIARISTA.equals(funcionario.unidadePagamento)) {
                    valorTotal /= calculo.quantidadeDiasCompetencia
                } else {
                    valorTotal /= 30
                }
            }
            if (matricula.possuiMultiploVinculo) {
                double valorCalculadoMultiplosVinculos = Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMCEL, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
                    valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMCEL, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
                }
                if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                    valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMCEL, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
                    valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMCEL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                    valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMCEL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                }
                if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
                    valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMCEL, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
                    valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMCEL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
                    valorCalculadoMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.SALFAMCEL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
                }
                valorTotal -= valorCalculadoMultiplosVinculos
                if (valorTotal <= 0) {
                    suspender 'Não há valor de salário família a ser pago para esta matrícula pois o valor a ser recebido já foi integralmente creditado para outra matrícula deste servidor'
                }
            }
            valorCalculado = valorTotal
        }
    }
}

