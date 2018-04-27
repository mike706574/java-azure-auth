package fun.mike.azure.auth.alpha;

public class AuthenticatorFactory {
    public static Authenticator build(String tenantId, String clientId) {
        String metadataUrl = MetadataUrlBuilder.build(tenantId);
        String requiredIssuer = RequiredIssuerBuilder.build(tenantId);
        TokenValidator tokenValidator = TokenValidatorFactory.build(metadataUrl,
                                                                    requiredIssuer,
                                                                    clientId);
        return new Authenticator(tokenValidator);
    }
}
