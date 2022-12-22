import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public static ObservableList<Reservation> data_table;

    @FXML
    private TableView<Reservation> myTableView;
    @FXML
    private TableColumn<Reservation, String> myDateColumn, myRestaurantColumn;
    @FXML
    private TableColumn<Reservation, Boolean> mySelectionColumn;


    private void loadData(){
        data_table = FXCollections.observableArrayList();

        data_table.add(new Reservation("25/12/2022", "Nimporte"));

        myTableView.setItems(data_table);
    }

    public void quitter(){
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        myRestaurantColumn.setCellValueFactory(new PropertyValueFactory<>("resto"));
        mySelectionColumn.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        loadData();
    }

}
