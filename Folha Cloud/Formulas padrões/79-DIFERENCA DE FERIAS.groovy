Funcoes.somenteFuncionarios()
if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        if (Funcoes.altsalfer() != 0) {
            suspender 'Não há alteração salarial em férias na competência'
        }
        valorCalculado = vvar
        valorReferencia = vvar
        Bases.compor(valorCalculado,
                Bases.FGTS,
                Bases.IRRF,
                Bases.INSS,
                Bases.PREVEST,
                Bases.FUNDASS,
                Bases.FUNDOPREV,
                Bases.MEDIAUXMAT,
                Bases.FUNDFIN)
    }
}

