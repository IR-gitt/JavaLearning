package client;

import commands.ClientCommands;
import commands.ServerCommands;
import javafx.application.Platform;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatController {

    public TextField msgField;

    public TextField tfLogin;

    public PasswordField tfPassword;

    public ListView<String> listClient;

    public TextArea dialogArea;

    public Pane authPane;
    public BorderPane loginBP;
    public BorderPane regBP;
    public Button btSendMsg;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Stage stage;
    BorderPane chatPane;
    BorderPane BPChat;
    private Text regStatus;
    private TextField tfRegLogin;
    private TextField tfRegNickname;
    private TextField tfRegPassword;
    private String nickname;

    // Создание сцены с чатом
    public BorderPane crChatPane() {

        listClient = new ListView<>();
        clientListMouseReleased(listClient);
        dialogArea = new TextArea();

        chatPane = new BorderPane();

        btSendMsg = new Button("Send");
        btSendMsg.setOnAction(e -> sendMsg());

        msgField = new TextField();
        msgField.setOnAction(event -> sendMsg());
        msgField.setPrefSize(700, 10);
        btSendMsg.setMaxSize(200, 10);

        ButtonBar bottomBar = new ButtonBar();

        bottomBar.getButtons().addAll(btSendMsg, msgField);
        chatPane.setLeft(listClient);
        chatPane.setCenter(dialogArea);
        chatPane.setBottom(bottomBar);

        return chatPane;
    }

    // Создание сцены с авторизацией и регистрацией
    public Pane crAuthPane() {

        authPane = new Pane();
        authPane.setPrefSize(300, 400);
        loginBP = crLoginBP();
        regBP = crRegBP();
        authPane.getChildren().addAll(loginBP, regBP);
        return authPane;
    }

    // Создание сцены с регистрацией
    private BorderPane crRegBP() {
        BorderPane regBP = new BorderPane();
        regBP.setPrefSize(300, 300);

        tfRegLogin = new TextField();

        tfRegNickname = new TextField();
        tfRegNickname.setPromptText("Press Nickname");

        tfRegPassword = new PasswordField();
        tfRegLogin.setPromptText("Press Login");

        tfRegPassword.setPromptText("Press Password");
        Button btReg = new Button("Registration");
        Button btBack = new Button("Back");
        btReg.setPrefSize(100, 20);
        btBack.setPrefSize(100, 20);
        regStatus = new Text();

        setActionReg(btReg);
        setActionBtnBack(btBack);

        GridPane gridCenter = new GridPane();
        GridPane gridBottom = new GridPane();

        gridCenter.setAlignment(Pos.CENTER);
        gridBottom.setAlignment(Pos.CENTER);

        gridCenter.add(regStatus, 0, 0);
        gridCenter.add(tfRegLogin, 0, 1);
        gridCenter.add(tfRegNickname, 0, 2);
        gridCenter.add(tfRegPassword, 0, 3);
        gridBottom.add(btReg, 0, 1);
        gridBottom.add(btBack, 0, 2);

        regBP.setBottom(gridBottom);
        regBP.setCenter(gridCenter);
        regBP.setVisible(false);

        return regBP;
    }

    // Создание сцены логинизации
    private BorderPane crLoginBP() {

        BorderPane loginBP = new BorderPane();
        loginBP.setPrefSize(300, 300);

        tfLogin = new TextField();
        tfPassword = new PasswordField();

        tfLogin.setPromptText("Login");
        tfPassword.setPromptText("Password");

        Button btLogin = new Button("Login");
        Button btReg = new Button("Registration");
        btLogin.setPrefSize(100, 20);
        btReg.setPrefSize(100, 20);

        setActionBtnReg(btReg);

        btLogin.setOnAction(e -> {
            tryToAuth();
        });

        GridPane gridCenter = new GridPane();
        GridPane gridBottom = new GridPane();
        gridCenter.setAlignment(Pos.CENTER);
        gridBottom.setAlignment(Pos.CENTER);

        gridCenter.add(tfLogin, 0, 1);
        gridCenter.add(tfPassword, 0, 2);
        gridBottom.add(btLogin, 0, 1);
        gridBottom.add(btReg, 0, 2);

        loginBP.setBottom(gridBottom);
        loginBP.setCenter(gridCenter);

        return loginBP;
    }

    // установка активнсоти для кнопки регистрации
    private void setActionBtnReg(Button btnReg) {
        // смена отображения сцены
        btnReg.setOnAction(event -> {
            loginBP.setVisible(false);
            regBP.setVisible(true);
        });
    }

    // установка активности кнопки назад
    private void setActionBtnBack(Button btnBack) {
        // смена отображения сцены
        btnBack.setOnAction(event -> {
            loginBP.setVisible(true);
            regBP.setVisible(false);
        });
    }

    // смена сцены на чат
    public void switchToChatPane() {
        BPChat = crChatPane();
        stage = (Stage) authPane.getScene().getWindow();
        stage.setScene(new Scene(BPChat));
    }

    //
    private void logout() {
        stage = (Stage) authPane.getScene().getWindow();
        stage.setOnCloseRequest(event -> {

            if (socket != null && !socket.isClosed()) {
                try {
                    out.writeUTF(ClientCommands.logout);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void connectUser() {
        try {
            socket = new Socket("localhost", 8188);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            userAuth();
                            userStrategy();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void userStrategy() {
        try {
            while (true) {
                String str = in.readUTF();

                //если в стр имеет / то выполняем команду иначе добавляем текст
                if (str.startsWith("/")) {
                    if (str.startsWith(ServerCommands.authSuc)) {
                        String[] token = str.split("\\s");
                        nickname = token[1];
                        Platform.runLater(this::switchToChatPane);
                        break;
                    }

                    if (str.equals(ClientCommands.logout)) {
                        // процедура выхода
                        Platform.runLater(this::logout);
                        break;
                    }

                    if (str.startsWith(ServerCommands.clientList)) {

                        // разделим на токены
                        String[] token = str.split("\\s");

                        Platform.runLater(() -> {

                            listClient.getItems().clear();

                            for (int i = 1; i < token.length; i++) {
                                listClient.getItems().add(token[i]);
                            }

                        });
                    }
                } else {
                    dialogArea.appendText(str + "\n");
                }
            }

        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
        } finally {
            Platform.runLater(() -> {
                switchToChatPane();
            });
            try {
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void userAuth() {
        try {

            while (true) {
                String str = in.readUTF();

                //если в стр имеет / то выполняем команду иначе добавляем текст
                if (str.startsWith("/")) {

                    if (str.startsWith(ServerCommands.clientList)) {

                        // разделим на токены
                        String[] token = str.split("\\s");

                        Platform.runLater(() -> {

                            listClient.getItems().clear();

                            for (int i = 1; i < token.length; i++) {
                                listClient.getItems().add(token[i]);
                            }
                            dialogArea.appendText(str + "\n");
                        });
                    }

                    if (str.equals(ClientCommands.logout)) {
                        throw new RuntimeException("Client disconnected");
                    }

                    if (str.startsWith(ServerCommands.authSuc)) {
                        String[] token = str.split("\\s");
                        nickname = token[1];
                        Platform.runLater(this::switchToChatPane);
                        break;
                    }

                    if (str.equals(ServerCommands.regSuc)) {
                        setResultTryToReg(ServerCommands.regSuc);
                    }

                    if (str.equals(ServerCommands.regFal)) {
                        setResultTryToReg(ServerCommands.regFal);
                    }

                } else {
                    Platform.runLater(() -> {
                        dialogArea.appendText(str + "\n");
                    });
                }
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    // отправка сообщения
    public void sendMsg() {

        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth() {
        if (socket == null || socket.isClosed()) {
            connectUser();
        }

        try {
            System.out.println("логин:" + tfLogin.getText());
            System.out.println(tfPassword.getText());
            out.writeUTF(String.format("%s %s %s", ClientCommands.auth, tfLogin.getText().trim(), tfPassword.getText().trim()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // выбор собеседника
    public void clientListMouseReleased(ListView listClient) {
        listClient.setOnMouseReleased(event -> {
            System.out.println(listClient.getSelectionModel().getSelectedItem());
            String msg = String.format("%s %s ", ClientCommands.priv, listClient.getSelectionModel().getSelectedItem());
            msgField.setText(msg);
        });
    }


    public void registration(String login, String password, String nickname) {
        if (socket == null || socket.isClosed()) {
            connectUser();
        }
        try {
            out.writeUTF(String.format("%s %s %s %s", ClientCommands.reg, login, password, nickname));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setResultTryToReg(String command) {
        if (command.equals(ServerCommands.regSuc)) {
            regStatus.setText("Registration was successful\n");
        }
        if (command.equals(ServerCommands.regFal)) {
            regStatus.setText("Login already taken\n");
        }
    }

    // регистрация пользователя
    public void setActionReg(Button btnReg) {
        btnReg.setOnAction(event -> {
            String login = tfRegLogin.getText().trim();
            String password = tfRegPassword.getText().trim();
            String nickname = tfRegNickname.getText().trim();

            if (login.length() * password.length() * nickname.length() == 0) {
                return;
            }

            registration(login, password, nickname);
        });
    }
}
