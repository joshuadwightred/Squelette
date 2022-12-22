import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Classe permettant de manipuler aisement differents formats de dates
 * en vous laissant ainsi le choix.
 *
 * @author Peggy Cellier
 * @author Xavier Le Guillou
 * @author Fabien Lotte
 * @author Adrien Delaye
 */
public class MyDate {
    /**
     * La Date java standard...
     */
    protected java.util.Date sycone;
    static protected DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
    static protected SimpleDateFormat dfsql =
            (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT);

    /**
     * Constructeur par defaut
     */
    public MyDate() {
        sycone = new java.util.Date();
    }

    /**
     * Constructeur par recopie
     *
     * @param date la MyDate a recopier
     */
    public MyDate(MyDate date) {
        sycone = date.sycone;
    }

    /**
     * Constructeur a partir d’une date sous forme de chaine de caracteres
     * La chaine est parsee sous les deux formats (java et sql)
     *
     * @param date la date sous forme d’une chaine de caracteres
     * @throws ParseException si la chaine ne peut etre parsee
     *                        92
     *                        INSA Rennes, D´epartement STPI Bases de donn´ees
     *                        sous aucun des deux formats
     */
    public MyDate(String date) throws java.text.ParseException {
        dfsql.applyPattern("yyyy-MM-dd");
        try {
            sycone = df.parse(date);
        } catch (java.text.ParseException e1) {
            try {
                sycone = dfsql.parse(date);
            } catch (java.text.ParseException e2) {
                throw e1;
            }
        }
    }

    /**
     * Constructeur a partir d’une date java standard
     *
     * @param date une date java standard
     */
    public MyDate(java.util.Date date) {
        sycone = date;
    }

    /**
     * Augmente la date courante d’un jour
     */
    public void tomorrow() {
        Calendar c = new GregorianCalendar();
        c.setTime(sycone);
        c.add(Calendar.DATE, 1);
        sycone = c.getTime();
    }

    /**
     * Conversion au format SQL
     *
     * @return la date au format SQL
     */
    public java.sql.Date toSQLDate() {
        return new java.sql.Date(sycone.getTime());
    }

    /**
     * Compare deux dates
     *
     * @param date une date au format MyDate
     * @return vrai si les deux dates sont egales
     * 93
     * INSA Rennes, D´epartement STPI Bases de donn´ees
     */
    public boolean egal(MyDate date) {
        return sycone.getTime() == date.sycone.getTime();
    }

    /**
     * Compare deux dates
     *
     * @param date une date au format SQL
     * @return vrai si les deux dates sont egales
     */
    public boolean egal(java.sql.Date date) {
        return sycone.getTime() == date.getTime();
    }

    /**
     * Surcharge et redefinition de toString()
     *
     * @return la date sous forme d’une chaine de caracteres (format Java)
     */
    public String toString() {
        return df.format(sycone);
    }
}
