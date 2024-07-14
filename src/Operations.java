import java.sql.SQLException;

public interface Operations {
    public void info();
    public void transaction_history() throws SQLException;
}
