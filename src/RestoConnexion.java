import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe gerant la connexion a la base de donnees
 */
public class RestoConnexion {
    /**
     * Tampon de lecture pour la saisie des requetes en mode console
     */
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    /**
     * Login et mot de passe de l’utilisateur
     */
    String userId, passWord;
    /**
     * Code de l’employe
     */
    String idEmp;
    /**
     * Date courante
     */
    MyDate dateCourante;
    /**
     * Pour connexion a la base INSA2
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
     * Pour suppressions de reservations anterieures
     */
    PreparedStatement r_payer;
    /**
     * Pour ajouts d'une categorie d'employé a un restaurant
     */
    PreparedStatement r_tarifs;

    /**
     * CONSTRUCTEUR établissant une connexion avec la base (attribut connexion).
     *
     * @param uid le nom de l’utilisateur
     * @param pwd le mot de passe de l’utilisateur
     * @throws Exception si erreur lors de la connexion a la base de donnees
     */
    public RestoConnexion(String uid, String pwd) throws Exception {
        Class.forName("org.postgresql.Driver"); // Chargement du driver
        userId = uid;
        passWord = pwd;
        connexion = DriverManager.getConnection("jdbc:postgresql:postgres", userId, passWord);
        connexion.setAutoCommit(false);
        r_select = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//!\\ ATTENTION //!\\
// Les noms des tables utilisées ci-dessous sont à adapter
// aux noms de vos propres tables
        r_insert = connexion.prepareStatement("INSERT INTO PREFIX_Reservation VALUES (?,?,?)");
        r_update = connexion.prepareStatement("UPDATE PREFIX_Reservation SET Restaurant = ? " + "WHERE Identifiant = ? AND Date = ?");
        r_delete = connexion.prepareStatement("DELETE FROM PREFIX_Reservation " + "WHERE Identifiant = ? AND Date = ?");
        r_payer = connexion.prepareStatement("DELETE FROM PREFIX_Reservation " + "WHERE Identifiant = ? AND Date < ?");
        r_tarifs = connexion.prepareStatement("INSERT INTO PREFIX_Tarif VALUES (?,?,?)");
    }

    /**
     * Methode posant une question à l’utilisateur et retournant la donnee saisie.
     *
     * @param prompt la question posée a l’utilisateur
     * @return la chaine de caracteres reponse de l’utilisateur
     */
    private static String acquisition(final String prompt) {
        String chaine = "";
        System.out.print(prompt + " > ");
        System.out.flush();
        try {
            chaine = br.readLine();
        } catch (java.io.IOException e) {
            System.out.println(e + "ds lecture de <" + prompt + ">");
        }
        return (chaine);
    }

    /**
     * Affichage de l’aide.
     *
     * @return la chaine de caracteres d’aide
     */
    private static String aide() {
        return "" + "employe          -  Changer d'employé\n" + "date             -  Changer la date courante\n" + "restaurants      -  Liste des restaurants\n" + "reservations     -  Liste des reservations (pour les 30 prochains jours)\n" + "historique       -  Liste des reservations passées\n" + "bilan            -  Bilan total des reservations antérieures a la date courante\n" + "nv-restaurant    -  Ajout d'un nouveau restaurant\n" + "nv-tarif         -  Ajout d'une catégorie à un restaurant avec le tarif associé\n" + "maj-reservation  -  Ajouts, modifications et suppressions de reservations\n" + "payer            -  Paiement des reservations passées\n" + "quitter          -  Sortie\n";
    }

