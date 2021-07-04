package mode;

/**
 * <pre>
 * Tip:
 *      用户登录成功后获得的Token
 *
 * Created by ACap on 2021/7/3 16:50
 * </pre>
 */
public class ModelUserToken {
    public String username;
    public String token;

    public ModelUserToken(String username, String token) {
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
