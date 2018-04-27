package fun.mike.azure.auth.alpha;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

public class TokenValidator {
    private final ConfigurableJWTProcessor<SecurityContext> processor;

    public TokenValidator(ConfigurableJWTProcessor<SecurityContext> processor) {
        this.processor = processor;
    }

    public AuthenticationResult validate(String token) {
        try {
            JWTClaimsSet claimsSet = processor.process(token, null);
            return AuthenticationResult.valid(claimsSet.getClaims());
        }
        catch(RemoteKeySourceException ex) {
            return AuthenticationResult.failed(ex.getMessage());
        }
        catch (ParseException | JOSEException | BadJOSEException ex) {
            return AuthenticationResult.invalid(ex.getMessage());
        }

    }
}
