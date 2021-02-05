package Lesson3.server;

import Lesson3.DB.ConnectionService;

public final class AuthenticationService {

    public AuthenticationService() {
    }

    public boolean findNicknameByLoginAndPassword(String login, String password) {
        boolean find = ConnectionService.selectUserExist(login, password);
        return find;
    }

}