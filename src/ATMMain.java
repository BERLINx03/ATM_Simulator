import atm.ATM;
import atm.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import services.AccountService;

import java.io.IOException;
import java.util.Objects;


public class ATMMain extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        AccountService accountService = new AccountService();
        ATM atm = new ATM(accountService);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
        Parent root = loader.load();

        // Get Controller and set ATM
        GuiController controller = loader.getController();
        controller.setATM(atm);

        // Set the scene
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("ATM Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
