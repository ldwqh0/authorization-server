package org.xyyh.authorization.core;

import java.io.Serializable;

public interface OAuth2ServerRefreshToken extends Serializable {
    String getTokenValue();
}
