package server;

import commands.ServerCommands;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();;
    private AuthService authService;

    public Server() {

        Socket socket = null;
        ServerSocket server = null;

        authService = new AuthService();

        try {
            server = new ServerSocket(8188);

            while (true) {
                socket = server.accept();
                ClientHandler client = new ClientHandler(this, socket);
                System.out.println("Клиент "+ clients.size()+
                        " подключился: " + socket.getRemoteSocketAddress());
                new Thread(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // отправка  пользотелю
    public void privateMsg(ClientHandler sender, String receiver, String msg){
        String message = String.format("[ %s ] to [ %s ]: %s", sender.getNickname(), receiver, msg);
        for (ClientHandler c : clients) {
            if(c.getNickname().equals(receiver)){
                c.sendMsg(message);
                if(!c.equals(sender)){
                    sender.sendMsg(message);
                }
                return;
            }
        }
        sender.sendMsg("not found user: "+ receiver);
    }

    // добавление пользователя в лист пользователей
    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        System.out.println("Count users: "+ clients.size());
        broadcastClientList();
    }

    // удаление пользователя из листа пользователей
    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientList();
    }


    public AuthService getAuthService() {
        return authService;
    }

    public boolean isLoginAuthenticated(String login){
        for (ClientHandler c : clients) {
            if(c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public void broadcastMsg(ClientHandler sender, String msg){
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    public void broadcastClientList(){
        StringBuilder sb = new StringBuilder(ServerCommands.clientList);

        for (ClientHandler c : clients) {
            sb.append(" ").append(c.getNickname());
        }

        String msg = sb.toString();

        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }
}