    public static int trueLength(String text) {
        int taille = 0;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) != ' ' || (text.charAt(i) == ' ' && text.charAt(i + 1) != ' ')) {
                taille += 1;
            }
        }
        return taille;
    }

    /**
     * Affichage d’un tableau en 1D.
     *
     * @param t1 le tableau à afficher
     */
    private static void afftab1colonne(String[] t1) {
        int MAX = 0;
        for (String s : t1) {
            if (trueLength(s) > MAX) MAX = trueLength(s);
        }
        for (String s : t1) {
            StringBuilder filler = new StringBuilder();
            for (int i = trueLength(s); i < MAX; i++) {
                filler.append(" ");
            }
            System.out.println("| " + s + filler + " |");
        }
    }

    private static void afftab1ligne(String[] t1) {
        for (String s : t1) System.out.print("| " + s + " ");
        System.out.println("|");
    }


    /**
     * Affichage d’un tableau en 2D.
     *
     * @param t2 le tableau à afficher
     */
    private static void afftab2(String[][] t2) {
        if (t2.length == 0) {
            System.out.println("(Vide)");
            return;
        }
        int[] ls = new int[t2[0].length];
        for (String[] strings : t2) {
            for (int i = 0; i < strings.length; i++) ls[i] = Integer.max(ls[i], strings[i].length());
        }

        for (String[] strings : t2) {
            for (int i = 0; i < strings.length; i++) {
                while (strings[i].length() < ls[i]) {
                    strings[i] = strings[i] + " ";
                }
            }
        }
        for (String[] strings : t2) afftab1ligne(strings);
    }
/*************************************************************************
 * Requetes de selection
 *************************************************************************/

    /**
     * Acquisition et verification du code employe.
     *
     * @param client la connexion a la base de donnees
     * @return le code de l’employe
     * @throws SQLException si erreur lors de l’execution de la requete SQL
     */
    private static String saisieEmp(RestoConnexion client) throws SQLException {
        while (true) {
            String code = acquisition("Code employe");
            if (client.verifEmp(code)) return code;
        }
    }

    /**
     * Fonction principale du programme.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Connexion
            //RestoConnexion client = new RestoConnexion(acquisition("Utilisateur"), acquisition("Mot de passe"));
            RestoConnexion client = new RestoConnexion("postgres", "admin");
            client.setEmp(saisieEmp(client));
            client.setDate(java.time.LocalDate.now().toString());
            // Menu
            String choix = "";
            while (!choix.equals("quitter")) {
                choix = acquisition("*** MENU (? : aide) ***");
                switch (choix) {
                    case "?":
                        System.out.print(aide());
                        break;
                    case "employe":
                        client.setEmp(saisieEmp(client));
                        break;
                    case "date":
                        client.setDate(acquisition("Date courante"));
                        break;
                    case "restaurants":
                        afftab1colonne(client.requeteResto());
                        break;
                    case "reservations":
                        afftab2(client.requeteReserv());
                        break;
                    case "historique":
                        afftab2(client.requeteConso());
                        break;
                    case "bilan":
                        afftab1ligne(client.requeteTotal());
                        break;
                    case "nv-restaurant": {
                        String nomresto = acquisition("Nom du nouveau restaurant");
                        String villeresto = acquisition("Ville du nouveau restaurant");
                        int capresto = Integer.parseInt(acquisition("Capacite du nouveau restaurant"));
                        String res = client.addResto(nomresto, villeresto, capresto);
                        System.out.println(res == null ? "OK" : res);
                        break;
                    }
                    case "nv-tarif": {
                        String cat = acquisition("Categorie");
                        String resto = acquisition("Restaurant");
                        int tarifCR = Integer.parseInt(acquisition("Tarif pour cette categorie dans ce restaurant"));
                        String res = client.addTarif(cat, resto, tarifCR);
                        System.out.println(res == null ? "OK" : res);
                        break;
                    }
                    case "maj-reservation": {
                        int nbins;
                        try {
                            nbins = Integer.parseInt(acquisition("nombre d’ajouts"));
                        } catch (Exception e) {
                            nbins = 0;
                        }
                        String[][] ins = new String[nbins][2];
                        for (int i = 0; i < nbins; i++) {
                            ins[i][0] = acquisition("INSERT " + (i + 1) + " date");
                            ins[i][1] = acquisition("INSERT " + (i + 1) + " resto");
                        }
                        int nbmod;
                        try {
                            nbmod = Integer.parseInt(acquisition("nombre de modifications"));
                        } catch (Exception e) {
                            nbmod = 0;
                        }
                        String[][] mod = new String[nbmod][2];
                        for (int i = 0; i < nbmod; i++) {
                            mod[i][0] = acquisition("UPDATE " + (i + 1) + " date");
                            mod[i][1] = acquisition("UPDATE " + (i + 1) + " resto");
                        }
                        int nbsup;
                        try {
                            nbsup = Integer.parseInt(acquisition("nombre de suppressions"));
                        } catch (Exception e) {
                            nbsup = 0;
                        }
                        String[] sup = new String[nbsup];
                        for (int i = 0; i < nbsup; i++)
                            sup[i] = acquisition("DELETE " + (i + 1) + " date");
                        String res = client.majReserv(ins, mod, sup);
                        System.out.println(res == null ? "Ok" : res);
                        break;
                    }
                    case "payer":
                        client.payer();
                        System.out.println("Ok");
                        break;
                }
            }
            client.terminer();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Cloture des instructions puis de la session.
     *
     * @throws SQLException
     */
    public void terminer() throws SQLException {
        r_select.close();
        r_insert.close();
        r_update.close();
        r_delete.close();
        r_payer.close();
        r_tarifs.close();
        connexion.close();
    }

    /**
     * Mise a jour de l’attribut dateCourante.
     *
     * @param date la nouvelle date
     * @throws ParseException si la nouvelle date n’est pas au bon format
     */
    public void setDate(String date) throws ParseException {
        dateCourante = new MyDate(date);
    }
