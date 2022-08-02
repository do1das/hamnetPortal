package radio.do1das.hamnetPortal.config;

public class Configuration {
    private String dbHost;
    private int dbPort;
    private String dbUser;
    private String dbPass;
    private String dbName;
    public String getDbHost() {
        return dbHost;
    }
    public int getDbPort() {
        return dbPort;
    }
    public String getDbUser() {
        return dbUser;
    }
    public String getDbPass() {
        return dbPass;
    }
    public String getDbName() {
        return dbName;
    }
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }
    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }
    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
