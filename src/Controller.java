import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public void quitter(){
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
