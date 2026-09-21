package br.com.motiva.model;

public class EquipeRocada extends EquipeManutencao {

    private String tipoEquipamento;

    public EquipeRocada(String nome, int quantidadeMembros, String tipoEquipamento) {
        super(nome, quantidadeMembros);
        this.tipoEquipamento = tipoEquipamento;
    }

    @Override
    public void executarServico(TrechoRodovia trecho) {
        System.out.println("Equipe " + getNome() + " acionada para o trecho " + trecho.getIdentificacao() + " (equipamento: " + tipoEquipamento + ").");
        // A logica de intervencao fica em RocadaMecanizada, evitando duplicacao.
        new RocadaMecanizada(getNome()).executarServico(trecho);
    }

    public String getTipoEquipamento() {
        return tipoEquipamento;
    }
}
