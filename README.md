## 项目说明
这是一个针对OAuth2的实现
随着spring-security-oauth项目的过期，好像不太好找开源的oauth2,openid 服务了。
Spring 社区推出来新版本的[spring-authorization-server](https://github.com/spring-projects-experimental/spring-authorization-server)，但它的逻辑全部通过filter实现，感觉不是很舒服，（理论上所有的逻辑都可以通过filter来实现，还要servlet干什么？特别是这种有着较强逻辑的业务）。  

本着学习的目的，根据相关规范，我使用spring web构建了authorization-server,并在实现的过程中参考了：
* OpenID Connect server [https://connect2id.com/](https://connect2id.com/)
* spring-authorization-server [https://github.com/spring-projects-experimental/spring-authorization-server](https://github.com/spring-projects-experimental/spring-authorization-server)  
并在实现过程中，参考了相关的rfc文档：
* The OAuth 2.0 Authorization Framework https://tools.ietf.org/html/rfc6749
* OpenID Connect Core 1.0 incorporating errata set 1 https://openid.net/specs/openid-connect-core-1_0.html
*  OAuth 2.0 Token Revocation https://tools.ietf.org/html/rfc7009
* OAuth 2.0 Token Introspection https://tools.ietf.org/html/rfc7662
* OAuth 2.0 Authorization Server Metadata draft-ietf-oauth-discovery-10 https://tools.ietf.org/html/draft-ietf-oauth-discovery-10
* OpenID Connect Discovery 1.0 incorporating errata set 1 https://openid.net/specs/openid-connect-discovery-1_0.html

## 模块说明
* authorization-server-core 授权服务核心模块
* authorization-server-configuration 授权服务配置模块
##　测试用例
* samples/authorization-server 一个简单的授权服务器
* samples/resource-server 一个简单的资源服务器
* samples/client-server 一个简单的客户端程序(计划中)

## 项目的启动
这是一个标准的maven项目  
使用如下命令将基础模块安装到本地
```bash
mvn clean install -Dmaven.test.skip=true
```
依次使用如下命令启动测试服务器
```
cd samples/authorization-server
mvn clean spring-boot:run #启动authorization-server
......
其它服务器类似
```
接下来可以使用swagger等工具进行测试了

