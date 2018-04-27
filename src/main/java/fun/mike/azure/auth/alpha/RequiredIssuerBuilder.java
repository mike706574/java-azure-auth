package fun.mike.azure.auth.alpha;

public class RequiredIssuerBuilder {
    public static String build(String tenantId) {
        return String.format("https://sts.windows.net/%s/",
                             tenantId);
    }
}
