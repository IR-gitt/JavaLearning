package server;

import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private class UserData {
        String login;
        String pass;
        String username;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.pass = password;
            this.username = nickname;
        }
    }

    private List<UserData> users;

    public AuthService() {
        users = new ArrayList<>();
        users.add(new UserData("user", "user", "user"));
        users.add(new UserData("admin", "admin", "admin"));

        for (int i = 1; i < 10; i++) {
            users.add(new UserData("login" + i, "pass" + i, "nick" + i));
        }
    }

    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if(user.login.equals(login) && user.pass.equals(password)){
                return user.username;
            }
        }

        return null;
    }


    public boolean registration(String login, String password, String nickname) {
        for (UserData user : users) {
            if(user.login.equals(login) || user.username.equals(nickname)){
                return false;
            }
        }

        users.add(new UserData(login, password, nickname));
        return true;
    }
}
