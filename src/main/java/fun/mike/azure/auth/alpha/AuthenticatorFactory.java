package fun.mike.azure.auth.alpha;

public class AuthenticatorFactory {
    public static Authenticator build(String tenantId, String clientId, int jwksConnectTimeout, int jwksReadTimeout) {
        String metadataUrl = MetadataUrlBuilder.build(tenantId);
        String requiredIssuer = RequiredIssuerBuilder.build(tenantId);
        TokenValidator tokenValidator = TokenValidatorFactory.build(metadataUrl,
                                                                    requiredIssuer,
                                                                    clientId,
                                                                    jwksConnectTimeout,
                                                                    jwksReadTimeout);
        return new Authenticator(tokenValidator);
    }
}
