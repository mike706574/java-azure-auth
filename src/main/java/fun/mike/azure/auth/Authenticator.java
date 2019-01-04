package fun.mike.azure.auth;

public class Authenticator {
    private final TokenValidator tokenValidator;


    public Authenticator(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    public AuthenticationResult authenticate(String header) {
        BearerTokenResult bearerTokenResult = BearerTokenParser.parse(header);

        if (bearerTokenResult.failed()) {
            return AuthenticationResult.invalid(bearerTokenResult.getMessage());
        }

        String token = bearerTokenResult.getToken();

        return tokenValidator.validate(token);
    }
}
