package fun.mike.azure.auth;

public class Authenticator {
    private final String tenantId;
    private final String clientId;

    public Authenticator(String tenantId, String clientId) {
        this.tenantId = tenantId;
        this.clientId = clientId;
    }

    public AuthenticationResult authenticate(String header) {
        BearerTokenResult bearerTokenResult = BearerTokenParser.parse(header);

        if (bearerTokenResult.failed()) {
            return AuthenticationResult.invalid(bearerTokenResult.getMessage());
        }

        JwksUrlResult jwksUrlResult = JwksUrlFetcher.fetch(tenantId);

        if (jwksUrlResult.failed()) {
            return AuthenticationResult.failed(jwksUrlResult.getMessage());
        }

        String token = bearerTokenResult.getToken();
        String jwksUrl = jwksUrlResult.getUrl();

        return TokenValidator.validate(tenantId, clientId, jwksUrl, token);
    }
}
