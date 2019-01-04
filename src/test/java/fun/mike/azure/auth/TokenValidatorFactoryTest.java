package fun.mike.azure.auth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fun.mike.io.alpha.IO;

import static org.junit.Assert.assertNotNull;

public class TokenValidatorFactoryTest {
    private static final String base = "src/test/resources/";

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void malformedMetadataUrl() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("OpenID provider metadata URL \"kjanejkgea\" is malformed.");

        String tenantId = "c834c34e-bbd3-4ea1-c2c2-51daeff91aa32";
        String clientId = "ae33c32e-d2f2-4992-a4b2-51d03e7c8677";
        String metadataUrl = "kjanejkgea";
        String requiredIssuer = RequiredIssuerBuilder.build(tenantId);
        TokenValidatorFactory.build(metadataUrl,
                                    requiredIssuer,
                                    clientId,
                                    5,
                                    5);
    }

    @Test
    public void metadataNotAvailable() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Failed to read OpenID provider metadata from URL \"http://localhost:9100\".");

        String tenantId = "c834c34e-bbd3-4ea1-c2c2-51daeff91aa32";
        String clientId = "ae33c32e-d2f2-4992-a4b2-51d03e7c8677";
        String metadataUrl = "http://localhost:9100";
        String requiredIssuer = RequiredIssuerBuilder.build(tenantId);
        TokenValidatorFactory.build(metadataUrl,
                                    requiredIssuer,
                                    clientId,
                                    5,
                                    5);
    }

    @Test
    public void metadataMalformed() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Failed to parse OpenID provider metadata from URL \"http://localhost:9100/\": Invalid JSON: Unexpected token lekanaw at position 7. Metadata: lekanaw");

        EchoServer.with(9100, "lekanaw", () -> {
                String tenantId = "c834c34e-bbd3-4ea1-c2c2-51daeff91aa32";
                String clientId = "ae33c32e-d2f2-4992-a4b2-51d03e7c8677";
                String metadataUrl = "http://localhost:9100/";
                String requiredIssuer = RequiredIssuerBuilder.build(tenantId);
                TokenValidatorFactory.build(metadataUrl,
                                            requiredIssuer,
                                            clientId,
                                            5,
                                            5);
            });
    }

    @Test
    public void malformedJwksUri() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Malformed JWKS URI \"lekwaela\" retrieved from metadata: URI is not absolute");

        String metadata = IO.slurp(base + "metadata-with-malformed-jwks-uri.json");

        EchoServer.with(9100, metadata, () -> {
                String tenantId = "c834c34e-bbd3-4ea1-c2c2-51daeff91aa32";
                String clientId = "ae33c32e-d2f2-4992-a4b2-51d03e7c8677";
                String metadataUrl = "http://localhost:9100";
                String requiredIssuer = RequiredIssuerBuilder.build(tenantId);
                TokenValidator tokenValidator = TokenValidatorFactory.build(metadataUrl,
                                                                            requiredIssuer,
                                                                            clientId,
                                                                            5,
                                                                            5);
                assertNotNull(tokenValidator);
            });
    }

    @Test
    public void success() {
        String metadata = IO.slurp(base + "local-metadata.json");

        EchoServer.with(9100, metadata, () -> {
                String tenantId = "c834c34e-bbd3-4ea1-c2c2-51daeff91aa32";
                String clientId = "ae33c32e-d2f2-4992-a4b2-51d03e7c8677";
                String metadataUrl = "http://localhost:9100";
                String requiredIssuer = RequiredIssuerBuilder.build(tenantId);
                TokenValidator tokenValidator = TokenValidatorFactory.build(metadataUrl,
                                                                            requiredIssuer,
                                                                            clientId,
                                                                            5,
                                                                            5);
                assertNotNull(tokenValidator);
            });
    }
}
