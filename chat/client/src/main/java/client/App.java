package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage){
        stage.setScene(new Scene(new ChatController().crAuthPane()));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
