package radio.do1das.hamnetPortal.database.objects;

import org.mindrot.jbcrypt.BCrypt;
import radio.do1das.hamnetPortal.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    public int id;
    public String callsign;
    public String name;
    public String mail;
    private String pwHash;
    public Date registeredSince;
    public Date lastLogin;
    public boolean activated;

    public User (String callsign) {
        this.callsign = callsign;
    }

    public boolean createUser(String name, String mail, String password) {
        String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt());
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Database db = new Database();
            conn = db.getCon();
            String query = "INSERT INTO user (callsign, name, mail, pwHash, registeredSince) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, callsign);
            stmt.setString(2, name);
            stmt.setString(3, mail);
            stmt.setString(4, hashedPw);
            stmt.setString(5, convertDate(new Date()));
            stmt.executeUpdate();
            stmt.close();
            return getUser();
        } catch (SQLException e) {
            //ToDo
        }


        return false;
    }

    private boolean getUser() {
        Connection conn = null;
        PreparedStatement stmt = null;

        try{
            Database db = new Database();
            conn = db.getCon();

            String query = "SELECT * FROM user WHERE callsign = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, callsign);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.id = rs.getInt("id");
                this.name = rs.getString("name");
                this.mail = rs.getString("mail");
                this.pwHash = rs.getString("pwHash");
                this.registeredSince = rs.getDate("registeredSince");
                this.lastLogin = rs.getDate("lastLogin");
                this.activated = rs.getBoolean("activated");
                stmt.close();
                return true;
            }
        } catch (SQLException e) {
            //ToDo
        }

        return false;
    }

    public static String convertDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
