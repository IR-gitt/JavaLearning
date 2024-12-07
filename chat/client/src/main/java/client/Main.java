package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage stage1 = new Stage();
        App app = new App();
        app.start(stage1);
        App app2 = new App();
        Stage stage = new Stage();
        app2.start(stage);
    }
}
