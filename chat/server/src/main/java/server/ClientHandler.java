package server;

import commands.ClientCommands;
import commands.ServerCommands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler implements Runnable {
    private Server server;
    private Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String nickname;
    private String login;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // реализация клиента
    private void clientStrategy() {
        try {
            while (true) {
                String str = in.readUTF();
                System.out.println(str);
                // команда выхода
                if (str.startsWith("/")) {
                    if (str.equals(ClientCommands.logout)) {
                        out.writeUTF(ClientCommands.logout);
                        break;
                    }

                    // приватый чат с пользователем
                    if (str.startsWith(ClientCommands.priv)) {
                        String[] token = str.split("\\s", 3);
                        if (token.length < 3) {
                            continue;
                        }
                        server.privateMsg(this, token[1], token[2]);
                    }

                    if (str.startsWith(ClientCommands.auth)) {
                        String[] token = str.split("\\s", 3);
                        if (token.length < 3) {
                            continue;
                        }

                        String newNick = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);

                        login = token[1];

                        System.out.println("New user: newNick");
                        //без проверки для добавления юзера
                        if (newNick != null) {
                            if (!server.isLoginAuthenticated(login)) {
                                nickname = newNick;
                                sendMsg(ServerCommands.authSuc + " " + nickname);

                                // добавление clientHandler в clientHandlerList
                                server.subscribe(this);
                                break;
                            }
                        } else {
                            sendMsg("Учетная запись уже используется");
                        }
                    }
                } else {
                    sendMsg("Incorrect data");
                }
            }
        } catch (SocketTimeoutException | RuntimeException e) {
           e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // удаление clientHandler в clientHandlerList
            server.unsubscribe(this);
            System.out.println("Client disconnected: " + nickname);
            try {
                server.unsubscribe(this);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // авторизация клиента
    private void clientAuth() {
        try {

            while (true) {
                String str = in.readUTF();
                System.out.println(str);
                if (str.startsWith("/")) {
                    // команда отключиться
                    if (str.equals(ClientCommands.logout)) {
                        out.writeUTF(ClientCommands.logout);
                        break;
                    }

                    // команда авторизация
                    if (str.startsWith(ClientCommands.auth)) {
                        String[] token = str.split("\\s", 3);
                        if (token.length < 3) {
                            continue;
                        }

                        String newNick = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);

                        login = token[1];

                        System.out.println(newNick);

                        if (newNick != null) {
                            if (!server.isLoginAuthenticated(login)) {
                                nickname = newNick;
                                sendMsg(ServerCommands.authSuc + " " + nickname);
                                server.subscribe(this);
                                System.out.println("client: " + socket.getRemoteSocketAddress() + " connected with nick: " + nickname);
                                break;
                            } else {
                                sendMsg("The account is already in use");
                            }
                        } else {
                            sendMsg("Incorrect login/password");
                        }
                    }

                    // команда регистрация
                    if (str.startsWith(ClientCommands.reg)) {

                        String[] token = str.split("\\s", 4);
                        if (token.length < 4) {
                            continue;
                        }

                        boolean regSuccess = server.getAuthService().registration(token[1], token[2], token[3]);
                        if (regSuccess) {
                            sendMsg(ServerCommands.regSuc);
                        } else {
                            sendMsg(ServerCommands.regFal);
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // отправка сообщения на сервер
    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
    // для клиента создание п поток
    @Override
    public void run() {

        //процесс авторизации
        clientAuth();

        //процесс работы
        clientStrategy();
    }
}
