/*
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
(function () {
    jQuery.post("/user/hasValid", "", function (json) {
        if (json.code != "000000") {
            location.href = "/static/valid4forget.html";
        }
    });
    jQuery.post("/user/sumuse", "", function (result) {
        if (result.code == "000000") {
            jQuery("#rate").text(result.data.rate);
            jQuery("#sumuse").text(result.data.sumuse);
        }
    });
})();

function notopen() {
    jAlert("功能正在建设，敬请期待","友情提示")
    return;
}

function submitForgetForm() {
    var username = jQuery("#username").val();
    var password = jQuery("#password").val();
    var smscode = jQuery("#smscode").val();
    if (username == null || "" == username) {
        jAlert("请输入您的手机号码", "友情提示");
        return false;
    }
    if (password == null || "" == password) {
        jAlert("请输入您的新登录密码(6-15位)", "友情提示");
        return false;
    }
    if (smscode == null || "" == smscode) {
        jAlert("请输入您的短信验证码", "友情提示");
        return false;
    }
    jQuery.post("/user/forget", "username=" + username + "&password=" + password + "&smscode=" + smscode, function (result) {
        if (result.code == "000000") {
            jConfirm('更新密码已成功,是否前往登录?', '友情提示', function (r) {
                if (r == true) {
                    location.href = '/static/login.html';
                } else {
                    location.href = '/static/forget.html';
                }
            });
        } else {
            jAlert(result.msg, "友情提示");
        }
    });
}

function sendsms() {
    var mobile = jQuery("#username").val();
    if (mobile == "") {
        jAlert("手机号码不能为空", "友情提示");
        return false;
    }
    jQuery.post("/user/sendsms", "mobile=" + mobile, function (json) {
        if (json.code == "000000") {
            jAlert(json.data.result, "友情提示");
        } else if (json.code == "000108") {
            alert(json.msg);
            location.href = "/static/valid4forget.html";
        } else {
            jAlert(json.msg, "友情提示");
        }
    });
}