package org.xyyh.authorization.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 服务发现端点，可以自动发现服务
 */
@RequestMapping("/.wellknow")
public class ServerDiscoveryEndpoint {

    /**
     * @return
     * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html">https://openid.net/specs/openid-connect-discovery-1_0.html</a>
     */
    @GetMapping("openid-configuration")
    public String getOpenidConfiguration() {
        //TODO 待实现
        return "";
    }

    /**
     * @return
     * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-discovery-10“>https://tools.ietf.org/html/draft-ietf-oauth-discovery-10</a>
     */
    @GetMapping("oauth-authorization-server")
    public String getOs() {
        //TODO 待实现
        return "";
    }
}
