import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public static ObservableList<Reservation> data_table;

    @FXML
    private TableView<Reservation> myTableView;
    @FXML
    private TableColumn<Reservation, String> myDateColumn, myRestaurantColumn;
    @FXML
    private TableColumn<Reservation, Boolean> mySelectionColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        myRestaurantColumn.setCellValueFactory(new PropertyValueFactory<>("resto"));
        mySelectionColumn.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        try {
            loadData();
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData() throws SQLException, ParseException {
        data_table = FXCollections.observableArrayList();
        String[][] reservations = ControllerAuth.backend.requeteReserv();
        for(int i = 0; i < 30; i++){
            data_table.add(new Reservation(reservations[i][0], reservations[i][1]));
        }
        myTableView.setItems(data_table);
    }

    public void quitter(){
        Platform.exit();
    }


}
