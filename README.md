# azure-auth

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fun.mike/azure-auth/badge.svg)](https://maven-badges.herokuapp.com/maven-central/fun.mike/azure-auth)
[![Javadocs](https://www.javadoc.io/badge/fun.mike/azure-auth.svg)](https://www.javadoc.io/doc/fun.mike/azure-auth)

Azure auth functionality.

## Example

```java
import fun.mike.azure.auth.Authenticator;
import fun.mike.azure.auth.AuthenticationResult;

final String tenantId = "c834c34e-bbd3-4ea1-c2c2-51daeff91aa32";
final String clientId = "ae33c32e-d2f2-4992-a4b2-51d03e7c8677"

final String header = "Authorization eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.XbPfbIHMI6arZ3Y922BhjWgQzWXcXNrz0ogtVhfEd2o";

String header = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);

Authenticator authenticator = AuthenticatorFactory.build(tenantId, clientId);

AuthenticationResult result = authenticator.authenticate(header);

result.valid()
=> true

result.getClaims()
=> {sub=1234567890, name=John Doe, iat=1516239022} (java.util.Map<String, Object>)
```

## Build

[![CircleCI](https://circleci.com/gh/mike706574/java-azure-auth.svg?style=svg)](https://circleci.com/gh/mike706574/java-azure-auth)

## Copyright and License

This project is licensed under the terms of the Apache 2.0 license.
