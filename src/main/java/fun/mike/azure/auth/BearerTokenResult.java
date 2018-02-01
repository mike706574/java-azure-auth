package fun.mike.azure.auth;

public class BearerTokenResult {
    private final String token;
    private final String message;

    private BearerTokenResult(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public static BearerTokenResult token(String token) {
        return new BearerTokenResult(token, null);
    }

    public static BearerTokenResult error(String message) {
        return new BearerTokenResult(null, message);
    }

    public boolean failed() {
        return token == null;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
