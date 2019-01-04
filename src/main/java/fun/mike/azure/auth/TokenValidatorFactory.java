package fun.mike.azure.auth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

public class TokenValidatorFactory {
    public static TokenValidator build(String metadataUrl,
                                       String requiredIssuer,
                                       String requiredAudience,
                                       int jwksConnectTimeout,
                                       int jwksReadTimeout) {
        URL metadataURL;
        try {
            metadataURL = new URL(metadataUrl);
        } catch (MalformedURLException ex) {
            String message = String.format("OpenID provider metadata URL \"%s\" is malformed.",
                                           metadataUrl);
            throw new IllegalStateException(message);
        }

        String rawMetadata;
        try {
            rawMetadata = IOUtils.readInputStreamToString(metadataURL.openStream(),
                                                          StandardCharsets.UTF_8);
        } catch (IOException e) {
            String message = String.format("Failed to read OpenID provider metadata from URL \"%s\".",
                                           metadataUrl);
            throw new IllegalStateException(message);
        }

        OIDCProviderMetadata metadata;
        try {
            metadata = OIDCProviderMetadata.parse(rawMetadata);
        } catch (com.nimbusds.oauth2.sdk.ParseException e) {
            String message = String.format("Failed to parse OpenID provider metadata from URL \"%s\": %s Metadata: %s",
                                           metadataUrl,
                                           e.getMessage(),
                                           rawMetadata);
            throw new IllegalStateException(message);
        }

        URI jwksUri = metadata.getJWKSetURI();

        URL jwksUrl;

        try {
            jwksUrl = jwksUri.toURL();
        } catch (IllegalArgumentException | MalformedURLException e) {
            String message = String.format("Malformed JWKS URI \"%s\" retrieved from metadata: %s",
                                           jwksUri.toString(),
                                           e.getMessage());
            throw new IllegalStateException(message);
        }

        ResourceRetriever resourceRetriever =
                new DefaultResourceRetriever(jwksConnectTimeout,
                                             jwksReadTimeout,
                                             RemoteJWKSet.DEFAULT_HTTP_SIZE_LIMIT);

        JWKSource<SecurityContext> jwksSource = new RemoteJWKSet<>(jwksUrl, resourceRetriever);

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, jwksSource);
        jwtProcessor.setJWSKeySelector(keySelector);

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<SecurityContext>() {
            @Override
            public void verify(JWTClaimsSet claimsSet, SecurityContext context)
                    throws BadJWTException {

                super.verify(claimsSet, context);

                String audience = claimsSet.getAudience().get(0);
                if (!requiredAudience.equals(audience)) {
                    String message = String.format("Expected audience \"%s\" to be \"%s\".",
                                                   audience,
                                                   requiredAudience);
                    throw new BadJWTException(message);
                }

                String issuer = claimsSet.getIssuer();
                if (!requiredIssuer.equals(issuer)) {
                    String message = String.format("Expected issuer \"%s\" to be \"%s\".",
                                                   issuer,
                                                   requiredIssuer);
                    throw new BadJWTException(message);
                }
            }
        });

        return new TokenValidator(jwtProcessor);
    }
}
