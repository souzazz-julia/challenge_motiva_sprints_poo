package br.com.motiva.model;

// Conceito base de qualquer intervencao feita pela Motiva em um trecho.
// Abstrata: nao existe "intervencao generica" no mundo real, sempre ha um tipo.
public abstract class IntervencaoOperacional {

    private final String responsavel;

    protected IntervencaoOperacional(String responsavel) {
        this.responsavel = responsavel;
    }

    public abstract void executarServico(TrechoRodovia trecho);

    public String getResponsavel() {
        return responsavel;
    }
}
