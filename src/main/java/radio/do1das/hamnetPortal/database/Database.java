package radio.do1das.hamnetPortal.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import radio.do1das.hamnetPortal.config.Configuration;
import radio.do1das.hamnetPortal.config.ConfigurationManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final static Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private Connection con = null;

    public Connection getCon() throws SQLException {
        if (con == null || con.isClosed())
            openConnection();
        return con;
    }

    private void openConnection() {
        ConfigurationManager.getInstance().loadConfigurationFile("/settings.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        String url = "jdbc:mysql://" + conf.getDbHost() + ":" + conf.getDbPort() + "/" + conf.getDbName();
        try {
            con = DriverManager.getConnection(url, conf.getDbUser(), conf.getDbPass());
        } catch (SQLException e) {
            LOGGER.error("Fehler bei der Datenbankverbindung", e);
            throw new RuntimeException(e);
        }
    }

    private void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            LOGGER.debug("Datenbankverbindung konnte nicht getrennt werden", e);
        }
    }

    @Override
    protected void finalize() {
        closeConnection();
    }
}
