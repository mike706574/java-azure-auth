package fun.mike.azure.auth;

public class RequiredIssuerBuilder {
    public static String build(String tenantId) {
        return String.format("https://sts.windows.net/%s/",
                             tenantId);
    }
}