/*************************************************************************
 * Requetes de mise a jour
 *************************************************************************/

    /**
     * Mise a jour de l’attribut idEmp.
     *
     * @param emp la nouvelle valeur de l’attribut idEmp
     */
    public void setEmp(String emp) {
        idEmp = emp;
    }
/*************************************************************************
 * Nouvelle requete de selection
 *************************************************************************/

    /**
     * SELECTION : verification de l’existence du code employe.
     *
     * @param id le code employe a verifier
     * @return vrai si le code existe dans la base
     * @throws SQLException si erreur lors de l’execution de la requete SQL
     */
    public boolean verifEmp(String id) throws SQLException {
        PreparedStatement pstmt = connexion.prepareStatement("SELECT Identifiant FROM prefix_employee");
        ResultSet res = pstmt.executeQuery();
        while (res.next()) {
            if (res.getInt(1) == Integer.parseInt(id)) {
                return true;
            }
        }
        return false;
    }
/*************************************************************************
 * Si vous avez le temps : requetes de mise a jour
 *************************************************************************/

    /**
     * SELECTION : liste des restaurants.
     *
     * @return un tableau de chaines a 1 dimension qui contient exactement
     * les restaurants existants
     * @throws SQLException si erreur lors de l’execution de la requete SQL
     */
    public String[] requeteResto() throws SQLException {
        PreparedStatement pstmt = connexion.prepareStatement("SELECT restaurant\n" + "FROM prefix_restaurant;");
        ResultSet res = pstmt.executeQuery();
        List<String> restos = new ArrayList<>();
        while (res.next()) {
            restos.add(res.getString(1));
        }
        pstmt.close();
        return restos.toArray(restos.toArray(new String[0]));
    }

    /**
     * SELECTION : liste des reservations antérieures à la date courante.
     *
     * @return Un tableau de chaines a 2 dimensions. Pour n reservations
     * passées, le tableau rendu contient n lignes et 3 colonnes
     * (une ligne par date) ; la premiere colonne contient les dates
     * (sous forme de chaine), la deuxième colonne contient le nom d’un
     * restaurant et la troisième contient le prix du repas
     * @throws SQLException   si erreur lors de l’execution de la requête SQL
     * @throws ParseException si erreur de format
     */
    public String[][] requeteConso() throws SQLException, ParseException {
        PreparedStatement pstmt = connexion.prepareStatement("SELECT date, prefix_reservation.restaurant, prix\n" + "FROM prefix_reservation, prefix_employee, prefix_tarif\n" + "WHERE prefix_reservation.identifiant = prefix_employee.identifiant AND\n" + "      prefix_employee.categorie = prefix_tarif.categorie AND\n" + "      prefix_reservation.restaurant = prefix_tarif.restaurant AND\n" + "      prefix_reservation.identifiant = ? AND\n" + "      prefix_reservation.date < ?;");
        pstmt.setInt(1, Integer.parseInt(idEmp));
        pstmt.setDate(2, dateCourante.toSQLDate());
        ResultSet res = pstmt.executeQuery();
        List<String[]> historique = new ArrayList<>();
        while (res.next()) {
            historique.add(new String[]{res.getString(1), res.getString(2), res.getString(3)});
        }
        pstmt.close();
        return historique.toArray(new String[0][0]);
    }

    /**
     * SELECTION : recapitulatif des reservations anterieures a la date courante.
     *
     * @return un tableau de chaines. Le tableau rendu contient 3 cases.
     * Il contient dans l’ordre : le nombre total de reservations passees
     * (premiere case), le nombre total de restaurants frequentes (deuxieme
     * case) et la somme due (troisieme case).
     * ATTENTION : il est fortement conseille de ne pas utiliser le tableau
     * de resultat de requeteConso, car ce serait beaucoup plus
     * problematique que de tout reecrire.
     * En revanche, cette methode s’inspire assez fortement de la methode
     * requeteConso, en particulier dans la partie "where" du "select".
     * NB : penser au cas ou l’employe n’a pas de reservations passees.
     * "sum()" calcule sur une colonne vide ne rend pas "0"
     * mais "NULL", contrairement a "count()" qui rend bien "0"
     * @throws SQLException si erreur lors de l’execution de la requete SQL
     */
    public String[] requeteTotal() throws SQLException {
        PreparedStatement pstmt = connexion.prepareStatement("SELECT count(*), count(Distinct prefix_reservation.restaurant), sum(prix)\n" + "FROM prefix_reservation, prefix_employee, prefix_tarif\n" + "WHERE prefix_reservation.identifiant = prefix_employee.identifiant AND\n" + "      prefix_employee.categorie = prefix_tarif.categorie AND\n" + "      prefix_reservation.restaurant = prefix_tarif.restaurant AND\n" + "      prefix_reservation.identifiant = ? AND\n" + "      prefix_reservation.date < ?;");
        pstmt.setInt(1, Integer.parseInt(idEmp));
        pstmt.setDate(2, dateCourante.toSQLDate());
        ResultSet res = pstmt.executeQuery();

        res.next();
        if (res.getInt(1) == 0) {
            return new String[]{"0", "0", "0.00"};
        } else {
            String[] historique = {res.getString(1), res.getString(2), res.getString(3)};
            pstmt.close();
            return historique;
        }
    }
