<!DOCTYPE HTML>
<html>
<head>
    <title>OAuth2.0 Home Page</title>
    <link href="/static/css/style.css" rel="stylesheet" type="text/css" media="all"/>
    <link href='http://fonts.googleapis.com/css?family=Rokkitt' rel='stylesheet' type='text/css'>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link type="text/css" href="//g.alicdn.com/sd/ncpc/nc.css?t=1516894524024" rel="stylesheet"/>
    <script type="text/javascript" src="/static/js/jquery-1.7.min.js"></script>
    <script type="text/javascript" src="//g.alicdn.com/sd/ncpc/nc.js?t=1516894524024"></script>
    <script>
        var _hmt = _hmt || [];
        (function() {
            var hm = document.createElement("script");
            hm.src = "https://hm.baidu.com/hm.js?f11a728a6e58315ab0a8554150920f9f";
            var s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>
</head>
<body>
<div id="_umfp" style="display:inline;width:1px;height:1px;overflow:hidden"></div>
<div class="wrap">
    <!-- strat-contact-form -->
    <div class="contact-form clearfix">
        <!-- start-account -->
        <div class="account">
            <h2><a href="https://www.cartoonai.com/static/signup.html">Don' have an account? Sign Up!</a></h2>
            <div class="span"><a href="#"><img src="/static/images/facebook.png" alt=""/><i>Sign In with Facebook</i>
                <div class="clear"></div>
            </a></div>
            <div class="span1"><a href="#"><img src="/static/images/twitter.png" alt=""/><i>Sign In with Twitter</i>
                <div class="clear"></div>
            </a></div>
            <div class="span2"><a href="#"><img src="/static/images/gplus.png" alt=""/><i>Sign In with Google+</i>
                <div class="clear"></div>
            </a></div>
        </div>
        <!-- end-account -->
        <!-- start-form -->
        <form class="contact_form" action="/oauth2.0/v1/authorize" method="post" id="contact_form" name="contact_form">
            <input type="hidden" name="client_pbk" value="${client_pbk!''}"/>
            <input type="hidden" name="client_id" value="${client.client_id!''}"/>
            <input type="hidden" name="client_secret" value="${client.client_secret!''}"/>
            <input type="hidden" name="response_type" value="${client.response_type!''}"/>
            <input type="hidden" name="redirect_uri" value="${client.redirect_uri!''}"/>
            <input type="hidden" name="state" value="${client.state!''}"/>
            <input type="hidden" id="error" value="${error!''}"/>
            <h1>Login Into Your Account</h1>
            <ul class="clearfix">
                <li style="width: 300px">
                    <input type="text" class="textbox1" name="username" maxlength="15" value="${username!''}" placeholder="username"/>
                    <span class="form_hint">Enter Your Account</span>
                    <p><img src="/static/images/contact.png" alt=""></p>
                </li>
                <li style="width: 300px">
                    <input type="password" name="password" class="textbox2" maxlength="15" placeholder="password">
                    <p><img src="/static/images/lock.png" alt=""></p>
                </li>
            </ul>
            <div class="container" style="margin-left: 0px;margin-top: 8%;width: 100%;">
                <div class="ln">
                    <div id="dom_id"></div>
                </div>
            </div>
            <div class="clear"></div>

            <div class="remember-me-wrapper clearfix">
                <label class="checkbox"><input type="checkbox" name="checkbox" checked="checked"
                                               style="margin-top: 10px;"><i></i>Remember
                    me</label>
                <div class="forgot">
                    <a href="#">forgot password?</a>
                </div>
            </div>

            <div class="clear"></div>
        </form>
        <!-- end-form -->
        <div class="clear"></div>
    </div>
    <!-- end-contact-form -->
    <div class="footer">
        <p>developed by <a href="#">www.aielves.net</a></p>
    </div>
</div>
<script>
    var nc = new noCaptcha();
    var nc_appkey = 'FFFF000000000179AE04';
    var nc_scene = 'login';
    var nc_token = [nc_appkey, (new Date()).getTime(), Math.random()].join(':');
    var nc_option = {
        renderTo: '#dom_id',
        appkey: nc_appkey,
        scene: nc_scene,
        token: nc_token,
        callback: function (data) {// 校验成功回调
            jQuery.post("/oauth2.0/v1/jaq", "csessionid="+data.csessionid+"&sig="+data.sig+"&token="+nc_token+"&scene="+nc_scene, function (json) {
                if(json.code == "000000"){
                    jQuery("#contact_form").submit();
                }else{
                    alert("Authentication Failed");
                    return false;
                }
            });
        }
    };
    nc.init(nc_option);
    window.onload = function () {
        jQuery("#nc_1__scale_text").text("Sign In By Slide Block");
        if(jQuery("#error").val() != ''){
            alert("Authentication Failed");
        }
    }
</script>
</body>
</html>