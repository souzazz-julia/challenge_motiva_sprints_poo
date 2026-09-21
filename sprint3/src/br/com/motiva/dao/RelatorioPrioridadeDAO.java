package br.com.motiva.dao;
import br.com.motiva.db.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO do historico de relatorios de prioridade.
 */
public class RelatorioPrioridadeDAO {

    /** Record que representa uma linha da tabela RELATORIO_PRIORIDADE. */
    public record RelatorioRecord(
            Long id,
            LocalDateTime dataGeracao,
            int qtMecanizada,
            int qtManual,
            int qtSemNecessidade,
            String resumo
    ) {
    }

    private static final String SQL_INSERIR =
            "INSERT INTO RELATORIO_PRIORIDADE "
                    + "(QT_MECANIZADA, QT_MANUAL, QT_SEM_NECESSIDADE, RESUMO) VALUES (?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT ID, DATA_GERACAO, QT_MECANIZADA, QT_MANUAL, QT_SEM_NECESSIDADE, RESUMO "
                    + "FROM RELATORIO_PRIORIDADE WHERE ID = ?";
    private static final String SQL_LISTAR =
            "SELECT ID, DATA_GERACAO, QT_MECANIZADA, QT_MANUAL, QT_SEM_NECESSIDADE, RESUMO "
                    + "FROM RELATORIO_PRIORIDADE ORDER BY DATA_GERACAO DESC";
    private static final String SQL_ATUALIZAR =
            "UPDATE RELATORIO_PRIORIDADE SET QT_MECANIZADA = ?, QT_MANUAL = ?, "
                    + "QT_SEM_NECESSIDADE = ?, RESUMO = ? WHERE ID = ?";
    private static final String SQL_DELETAR =
            "DELETE FROM RELATORIO_PRIORIDADE WHERE ID = ?";

    public RelatorioPrioridadeDAO() {
    }

    /**
     *Gerador do relatório(atalho) recebe as contagens ja calculadas e grava o historico.
     */
    public RelatorioRecord salvarRelatorio(int qtMecanizada, int qtManual,
                                           int qtSemNecessidade, String resumo) {
        return inserir(new RelatorioRecord(null, null, qtMecanizada, qtManual,
                qtSemNecessidade, resumo));
    }

    public RelatorioRecord inserir(RelatorioRecord relatorio) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_INSERIR, new String[]{"ID"});
            stmt.setInt(1, relatorio.qtMecanizada());
            stmt.setInt(2, relatorio.qtManual());
            stmt.setInt(3, relatorio.qtSemNecessidade());
            stmt.setString(4, relatorio.resumo());
            stmt.executeUpdate();

            Long idGerado = null;
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idGerado = rs.getLong(1);
            }
            return new RelatorioRecord(idGerado, LocalDateTime.now(), relatorio.qtMecanizada(),
                    relatorio.qtManual(), relatorio.qtSemNecessidade(), relatorio.resumo());
        } catch (SQLException e) {
            System.err.println("Erro ao inserir relatorio: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public RelatorioRecord buscarPorId(long id) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_BUSCAR_POR_ID);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar relatorio: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public List<RelatorioRecord> listarTodas() {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RelatorioRecord> relatorios = new ArrayList<>();
        try {
            stmt = conn.prepareStatement(SQL_LISTAR);
            rs = stmt.executeQuery();
            while (rs.next()) {
                relatorios.add(mapear(rs));
            }
            return relatorios;
        } catch (SQLException e) {
            System.err.println("Erro ao listar relatorios: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public void atualizar(RelatorioRecord relatorio) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_ATUALIZAR);
            stmt.setInt(1, relatorio.qtMecanizada());
            stmt.setInt(2, relatorio.qtManual());
            stmt.setInt(3, relatorio.qtSemNecessidade());
            stmt.setString(4, relatorio.resumo());
            stmt.setLong(5, relatorio.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar relatorio: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(null, stmt);
        }
    }

    public void deletar(long id) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_DELETAR);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar relatorio: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(null, stmt);
        }
    }

    private RelatorioRecord mapear(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("DATA_GERACAO");
        LocalDateTime data = (ts != null) ? ts.toLocalDateTime() : null;
        return new RelatorioRecord(
                rs.getLong("ID"),
                data,
                rs.getInt("QT_MECANIZADA"),
                rs.getInt("QT_MANUAL"),
                rs.getInt("QT_SEM_NECESSIDADE"),
                rs.getString("RESUMO")
        );
    }

    private void fechar(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar recursos: " + e.getMessage());
        }
    }
}
