/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.soho.codegen.shiro.aconst;

/**
 * Created by Administrator on 2017/4/30.
 */
public interface BizErrorCode {

    public static String OAUTH_CLIENT_ID_ERROR = "000101"; // client_id参数错误
    public static String OAUTH_LOGIN_ERROR = "000102"; // OAUTH登录失败
    public static String OAUTH_REDIRECT_URI_ERROR = "000103"; // redirect_uri参数错误
    public static String OAUTH_CLIENT_SECRET_ERROR = "000104"; // client_secret参数错误
    public static String OAUTH_AUTHORIZE_CODE_ERROR = "000105"; // 授权码错误
    public static String OAUTH_AUTHORIZE_CODE_TIMEOUT = "000106"; // 授权码失效
    public static String OAUTH_TOKEN_CODE_ERROR = "000107"; // TOKEN验证无效

}
