package fun.mike.azure.auth;

public class JwksUrlResult {
    private final String url;
    private final String message;

    private JwksUrlResult(String token, String message) {
        this.url = token;
        this.message = message;
    }

    public static JwksUrlResult url(String url) {
        return new JwksUrlResult(url, null);
    }

    public static JwksUrlResult error(String message) {
        return new JwksUrlResult(null, message);
    }

    public boolean failed() {
        return url == null;
    }

    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
    }
}
