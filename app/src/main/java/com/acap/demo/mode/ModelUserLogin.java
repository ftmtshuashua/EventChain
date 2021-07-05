package com.acap.demo.mode;

/**
 * <pre>
 * Tip:
 *      登录信息
 *
 * Created by ACap on 2021/7/3 16:50
 * </pre>
 */
public class ModelUserLogin {
    public String username;
    public String token;

    public ModelUserLogin(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public String toString() {
        return "ModelUserLoginToken{" +
                "username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
