<#--

       Copyright ${license.git.copyrightYears} the original author or authors.

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

-->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>MFX Sign Up Page</title>
    <link rel="stylesheet" href="/static/css/style.default.css" type="text/css"/>
    <script type="text/javascript" src="/static/js/plugins/jquery-1.7.min.js"></script>
    <script type="text/javascript" src="/static/js/plugins/jquery-ui-1.8.16.custom.min.js"></script>
    <script type="text/javascript" src="/static/js/plugins/jquery.cookie.js"></script>
    <script type="text/javascript" src="/static/js/plugins/jquery.uniform.min.js"></script>
    <script type="text/javascript" src="/static/js/custom/general.js"></script>
    <script type="text/javascript" src="/static/js/custom/index.js"></script>
    <!--[if IE 9]>
    <link rel="stylesheet" media="screen" href="/static/css/style.ie9.css"/>
    <![endif]-->
    <!--[if IE 8]>
    <link rel="stylesheet" media="screen" href="/static/css/style.ie8.css"/>
    <![endif]-->
    <!--[if lt IE 9]>
    <script src="/static/js/plugins/css3-mediaqueries.js"></script>
    <![endif]-->
    <script type="text/javascript">
        jQuery(function () {
            if (jQuery("#nousername").html() != "") {
                jQuery(".nousername").show();
            }
        });
    </script>
</head>

<body class="loginpage">
<div class="loginbox">
    <div class="loginboxinner">

        <div class="logo">
            <h1 class="logo">OAUTH.<span>SIGN UP</span></h1>
            <span class="slogan">Please input your information</span>
        </div><!--logo-->

        <form id="login" action="/mfxuser/app/reg" method="post">
            <input type="hidden" name="refeId" value="${refeId!''}"/>
            <div class="username">
                <div class="usernameinner">
                    <input type="text" name="username" id="username" />
                </div>
            </div>

            <div class="password">
                <div class="passwordinner">
                    <input type="password" name="passwd" id="password" />
                </div>
            </div>

            <div class="username">
                <div class="usernameinner">
                    <input type="text" name="nickname" id="nickname" />
                </div>
            </div>

            <div class="username">
                <div class="usernameinner">
                    <input type="text" name="mobile" id="mobile" />
                </div>
            </div>

            <button>submit</button>

        <#--<div class="keep"><input type="checkbox"/> 记住密码</div>-->

        </form>

    </div><!--loginboxinner-->
</div><!--loginbox-->


</body>
</html>
