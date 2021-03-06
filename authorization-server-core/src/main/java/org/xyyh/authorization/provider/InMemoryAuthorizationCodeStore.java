package org.xyyh.authorization.provider;

import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2AuthorizationCode;
import org.xyyh.authorization.core.OAuth2AuthorizationCodeStore;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAuthorizationCodeStore implements OAuth2AuthorizationCodeStore {

    private final Map<String, OAuth2AuthorizationCode> codeRepository = new ConcurrentHashMap<>();
    private final Map<String, OAuth2Authentication> authenticationRepository = new ConcurrentHashMap<>();

    @Override
    public OAuth2AuthorizationCode save(OAuth2AuthorizationCode code, OAuth2Authentication authentication) {
        String codeKey = code.getValue();
        codeRepository.put(codeKey, code);
        authenticationRepository.put(codeKey, authentication);
        return code;
    }

    @Override
    public Optional<OAuth2Authentication> consume(String code) {
        OAuth2AuthorizationCode authorizationCode = codeRepository.remove(code);
        OAuth2Authentication authentication = authenticationRepository.remove(code);
        if (!Objects.isNull(authorizationCode) && !Objects.isNull(authentication)
                && Instant.now().isBefore(authorizationCode.getExpiresAt())
        ) {
            return Optional.of(authentication);
        } else {
            return Optional.empty();
        }
    }
}
