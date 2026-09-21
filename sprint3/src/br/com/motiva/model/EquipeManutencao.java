package br.com.motiva.model;

public class EquipeManutencao {

    private String nome;
    private int quantidadeMembros;

    public EquipeManutencao(String nome, int quantidadeMembros) {
        this.nome = nome;
        this.quantidadeMembros = quantidadeMembros;
    }

    public void executarServico(TrechoRodovia trecho) {
        System.out.println("Equipe " + nome + " executando servico no trecho " + trecho.getIdentificacao() + ".");
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeMembros() {
        return quantidadeMembros;
    }
}
