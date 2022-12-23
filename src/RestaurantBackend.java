import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;

public class RestaurantBackend {

    public static final String LOCAL_SERVER = "jdbc:postgresql://localhost:5432/postgres";
    public static final String INSA_SERVER = "jdbc:postgresql://educ2.educ.insa:5432/INSA2";

    /**
     * Login et mot de passe de l’utilisateur
     */
    String userId, passWord;
    /**
     * Code de l’employé
     */
    String idEmp;
    /**
     * Date courante
     */
    MyDate dateCourante;
    /**
     * Pour connexion à la base INSA2
     */
    Connection connexion;
    /**
     * Pour instructions simples (select, lock...)
     */
    Statement r_select;
    /**
     * Pour ajouts de reservations
     */
    PreparedStatement r_insert;
    /**
     * Pour modifications de reservations
     */
    PreparedStatement r_update;
    /**
     * Pour suppressions de reservations futures
     */
    PreparedStatement r_delete;
    /**
     * Pour suppressions de reservations antérieures
     */
    PreparedStatement r_payer;
    /**
     * Pour ajouts d'une catégorie d'employé à un restaurant
     */
    PreparedStatement r_tarifs;

    /**
     * CONSTRUCTEUR établissant une connexion avec la base (attribut connexion).
     *
     * @param uid le nom de l’utilisateur
     * @param pwd le mot de passe de l’utilisateur
     * @throws Exception si erreur lors de la connexion à la base de donnees
     */

    public RestaurantBackend(String server, String uid, String pwd) throws Exception {
        Class.forName("org.postgresql.Driver"); // Chargement du driver
        userId = uid;
        passWord = pwd;
        if(server.equals("INSA2")){
            connexion = DriverManager.getConnection(INSA_SERVER, userId, passWord);
        } else if (server.equals("Localhost")) {
            connexion = DriverManager.getConnection(LOCAL_SERVER, userId, passWord);
        } else {
            throw new Exception("Serveur inconnu");
        }

        connexion.setAutoCommit(false);
        r_select = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        r_insert = connexion.prepareStatement("INSERT INTO PREFIX_Reservation VALUES (?,?,?)");
        r_update = connexion.prepareStatement("UPDATE PREFIX_Reservation SET Restaurant = ? WHERE Identifiant = ? AND Date = ?");
        r_delete = connexion.prepareStatement("DELETE FROM PREFIX_Reservation WHERE Identifiant = ? AND Date = ?");
        r_payer = connexion.prepareStatement("DELETE FROM PREFIX_Reservation WHERE Identifiant = ? AND Date < ?");
        r_tarifs = connexion.prepareStatement("INSERT INTO PREFIX_Tarif VALUES (?,?,?)");
    }

    public boolean verifEmp(int id) throws SQLException {
        PreparedStatement pstmt = connexion.prepareStatement("SELECT Identifiant FROM prefix_employee");
        ResultSet res = pstmt.executeQuery();
        while (res.next()) {
            if (res.getInt(1) == id) {
                return true;
            }
        }
        return false;
    }

    public void setEmp(String emp) {
        idEmp = emp;
    }

    public void setDate(String date) throws ParseException {
        dateCourante = new MyDate(date);
    }

    public MyDate getDateCourante(){
        return dateCourante;
    }

    public String getIdEmp(){
        return idEmp;
    }

    public String[][] requeteReserv() throws SQLException {
        PreparedStatement pstmt = connexion.prepareStatement("SELECT date, restaurant\n" + "FROM prefix_reservation\n" + "WHERE identifiant = ?\n" + "    AND date > ?\n" + "ORDER BY date;");
        pstmt.setInt(1, Integer.parseInt(idEmp));
        pstmt.setDate(2, dateCourante.toSQLDate());

        ResultSet res = pstmt.executeQuery();
        ArrayList<String[]> array = new ArrayList<>();
        while (res.next()) {
            array.add(new String[]{res.getString(1), res.getString(2)});
        }
        res.close();
        pstmt.close();

        MyDate date = new MyDate(dateCourante);
        String[][] rtn = new String[30][];
        for (int i = 0; i < 30; i++) {
            date.tomorrow();
            rtn[i] = new String[]{date.toSQLDate().toString(), ""};
            if(i >= array.size()){
                rtn[i][1] = "";
            } else {
                rtn[i][1] = array.get(i)[1];
            }
        }
        return rtn;
    }

}
