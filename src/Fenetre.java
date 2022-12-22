import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Fenetre extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main.fxml")));
        primaryStage.setTitle("Restaurants GUI");
        primaryStage.setScene(new Scene(root));

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
