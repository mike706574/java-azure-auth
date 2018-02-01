package fun.mike.azure.auth;

import java.util.Map;

public class AuthenticationResult {
    private final boolean failed;
    private final boolean valid;

    private final String message;
    private final Map<String, Object> claims;

    public AuthenticationResult(boolean failed,
            boolean valid,
            String message,
            Map<String, Object> claims) {
        this.failed = failed;
        this.valid = valid;
        this.message = message;
        this.claims = claims;
    }

    public static AuthenticationResult valid(Map<String, Object> claims) {
        return new AuthenticationResult(false, true, null, claims);
    }

    public static AuthenticationResult invalid(String message) {
        return new AuthenticationResult(false, false, message, null);
    }

    public static AuthenticationResult failed(String message) {
        return new AuthenticationResult(true, false, message, null);
    }

    public boolean failed() {
        return failed;
    }

    public boolean valid() {
        return valid;
    }

    public boolean invalid() {
        return !valid;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }
}
