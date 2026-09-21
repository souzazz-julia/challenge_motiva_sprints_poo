package br.com.motiva.dao;
import br.com.motiva.db.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade Equipe (cobre EquipeManutencao e EquipeRocada).
 * A hierarquia e persistida em uma unica tabela com a coluna discriminadora TIPO.
 */
public class EquipeManutencaoDAO {

    /** Record que é uma linha da tabela EQUIPE_MANUTENCAO. */
    public record EquipeRecord(
            Long id,
            String nome,
            int quantidadeMembros,
            String tipo,            // 'MANUTENCAO' ou 'ROCADA'
            String tipoEquipamento  // nulo quando tipo = 'MANUTENCAO'
    ) {
    }

    private static final String SQL_INSERIR =
            "INSERT INTO EQUIPE_MANUTENCAO (NOME, QUANTIDADE_MEMBROS, TIPO, TIPO_EQUIPAMENTO) "
                    + "VALUES (?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT ID, NOME, QUANTIDADE_MEMBROS, TIPO, TIPO_EQUIPAMENTO "
                    + "FROM EQUIPE_MANUTENCAO WHERE ID = ?";
    private static final String SQL_LISTAR =
            "SELECT ID, NOME, QUANTIDADE_MEMBROS, TIPO, TIPO_EQUIPAMENTO "
                    + "FROM EQUIPE_MANUTENCAO ORDER BY ID";
    private static final String SQL_ATUALIZAR =
            "UPDATE EQUIPE_MANUTENCAO SET NOME = ?, QUANTIDADE_MEMBROS = ?, TIPO = ?, "
                    + "TIPO_EQUIPAMENTO = ? WHERE ID = ?";
    private static final String SQL_DELETAR =
            "DELETE FROM EQUIPE_MANUTENCAO WHERE ID = ?";

    public EquipeManutencaoDAO() {
    }

    public EquipeRecord inserir(EquipeRecord equipe) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_INSERIR, new String[]{"ID"});
            stmt.setString(1, equipe.nome());
            stmt.setInt(2, equipe.quantidadeMembros());
            stmt.setString(3, equipe.tipo());
            stmt.setString(4, equipe.tipoEquipamento());
            stmt.executeUpdate();

            Long idGerado = null;
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idGerado = rs.getLong(1);
            }
            return new EquipeRecord(idGerado, equipe.nome(), equipe.quantidadeMembros(),
                    equipe.tipo(), equipe.tipoEquipamento());
        } catch (SQLException e) {
            System.err.println("Erro ao inserir equipe: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public EquipeRecord buscarPorId(long id) {
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
            System.err.println("Erro ao buscar equipe: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public List<EquipeRecord> listarTodas() {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<EquipeRecord> equipes = new ArrayList<>();
        try {
            stmt = conn.prepareStatement(SQL_LISTAR);
            rs = stmt.executeQuery();
            while (rs.next()) {
                equipes.add(mapear(rs));
            }
            return equipes;
        } catch (SQLException e) {
            System.err.println("Erro ao listar equipes: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public void atualizar(EquipeRecord equipe) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_ATUALIZAR);
            stmt.setString(1, equipe.nome());
            stmt.setInt(2, equipe.quantidadeMembros());
            stmt.setString(3, equipe.tipo());
            stmt.setString(4, equipe.tipoEquipamento());
            stmt.setLong(5, equipe.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar equipe: " + e.getMessage());
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
            System.err.println("Erro ao deletar equipe: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(null, stmt);
        }
    }

    private EquipeRecord mapear(ResultSet rs) throws SQLException {
        return new EquipeRecord(
                rs.getLong("ID"),
                rs.getString("NOME"),
                rs.getInt("QUANTIDADE_MEMBROS"),
                rs.getString("TIPO"),
                rs.getString("TIPO_EQUIPAMENTO")
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
