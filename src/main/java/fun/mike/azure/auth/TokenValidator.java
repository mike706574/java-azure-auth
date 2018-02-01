package fun.mike.azure.auth;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

public class TokenValidator {
    public static AuthenticationResult validate(String tenantId, String clientId, String jwksUrl, String token) {
        JWKSource<SecurityContext> jwksSource = null;
        try {
            jwksSource = new RemoteJWKSet<>(new URL(jwksUrl));
        } catch (MalformedURLException ex) {
            String message = String.format("JWKS URL \"%s\" retrieved from OpenID provider is malformed.",
                                           jwksUrl);
            return AuthenticationResult.failed(message);
        }

        return validate(tenantId, clientId, token, jwksSource);
    }

    private static AuthenticationResult validate(String tenantId, String clientId, String token, JWKSource<SecurityContext> jwksSource) {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, jwksSource);
        jwtProcessor.setJWSKeySelector(keySelector);

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<SecurityContext>() {
            @Override
            public void verify(JWTClaimsSet claimsSet)
                    throws BadJWTException {

                super.verify(claimsSet);

                if (claimsSet.getExpirationTime() == null) {
                    throw new BadJWTException("Missing required token expiration claim.");
                }

                String subject = claimsSet.getSubject();
                if (clientId.equals(claimsSet.getSubject())) {
                    String message = String.format("Expected subject \"%s\" to be \"%s\".",
                                                   clientId,
                                                   subject);
                    throw new BadJWTException(message);
                }

                String expectedIssuer = String.format("https://sts.windows.net/%s/",
                                                      tenantId);

                if (!expectedIssuer.equals(claimsSet.getIssuer())) {
                    String message = String.format("Expected issuer \"%s\" to be \"%s\".",
                                                   clientId,
                                                   expectedIssuer);
                    throw new BadJWTException(message);
                }
            }
        });

        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            return AuthenticationResult.valid(claimsSet.getClaims());
        } catch (ParseException | JOSEException | BadJOSEException ex) {
            return AuthenticationResult.invalid(ex.getMessage());
        }
    }
}
