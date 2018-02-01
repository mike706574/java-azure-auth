package fun.mike.azure.auth;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JwksUrlFetcher {
    public static JwksUrlResult fetch(String tenantId) {
        URL metadataURL = null;

        String metadataPath = String.format("https://login.microsoftonline.com/%s/v2.0/.well-known/openid-configuration",
                                            tenantId);
        try {
            metadataURL = new URL(metadataPath);
        } catch (MalformedURLException ex) {
            String message = String.format("OpenID provider metadata URL \"%s\" is malformed.",
                                           metadataPath);
            return JwksUrlResult.error(message);
        }

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);


        JSONObject metadata = null;
        try (InputStream metadataStream = metadataURL.openStream()) {
            metadata = (JSONObject) parser.parse(metadataStream);

            if (metadata.containsKey("jwks_uri")) {
                return JwksUrlResult.url((String) metadata.get("jwks_uri"));
            }

            String message = String.format("No \"jwks_uri\" property present in OpenID provider metadata retrieved from \"%s\".",
                                           metadataPath);
            return JwksUrlResult.error(message);
        } catch (IOException | ClassCastException | ParseException ex) {
            String message = String.format("Failed to parse OpenID provider metadata from \"%s\".",
                                           metadataURL);
            return JwksUrlResult.error(message);
        }
    }
}
