package br.com.motiva.main;
import br.com.motiva.dao.EquipeManutencaoDAO;
import br.com.motiva.dao.EquipeManutencaoDAO.EquipeRecord;
import br.com.motiva.dao.IntervencaoOperacionalDAO;
import br.com.motiva.dao.IntervencaoOperacionalDAO.IntervencaoRecord;
import br.com.motiva.dao.TrechoRodoviaDAO;
import br.com.motiva.dao.TrechoRodoviaDAO.TrechoRecord;
import br.com.motiva.db.ConexaoBD;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.service.GeradorRelatorio;
import java.util.List;

/**
 * Demonstra todas as operacoes da Sprint 3:
 * conexao, CRUD das tres entidades, geracao de relatorio com persistencia
 * e consulta do historico.
 */
public class Main {

    public static void main(String[] args) {
        ConexaoBD conexao = ConexaoBD.getInstancia();
        try {
            // 1. Testar conexao
            conexao.conectar();

            // 2. CRUD de Equipe
            System.out.println("\n===== CRUD DE EQUIPES =====");
            EquipeManutencaoDAO daoEquipe = new EquipeManutencaoDAO();

            EquipeRecord alfa = daoEquipe.inserir(
                    new EquipeRecord(null, "Equipe Alfa", 5, "ROCADA", "Trator de arrasto"));
            EquipeRecord beta = daoEquipe.inserir(
                    new EquipeRecord(null, "Equipe Beta", 3, "MANUTENCAO", null));
            System.out.println("Inseridas: " + alfa + " / " + beta);

            EquipeRecord buscada = daoEquipe.buscarPorId(alfa.id());
            System.out.println("Buscar por id: " + buscada);

            daoEquipe.atualizar(new EquipeRecord(beta.id(), "Equipe Beta", 4, "MANUTENCAO", null));
            System.out.println("Apos atualizar id " + beta.id() + ": " + daoEquipe.buscarPorId(beta.id()));

            System.out.println("Listagem completa:");
            daoEquipe.listarTodas().forEach(e -> System.out.println("  " + e));

            // 3. CRUD de Trechos (com FK para a equipe responsavel)
            System.out.println("\n===== CRUD DE TRECHOS =====");
            TrechoRodoviaDAO daoTrecho = new TrechoRodoviaDAO();

            daoTrecho.inserir(new TrechoRecord(null, "BR-116 KM 10 ao 15", 10.0, 15.0, 62.0, "UMIDO", alfa.id()));
            daoTrecho.inserir(new TrechoRecord(null, "SP-348 KM 50 ao 60", 50.0, 60.0, 18.0, "SECO", beta.id()));
            TrechoRecord comum = daoTrecho.inserir(
                    new TrechoRecord(null, "BR-101 KM 200 ao 210", 200.0, 210.0, 40.0, "COMUM", null));

            System.out.println("Buscar por id: " + daoTrecho.buscarPorId(comum.id()));
            daoTrecho.atualizar(new TrechoRecord(comum.id(), "BR-101 KM 200 ao 210",
                    200.0, 210.0, 55.0, "COMUM", alfa.id()));
            System.out.println("Apos atualizar id " + comum.id() + ": " + daoTrecho.buscarPorId(comum.id()));

            System.out.println("Listagem completa:");
            daoTrecho.listarTodas().forEach(t -> System.out.println("  " + t));

            // 4. CRUD de Intervencoes
            System.out.println("\n===== CRUD DE INTERVENCOES =====");
            IntervencaoOperacionalDAO daoIntervencao = new IntervencaoOperacionalDAO();

            IntervencaoRecord mec = daoIntervencao.inserir(
                    new IntervencaoRecord(null, "MECANIZADA", "Equipe Alfa"));
            daoIntervencao.inserir(new IntervencaoRecord(null, "PULVERIZACAO", "Equipe Beta"));
            System.out.println("Buscar por id: " + daoIntervencao.buscarPorId(mec.id()));
            System.out.println("Listagem completa:");
            daoIntervencao.listarTodas().forEach(i -> System.out.println("  " + i));

            // 5. Gerar relatorio a partir dos trechos do banco (com persistencia)
            System.out.println("\n===== GERACAO DE RELATORIO =====");
            List<TrechoRecord> registros = daoTrecho.listarTodas();
            TrechoRodovia[] trechos = registros.stream()
                    .map(TrechoRodoviaDAO::paraDominio)
                    .toArray(TrechoRodovia[]::new);

            GeradorRelatorio gerador = new GeradorRelatorio();
            gerador.gerarRelatorio(trechos);

            // 6. Consultar historico de relatorios
            System.out.println("\n===== HISTORICO DE RELATORIOS =====");
            new br.com.motiva.dao.RelatorioPrioridadeDAO()
                    .listarTodas()
                    .forEach(r -> System.out.println("  " + r));

        } catch (RuntimeException e) {
            System.err.println("Falha na execucao: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 7. Fechar conexao
            conexao.desconectar();
        }
    }
}
