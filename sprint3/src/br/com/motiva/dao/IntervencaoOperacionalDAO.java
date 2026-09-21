package br.com.motiva.dao;
import br.com.motiva.db.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade Intervencao (cobre RocadaMecanizada e Pulverizacao).
 * A hierarquia e persistida em uma unica tabela com a coluna discriminadora TIPO.
 */
public class IntervencaoOperacionalDAO {

    /** Record que representa uma linha da tabela INTERVENCAO_OPERACIONAL. */
    public record IntervencaoRecord(
            Long id,
            String tipo,        // 'MECANIZADA' ou 'PULVERIZACAO'
            String responsavel
    ) {
    }

    private static final String SQL_INSERIR =
            "INSERT INTO INTERVENCAO_OPERACIONAL (TIPO, RESPONSAVEL) VALUES (?, ?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT ID, TIPO, RESPONSAVEL FROM INTERVENCAO_OPERACIONAL WHERE ID = ?";
    private static final String SQL_LISTAR =
            "SELECT ID, TIPO, RESPONSAVEL FROM INTERVENCAO_OPERACIONAL ORDER BY ID";
    private static final String SQL_ATUALIZAR =
            "UPDATE INTERVENCAO_OPERACIONAL SET TIPO = ?, RESPONSAVEL = ? WHERE ID = ?";
    private static final String SQL_DELETAR =
            "DELETE FROM INTERVENCAO_OPERACIONAL WHERE ID = ?";

    public IntervencaoOperacionalDAO() {
    }

    public IntervencaoRecord inserir(IntervencaoRecord intervencao) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_INSERIR, new String[]{"ID"});
            stmt.setString(1, intervencao.tipo());
            stmt.setString(2, intervencao.responsavel());
            stmt.executeUpdate();

            Long idGerado = null;
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idGerado = rs.getLong(1);
            }
            return new IntervencaoRecord(idGerado, intervencao.tipo(), intervencao.responsavel());
        } catch (SQLException e) {
            System.err.println("Erro ao inserir intervencao: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public IntervencaoRecord buscarPorId(long id) {
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
            System.err.println("Erro ao buscar intervencao: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public List<IntervencaoRecord> listarTodas() {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<IntervencaoRecord> intervencoes = new ArrayList<>();
        try {
            stmt = conn.prepareStatement(SQL_LISTAR);
            rs = stmt.executeQuery();
            while (rs.next()) {
                intervencoes.add(mapear(rs));
            }
            return intervencoes;
        } catch (SQLException e) {
            System.err.println("Erro ao listar intervencoes: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public void atualizar(IntervencaoRecord intervencao) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_ATUALIZAR);
            stmt.setString(1, intervencao.tipo());
            stmt.setString(2, intervencao.responsavel());
            stmt.setLong(3, intervencao.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar intervencao: " + e.getMessage());
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
            System.err.println("Erro ao deletar intervencao: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(null, stmt);
        }
    }

    private IntervencaoRecord mapear(ResultSet rs) throws SQLException {
        return new IntervencaoRecord(
                rs.getLong("ID"),
                rs.getString("TIPO"),
                rs.getString("RESPONSAVEL")
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
