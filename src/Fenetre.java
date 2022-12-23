import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Fenetre extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        selectDatabase();
    }

    public void selectDatabase() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("DatabaseSelector.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Sélection de la base de données");
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }

}