/*************************************************************************
 * MAIN
 *************************************************************************/

    /**
     * MAJ : ajout d’un nouveau restaurant
     * Cette methode permet d’ajouter un restaurant.
     * /!\ ATTENTION /!\
     * Une ville ne peut pas avoir plus de 10 restaurants.
     * Si 10 restaurants existent deja dans la ville ou se trouve le
     * restaurant a ajouter, alors le restaurant n’est pas ajoute.
     *
     * @param nomresto   nom du restaurant a ajouter (chaine de caracteres).
     * @param villeresto nom de la ville du restaurant (chaine de caracteres).
     * @param capresto   capacite du restaurant (entier).
     * @return une chaine de caracteres indiquant un message d’erreur,
     * ou NULL si la transaction est correcte
     * @throws SQLException   si erreur lors de l’execution de la requete SQL
     * @throws ParseException si erreur de format
     */
    public String addResto(String nomresto, String villeresto, int capresto) throws SQLException, ParseException {
        try {
            connexion.createStatement().executeUpdate("BEGIN;\n" + "LOCK TABLE prefix_restaurant in EXCLUSIVE MODE;");
            PreparedStatement pstmt = connexion.prepareStatement("SELECT count(distinct restaurant)\n" + "FROM prefix_restaurant\n" + "WHERE ville = ?;");
            pstmt.setString(1, villeresto);
            ResultSet res = pstmt.executeQuery();
            res.next();
            if (res.getInt(1) >= 10) {
                connexion.createStatement().executeUpdate("COMMIT;");
                return nomresto + " n'a pas pu être ajouté car la capacité maximale de la ville est déjà atteinte\n";
            }

            pstmt = connexion.prepareStatement("INSERT INTO prefix_restaurant VALUES (?, ?, ?);");
            pstmt.setString(1, nomresto);
            pstmt.setString(2, villeresto);
            pstmt.setInt(3, capresto);
            pstmt.executeUpdate();
            connexion.createStatement().executeUpdate("COMMIT;");
            return nomresto + " a bien été ajouté\n";
        } catch (Exception e) {
            connexion.createStatement().executeUpdate("COMMIT;");
            return nomresto + " n'a pas pu être ajouté\n" + e.getMessage();
        }
    }

    /**
     * SELECTION : liste des reservations pour les 30 jours a venir.
     *
     * @return un tableau de chaines a 2 dimensions : 30 lignes et 2 colonnes
     * (une ligne par date) ; la premiere colonne contient les dates
     * (sous forme de chaine), et la seconde contient le nom d’un
     * restaurant (en cas de reservation pour cette date) ou la chaine vide
     * (non pas NULL)
     * @throws SQLException si erreur lors de l’execution de la requete SQL
     */
    public String[][] requeteReserv() throws SQLException, ParseException {
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

    /**
     * MAJ : reglement des reservations de l’employe anterieures a la date courante.
     * Cette methode a pour effet de supprimer les reservations
     * passees de l’employe.
     *
     * @throws SQLException   si erreur lors de l’execution de la requete SQL.
     * @throws ParseException si erreur de format.
     */
    public void payer() throws SQLException, ParseException {
        PreparedStatement pstmt = connexion.prepareStatement("DELETE FROM prefix_reservation\n" + "WHERE prefix_reservation.identifiant = ?\n" + "    AND prefix_reservation.date <= ?;");
        pstmt.setInt(1, Integer.parseInt(idEmp));
        pstmt.setDate(2, dateCourante.toSQLDate());
        int r = pstmt.executeUpdate();
        System.out.println(r + " Payement(s) effectué(s)");
    }

    /**
     * MAJ : ajout/modification/suppression de reservations.
     * Cette methode permet d’ajouter, de modifier ou de supprimer des
     * reservations. Ces trois types mises a jour peuvent potentiellement
     * etre effectues simultanement.
     * /!\ ATTENTION /!\
     * On ne tente ici qu’UNE SEULE transaction. S’il y a un probleme,
     * la transaction ENTIERE est annulee.
     *
     * @param insert tableau (a 2 colonnes) des nouvelles reservations.
     *               Chaque ligne correspond a un couple (date,restaurant) que vous devrez
     *               ajouter a la base
     * @param update tableau (a 2 colonnes) des modifications de reservations.
     *               Chaque ligne correspond a un couple (date,restaurant)
     * @param delete tableau (a 1 colonne) des suppressions de reservations.
     *               Chaque element correspond a une date pour laquelle il faut supprimer
     *               la reservation
     * @return une chaine de caracteres indiquant un message d’erreur,
     * ou NULL si la transaction est correcte
     * @throws SQLException   si erreur lors de l’execution de la requete SQL
     * @throws ParseException si erreur de format
     */
    public String majReserv(String[][] insert, String[][] update, String[] delete) throws SQLException, ParseException {

        try {
            connexion.createStatement().executeUpdate("BEGIN;\n" + "LOCK TABLE prefix_reservation in EXCLUSIVE MODE;");

            for (String[] couple : insert) {
                PreparedStatement pstmt = connexion.prepareStatement("INSERT INTO PREFIX_Reservation\n" + "VALUES (?, ?, ?);");
                pstmt.setString(1, couple[1]);
                pstmt.setInt(2, Integer.parseInt(idEmp));
                pstmt.setDate(3, new MyDate(couple[0]).toSQLDate());

                int r = pstmt.executeUpdate();
                if (r == 0) {
                    System.out.println("La reservation à " + couple[1] + " le " + couple[0] + " n'a pas pu être ajoutée");
                }
                pstmt.close();
            }

            for (String[] couple : update) {
                PreparedStatement pstmt = connexion.prepareStatement("UPDATE prefix_reservation\n" + "SET restaurant = ?\n" + "WHERE identifiant = ?\n" + "  AND date = ?;");
                pstmt.setString(1, couple[1]);
                pstmt.setInt(2, Integer.parseInt(idEmp));
                pstmt.setDate(3, new MyDate(couple[0]).toSQLDate());
                int r = pstmt.executeUpdate();

                if (r == 0) {
                    System.out.println("La reservation à " + couple[1] + " le " + couple[0] + " n'a pas pu être ajoutée");
                }
                pstmt.close();
            }

            for (String date : delete) {
                PreparedStatement pstmt = connexion.prepareStatement("DELETE FROM prefix_reservation\n" + "WHERE prefix_reservation.identifiant = ?\n" + "    AND prefix_reservation.date = ?;");
                pstmt.setInt(1, Integer.parseInt(idEmp));
                pstmt.setDate(2, new MyDate(date).toSQLDate());
                int r = pstmt.executeUpdate();

                if (r == 0) {
                    System.out.println("La reservation le " + date + " n'a pas pu être supprimée");
                }
                pstmt.close();
            }

            PreparedStatement pstmt = connexion.prepareStatement("SELECT *\n" + "FROM prefix_reservation, prefix_restaurant\n" + "WHERE prefix_restaurant.restaurant = prefix_reservation.restaurant\n" + "GROUP BY prefix_reservation.date, prefix_restaurant.capacite, prefix_restaurant.restaurant, prefix_reservation.restaurant, prefix_reservation.identifiant\n" + "HAVING COUNT(DISTINCT identifiant) > prefix_restaurant.capacite;");
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                connexion.createStatement().executeUpdate("ROLLBACK;");
                return "La capacité d'un restaurant n'a pas été respectée";
            }
            pstmt.close();
            res.close();

            pstmt = connexion.prepareStatement("SELECT prefix_reservation.date, prefix_reservation.identifiant\n" + "FROM prefix_reservation\n" + "EXCEPT\n" + "SELECT prefix_reservation.date, prefix_reservation.identifiant\n" + "FROM prefix_reservation,\n" + "     prefix_employee,\n" + "     prefix_tarif\n" + "WHERE prefix_tarif.restaurant = prefix_reservation.restaurant\n" + "  AND prefix_employee.identifiant = prefix_reservation.identifiant\n" + "  AND prefix_employee.categorie = prefix_tarif.categorie;");
            res = pstmt.executeQuery();

            if (res.next()) {
                connexion.createStatement().executeUpdate("ROLLBACK;");
                return "La catégorie d'un employé n'a pas été respectée";
            }
            pstmt.close();
            res.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "Impossible de mettre à jour\n" + e.getMessage();
        } finally {
            connexion.createStatement().executeUpdate("COMMIT;");
        }
    }

    /**
     * MAJ : ajout d’une categorie a un restaurant avec le tarif
     * 87
     * INSA Rennes, D´epartement STPI Bases de donn´ees
     *
     * @param cat     cat´egorie (chaine de caracteres).
     * @param resto   restaurant (chaine de caracteres).
     * @param tarifCR tarif associe a la categorie pour ce restaurant (entier).
     * @return une chaine de caracteres indiquant un message d’erreur,
     * ou NULL si la transaction est correcte
     * @throws SQLException si erreur lors de l’execution de la requete SQL
     */
    public String addTarif(String cat, String resto, double tarifCR) throws SQLException, ParseException {
        PreparedStatement pstmt = connexion.prepareStatement("INSERT INTO PREFIX_tarif VALUES (?, ?, ?);");
        pstmt.setString(1, resto);
        pstmt.setString(2, cat);
        pstmt.setDouble(3, tarifCR);

        if (pstmt.executeUpdate() == 0) {
            return "L'ajout n'a pas fonctionné";
        }

        return null;
    }
}
