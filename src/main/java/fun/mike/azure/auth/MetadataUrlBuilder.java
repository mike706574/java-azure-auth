package fun.mike.azure.auth;

public class MetadataUrlBuilder {
    public static String build(String tenantId) {
        return String.format("https://login.microsoftonline.com/%s/v2.0/.well-known/openid-configuration",
                             tenantId);
    }
}
