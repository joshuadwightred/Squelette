import javafx.scene.control.CheckBox;

public class Reservation {

    private String date, resto;
    private CheckBox checkBox;

    public Reservation(String date, String resto){
        this.checkBox = new CheckBox();
        this.date = date;
        this.resto = resto;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResto() {
        return resto;
    }

    public void setResto(String resto) {
        this.resto = resto;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    //validate.setOnAction();
}
