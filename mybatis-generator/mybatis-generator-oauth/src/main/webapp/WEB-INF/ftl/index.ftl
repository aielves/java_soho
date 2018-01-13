<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>SOHO科技 - 用户登录</title>
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
            <h1 class="logo">OAUTH.<span>CAS认证系统</span></h1>
            <span class="slogan">一直在超越,从未被模仿</span>
        </div><!--logo-->

        <br clear="all"/><br/>

        <div class="nousername">
            <div class="loginmsg" id="nousername">${errorMsg!""}</div>
        </div><!--nousername-->

        <div class="nopassword">
            <div class="loginmsg">密码不正确.</div>
            <div class="loginf">
                <div class="thumb"><img alt="" src="/static/images/avatar1.png"/></div>
                <div class="userlogged">
                    <h4></h4>
                    <a href="index.html">Not <span></span>?</a>
                </div>
            </div><!--loginf-->
        </div><!--nopassword-->

        <form id="login" action="/oauth/login" method="post">
            <input type="hidden" name="client_id" value="${client.client_id}"/>
            <input type="hidden" name="redirect_uri" value="${client.redirect_uri}"/>
            <input type="hidden" name="response_type" value="${client.response_type}"/>
            <div class="username">
                <div class="usernameinner">
                    <input type="text" name="username" id="username" value="${username!''}"/>
                </div>
            </div>

            <div class="password">
                <div class="passwordinner">
                    <input type="password" name="password" id="password" value="${password!''}"/>
                </div>
            </div>

            <button>登录</button>

            <div class="keep"><input type="checkbox" /> 记住密码?</div>

        </form>

    </div><!--loginboxinner-->
</div><!--loginbox-->


</body>
</html>
