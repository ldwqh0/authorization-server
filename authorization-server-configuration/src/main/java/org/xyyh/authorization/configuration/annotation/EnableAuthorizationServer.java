package org.xyyh.authorization.configuration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import org.xyyh.authorization.configuration.AuthorizationServerConfiguration;
import org.xyyh.authorization.configuration.AuthorizationServerSecurityConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ AuthorizationServerSecurityConfiguration.class, AuthorizationServerConfiguration.class })
public @interface EnableAuthorizationServer {

}
