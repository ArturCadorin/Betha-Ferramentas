valorReferencia = Lancamentos.valor(evento)
valorCalculado = Funcoes.calcexclusivo(valorReferencia, TipoProcessamento.MENSAL, Funcoes.diastrab(), TipoProcessamento.FERIAS, Funcoes.diasferias())

