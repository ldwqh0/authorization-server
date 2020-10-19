package org.xyyh.authorization.core;

import java.util.Map;
import java.util.Optional;

public interface OAuth2TokenIntrospectionService {
    Optional<Map<String, Object>> inspectAccessToken(String token);

    Optional<Map<String, Object>> inspectRefreshToken(String token);
}
