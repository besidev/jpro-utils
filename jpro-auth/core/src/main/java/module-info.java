/**
 * Module descriptor for the Auth Core module.
 *
 * @author Besmir Beqiri
 */
module one.jpro.platform.auth.core {
    requires javafx.graphics;
    requires org.jetbrains.annotations;
    requires java.net.http;
    requires jpro.webapi;
    requires jwks.rsa;
    requires com.auth0.jwt;
    requires one.jpro.platform.internal.openlink;

    requires transitive org.json;
    requires transitive org.slf4j;

    opens one.jpro.platform.auth.core;

    exports one.jpro.platform.auth.core;
    exports one.jpro.platform.auth.core.api;
    exports one.jpro.platform.auth.core.authentication;
    exports one.jpro.platform.auth.core.jwt;
    exports one.jpro.platform.auth.core.oauth2;
    exports one.jpro.platform.auth.core.oauth2.provider;
    exports one.jpro.platform.auth.core.http;
    exports one.jpro.platform.auth.core.utils;
}