package fun.mike.azure.auth;

import java.util.Arrays;
import java.util.List;

public class BearerTokenParser {
    public static BearerTokenResult parse(String header) {
        if (header == null) {
            return BearerTokenResult.error("No \"Authorization\" header present.");
        }

        List<String> parts = Arrays.asList(header.split(" "));

        if (parts.size() != 2) {
            return BearerTokenResult.error("Malformed \"Authorization\" header.");
        }

        String scheme = parts.get(0);

        if (!"Bearer".equals(scheme)) {
            String message = String.format("Unexpected authentication scheme %s in the \"Authorization\" header; expected \"Bearer\".",
                                           scheme);
            return BearerTokenResult.error(message);
        }

        return BearerTokenResult.token(parts.get(1));
    }
}
