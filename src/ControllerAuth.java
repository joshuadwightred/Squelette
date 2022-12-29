import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class ControllerAuth {

    static RestaurantBackend backend;
    static String server;

    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private Label myLabel;
    @FXML
    private Label myEmployeeLabel;

    @FXML
    private TextField myTextField;
    @FXML
    private TextField myEmployeeTextField;

    @FXML
    private PasswordField myPasswordField;

    public void switchToSceneEmployee(ActionEvent event) {
        try {
            backend = new RestaurantBackend(server, myTextField.getText(), myPasswordField.getText());
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Employee.fxml")));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            myLabel.setText("Erreur d'authentification");
            System.out.println(e.getMessage());
        }
    }

    public void seConnecter(ActionEvent event) {
        try {
            if(backend.verifEmp(Integer.parseInt(myEmployeeTextField.getText()))){
                backend.setEmp(myEmployeeTextField.getText());
                backend.setDate(java.time.LocalDate.now().toString());
                ((Node)event.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("Main.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Restaurants-GUI");
                stage.setScene(new Scene(root, 600, 600));
                stage.show();
            } else {
                myEmployeeLabel.setText("Code inconnu");
            }
        } catch (Exception e) {
            myEmployeeLabel.setText("Erreur de code");
        }
    }

}
