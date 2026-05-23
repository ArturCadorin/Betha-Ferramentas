if (TipoMatricula.AUTONOMO.equals(matricula.tipo) || TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para autônomos e estagiários"
}
if (Funcoes.possuiPrevidencia(TipoPrevidencia.ASSISTENCIA_MUNICIPAL)) {
    def baseprev = Bases.valor(Bases.FUNDASS)
    if (baseprev <= 0) {
        suspender 'Não há valor de base de assistência municipal para cálculo'
    }
    int forma = 1 //1-Aplicar %, 2-Usar tabela assistência municipal
    def vaux
    def taxa
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorReferencia = vvar
        valorCalculado = vvar
    } else {
        if (forma == 1) {
            vaux = baseprev
            taxa = evento.taxa
        } else {
            vaux = EncargosSociais.AssistenciaMunicipal.buscaMaior(1)
            if (baseprev <= vaux) {
                vaux = baseprev
            }
            def vaux2 = Numeros.trunca(vaux, 2)
            taxa = EncargosSociais.AssistenciaMunicipal.buscaContribuicao(vaux2, 2)
        }
        valorReferencia = taxa
        valorCalculado = (vaux * taxa) / 100
    }
    Bases.compor(valorCalculado, Bases.DEDUCIRRFMES, Bases.ABATIRRF)
}

