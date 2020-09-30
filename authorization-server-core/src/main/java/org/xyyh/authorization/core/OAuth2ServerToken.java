package org.xyyh.authorization.core;

import java.io.Serializable;
import java.time.Instant;

public interface OAuth2ServerToken extends Serializable {
    String getTokenValue();

    Instant getIssuedAt();

    Instant getExpiresAt();
}
