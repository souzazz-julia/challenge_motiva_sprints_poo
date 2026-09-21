package br.com.motiva.model;

public class TrechoRodovia {

    private String identificacao;
    private double quilometroInicial;
    private double quilometroFinal;
    private double nivelVegetacao;
    private EquipeManutencao equipeResponsavel;

    public TrechoRodovia(String identificacao, double quilometroInicial, double quilometroFinal, double nivelVegetacao) {
        this.identificacao = identificacao;
        this.quilometroInicial = quilometroInicial;
        this.quilometroFinal = quilometroFinal;
        this.setNivelVegetacao(nivelVegetacao);
    }

    public void registrarCrescimento(double taxa) {
        if (taxa < 0) {
            throw new IllegalArgumentException("A taxa de crescimento nao pode ser negativa.");
        }
        this.nivelVegetacao += taxa * getFatorCrescimento();
    }

    // Trechos genericos crescem na taxa informada. Subclasses ajustam este fator
    // conforme suas condicoes (ex.: umido cresce mais rapido, seco cresce menos).
    protected double getFatorCrescimento() {
        return 1.0;
    }

    public boolean precisaRocada() {
        return this.nivelVegetacao >= 30.0;
    }

    public void atribuirEquipe(EquipeManutencao equipe) {
        this.equipeResponsavel = equipe;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public double getQuilometroInicial() {
        return quilometroInicial;
    }

    public double getQuilometroFinal() {
        return quilometroFinal;
    }

    public double getNivelVegetacao() {
        return nivelVegetacao;
    }

    public void setNivelVegetacao(double nivelVegetacao) {
        if (nivelVegetacao < 0) {
            throw new IllegalArgumentException("O nivel de vegetacao nao pode ser negativo.");
        }
        this.nivelVegetacao = nivelVegetacao;
    }

    public EquipeManutencao getEquipeResponsavel() {
        return equipeResponsavel;
    }
}
