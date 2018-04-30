package fun.mike.azure.auth.alpha;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fun.mike.io.alpha.IO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Ignore
public class TokenValidatorTest {
    private static final String base = "src/test/resources/";

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void jwksNotAvailable() {
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

                String token = "ABCD";
                AuthenticationResult result = tokenValidator.validate(token);

                assertTrue(result.failed());
                assertEquals("Couldn't retrieve remote JWK set: Connection refused (Connection refused)",
                             result.getMessage());
            });
    }
}
