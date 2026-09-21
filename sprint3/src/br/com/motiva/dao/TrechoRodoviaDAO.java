package br.com.motiva.dao;
import br.com.motiva.db.ConexaoBD;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.model.TrechoSeco;
import br.com.motiva.model.TrechoUmido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade Trecho (TrechoRodovia, TrechoUmido e TrechoSeco)
 * A hierarquia e persistida em uma unica tabela com a coluna discriminadora TIPO.
 * A FK EQUIPE_ID guarda a equipe responsavel (pode ser nula)
 */
public class TrechoRodoviaDAO {

    /** Record que representa uma linha da tabela TRECHO_RODOVIA. */
    public record TrechoRecord(
            Long id,
            String identificacao,
            double quilometroInicial,
            double quilometroFinal,
            double nivelVegetacao,
            String tipo,      // 'COMUM', 'UMIDO' ou 'SECO'
            Long equipeId     // FK para EQUIPE_MANUTENCAO (pode ser nula)
    ) {
    }

    private static final String SQL_INSERIR =
            "INSERT INTO TRECHO_RODOVIA "
                    + "(IDENTIFICACAO, QUILOMETRO_INICIAL, QUILOMETRO_FINAL, NIVEL_VEGETACAO, TIPO, EQUIPE_ID) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT ID, IDENTIFICACAO, QUILOMETRO_INICIAL, QUILOMETRO_FINAL, NIVEL_VEGETACAO, TIPO, EQUIPE_ID "
                    + "FROM TRECHO_RODOVIA WHERE ID = ?";
    private static final String SQL_LISTAR =
            "SELECT ID, IDENTIFICACAO, QUILOMETRO_INICIAL, QUILOMETRO_FINAL, NIVEL_VEGETACAO, TIPO, EQUIPE_ID "
                    + "FROM TRECHO_RODOVIA ORDER BY ID";
    private static final String SQL_ATUALIZAR =
            "UPDATE TRECHO_RODOVIA SET IDENTIFICACAO = ?, QUILOMETRO_INICIAL = ?, QUILOMETRO_FINAL = ?, "
                    + "NIVEL_VEGETACAO = ?, TIPO = ?, EQUIPE_ID = ? WHERE ID = ?";
    private static final String SQL_DELETAR =
            "DELETE FROM TRECHO_RODOVIA WHERE ID = ?";

    public TrechoRodoviaDAO() {
    }

    public TrechoRecord inserir(TrechoRecord trecho) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_INSERIR, new String[]{"ID"});
            stmt.setString(1, trecho.identificacao());
            stmt.setDouble(2, trecho.quilometroInicial());
            stmt.setDouble(3, trecho.quilometroFinal());
            stmt.setDouble(4, trecho.nivelVegetacao());
            stmt.setString(5, trecho.tipo());
            if (trecho.equipeId() != null) {
                stmt.setLong(6, trecho.equipeId());
            } else {
                stmt.setNull(6, Types.NUMERIC);
            }
            stmt.executeUpdate();

            Long idGerado = null;
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idGerado = rs.getLong(1);
            }
            return new TrechoRecord(idGerado, trecho.identificacao(), trecho.quilometroInicial(),
                    trecho.quilometroFinal(), trecho.nivelVegetacao(), trecho.tipo(), trecho.equipeId());
        } catch (SQLException e) {
            System.err.println("Erro ao inserir trecho: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public TrechoRecord buscarPorId(long id) {
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
            System.err.println("Erro ao buscar trecho: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public List<TrechoRecord> listarTodas() {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<TrechoRecord> trechos = new ArrayList<>();
        try {
            stmt = conn.prepareStatement(SQL_LISTAR);
            rs = stmt.executeQuery();
            while (rs.next()) {
                trechos.add(mapear(rs));
            }
            return trechos;
        } catch (SQLException e) {
            System.err.println("Erro ao listar trechos: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(rs, stmt);
        }
    }

    public void atualizar(TrechoRecord trecho) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_ATUALIZAR);
            stmt.setString(1, trecho.identificacao());
            stmt.setDouble(2, trecho.quilometroInicial());
            stmt.setDouble(3, trecho.quilometroFinal());
            stmt.setDouble(4, trecho.nivelVegetacao());
            stmt.setString(5, trecho.tipo());
            if (trecho.equipeId() != null) {
                stmt.setLong(6, trecho.equipeId());
            } else {
                stmt.setNull(6, Types.NUMERIC);
            }
            stmt.setLong(7, trecho.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar trecho: " + e.getMessage());
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
            System.err.println("Erro ao deletar trecho: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            fechar(null, stmt);
        }
    }

    /**
     * Reconstroi o objeto de dominio, escolhendo a subclasse
     * correta pelo discriminador TIPO.
     */
    public static TrechoRodovia paraDominio(TrechoRecord r) {
        return switch (r.tipo()) {
            case "UMIDO" -> new TrechoUmido(r.identificacao(), r.quilometroInicial(),
                    r.quilometroFinal(), r.nivelVegetacao());
            case "SECO" -> new TrechoSeco(r.identificacao(), r.quilometroInicial(),
                    r.quilometroFinal(), r.nivelVegetacao());
            default -> new TrechoRodovia(r.identificacao(), r.quilometroInicial(),
                    r.quilometroFinal(), r.nivelVegetacao());
        };
    }

    private TrechoRecord mapear(ResultSet rs) throws SQLException {
        Long equipeId = rs.getLong("EQUIPE_ID");
        if (rs.wasNull()) {
            equipeId = null;
        }
        return new TrechoRecord(
                rs.getLong("ID"),
                rs.getString("IDENTIFICACAO"),
                rs.getDouble("QUILOMETRO_INICIAL"),
                rs.getDouble("QUILOMETRO_FINAL"),
                rs.getDouble("NIVEL_VEGETACAO"),
                rs.getString("TIPO"),
                equipeId
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
