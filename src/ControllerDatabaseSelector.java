import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerDatabaseSelector implements Initializable {

    @FXML
    private ListView<String> myListView;

    @FXML
    private Label myLabel;


    String[] databases = {"INSA2", "Localhost"};

    String currentSelection;
    String base = "Actuellement sélectionnée : ";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myListView.getItems().addAll(databases);

        myListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentSelection = myListView.getSelectionModel().getSelectedItem();
            myLabel.setText(base + currentSelection);
        });
    }

    public void switchToSceneUser(ActionEvent event) throws IOException {
        ControllerAuth.server = currentSelection;
        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("User.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Authentification");
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }

}
