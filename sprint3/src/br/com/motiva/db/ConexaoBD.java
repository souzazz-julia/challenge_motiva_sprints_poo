package br.com.motiva.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerencia a conexao unica com o banco Oracle (Singleton).
 */
public class ConexaoBD {

    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";

    // Valores padrao (Oracle Free local)
    private static final String URL =
            System.getProperty("db.url", "jdbc:oracle:thin:@//localhost:1521/FREEPDB1");
    private static final String USUARIO =
            System.getProperty("db.user", "motiva");
    private static final String SENHA =
            System.getProperty("db.password", "motiva");

    private static ConexaoBD instancia;

    private Connection conexao;

    // Construtor privado impede instanciacao externa
    private ConexaoBD() {
    }

    public static ConexaoBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBD();
        }
        return instancia;
    }

    /**
     * Abre a conexao (se ainda nao estiver aberta) e devolve
     * A mesma conexao e reutilizada por todos os DAOs durante a execucao
     */
    public Connection conectar() {
        try {
            if (conexao == null || conexao.isClosed()) {
                Class.forName(DRIVER);
                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                System.out.println("Conexao com o Oracle estabelecida.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC nao encontrado. Adicione ojdbc17.jar ao classpath.");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar no banco: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return conexao;
    }

    public void desconectar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("Conexao encerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao encerrar a conexao: " + e.getMessage());
        }
    }
}
