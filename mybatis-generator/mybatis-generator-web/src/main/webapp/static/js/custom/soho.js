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
jQuery(function () {

});
var islogin = false;

function initMsg() {
    jQuery.post("/codegen/getUser", "", function (result) {
        if (result.code == "000000") {
            jQuery("#loginName").html(result.data.nickname + "&nbsp;&nbsp;<a href='/user/logout'>退出</a>");
            islogin = true;
        } else {
            var msg = "友情提示:生成功能需要登录后方可使用!";
            var position = "center";
            var scrollpos = jQuery(document).scrollTop();
            // if(scrollpos < 50) position = "customtop-right";
            jQuery.jGrowl(msg, {life: 10000, position: position});
            return false;
        }
    });
}

function getDBTable() {
    var dbType = jQuery("#dbType").val();
    var jdbcUrl = jQuery("#jdbcUrl").val();
    var username = jQuery("#username").val();
    var password = jQuery("#password").val();
    if (dbType == null || "" == dbType) {
        jAlert("请选择数据库类型", "友情提示");
        return false;
    }
    if (jdbcUrl == null || "" == jdbcUrl) {
        jAlert("请输入数据库地址", "友情提示");
        return false;
    }
    if (username == null || "" == username) {
        jAlert("请输入数据库帐号", "友情提示");
        return false;
    }
    if (password == null || "" == password) {
        jAlert("请输入数据库密码", "友情提示");
        return false;
    }
    jdbcUrl = encodeURI(jdbcUrl, "UTF-8");
    username = encodeURI(username, "UTF-8");
    password = encodeURI(password, "UTF-8");
    jQuery.post("/codegen/getDBTables", "dbType=" + dbType + "&jdbcUrl=" + jdbcUrl + "&username=" + username + "&password=" + password, function (result) {
        if (result.code == "000000") {
            jQuery("#tables").html("");
            var table_bf = jQuery("#table_bf");
            table_bf.html("");
            jQuery.each(result.data.tables, function (i, v) {
                table_bf.append("<option value='" + v + "'>" + v + "</option>")
            });
        } else {
            jAlert(result.msg, "友情提示");
            return false;
        }
    });
}

function submitMyForm() {
    if (!islogin) {
        jAlert("登录后方可使用该功能!", "友情提示");
        return;
    }
    jQuery("#sohoform").validate({
        submitHandler: function () {
            doSubmit();
        },
        rules: {
            jdbcUrl: "required",
            username: "required",
            password: "required",
            packageName: "required",
            dbType: "required",
            moduleName: "required"
        },
        messages: {
            jdbcUrl: "请输入数据库地址",
            username: "请输入数据库帐号",
            password: "请输入数据库密码",
            packageName: "请输入类包路径",
            dbType: "请选择数据库类型",
            moduleName: "至少选择一个模块"
        }
    });
}

var isSubmit = false;

function doSubmit() {
    if (isSubmit) {
        return;
    }
    isSubmit = true;
    jQuery("#submitbtn").text("正在生成...").attr("disabled", true);
    var moduleName = "";
    jQuery("input[name='moduleName']:checked").each(function () {
        moduleName += "," + jQuery(this).val();
    });
    var packageName = jQuery("#packageName").val();
    var dbType = jQuery("#dbType").val();
    var jdbcUrl = jQuery("#jdbcUrl").val();
    var username = jQuery("#username").val();
    var password = jQuery("#password").val();
    var tables = "";
    jQuery("#tables option").each(function () {
        tables += "," + jQuery(this).val();
    });
    if ("" == moduleName) {
        jAlert("至少选择一个模块", "友情提示");
        isSubmit = false;
        jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
        return false;
    } else {
        moduleName = moduleName.substring(1);
    }
    if ("" == packageName) {
        jAlert("请输入类包路径", "友情提示");
        isSubmit = false;
        jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
        return false;
    }
    if (dbType == null || "" == dbType) {
        jAlert("请选择数据库类型", "友情提示");
        isSubmit = false;
        jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
        return false;
    }
    if (jdbcUrl == null || "" == jdbcUrl) {
        jAlert("请输入数据库地址", "友情提示");
        isSubmit = false;
        jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
        return false;
    }
    if (username == null || "" == username) {
        jAlert("请输入数据库帐号", "友情提示");
        isSubmit = false;
        jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
        return false;
    }
    if (password == null || "" == password) {
        jAlert("请输入数据库密码", "友情提示");
        isSubmit = false;
        jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
        return false;
    }
    if ("" == tables) {
        jAlert("至少选择一个数据表", "友情提示");
        isSubmit = false;
        jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
        return false;
    } else {
        tables = tables.substring(1);
    }
    jdbcUrl = encodeURI(jdbcUrl, "UTF-8");
    username = encodeURI(username, "UTF-8");
    password = encodeURI(password, "UTF-8");
    jQuery.post("/codegen/generate", "dbType=" + dbType + "&jdbcUrl=" + jdbcUrl + "&username=" + username + "&password=" + password + "&moduleName=" + moduleName + "&packageName=" + packageName + "&tables=" + tables, function (result) {
        // alert(JSON.stringify(result));
        if (result.code == "000000") {
            jConfirm('已成功生成文件,是否查看文件列表?', '友情提示', function (r) {
                if (r == true) {
                    getFiles();
                    jQuery("#basicform_li").attr("class", "");
                    jQuery("#basicform").hide();
                    jQuery("#fileform_li").attr("class", "current");
                    jQuery("#fileform").show();
                }
                isSubmit = false;
                jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
            });
        } else {
            jAlert(result.msg, "友情提示");
            isSubmit = false;
            jQuery("#submitbtn").text("开始生成").removeAttr("disabled");
        }
    });
}

function getFiles() {
    jQuery("#tribune").hide();
    jQuery("#basicform").hide();
    if (!islogin) {
        alert("请先登录");
        return;
    }
    jQuery.post("/codegen/getZipFile", "", function (result) {
        // alert(JSON.stringify(result));
        if (result.code == "000000") {
            var body = jQuery("#fileTableBody");
            body.html("");
            var htmlStr = "";
            if (JSON.stringify(result.data) == "{}") {
                body.html("<tr><td colspan=\"7\" align=\"center\" class=\"center\">暂无数据</td></tr>");
            } else {
                jQuery.each(result.data.list, function (i, v) {
                    htmlStr += ("<tr id='tr_" + v.id + "'>");
                    htmlStr += "<td align=\"center\"><div class=\"checker\" id=\"uniform-undefined\"><span id='cbx_" + v.id + "_s1'><div class=\"checker\" id=\"uniform-undefined\"><span id='cbx_" + v.id + "_s2'><input id='cbx_" + v.id + "' name='fileId' value='" + v.id + "' style=\"opacity: 0;\" type=\"checkbox\" onclick=\"cbx_func('cbx_" + v.id + "')\"></span></div></span></div></td>";
                    htmlStr += ("<td width=\"18%\">" + v.dburl + "</td>");
                    var tableStr = v.tables;
                    if (tableStr.length > 100) {
                        tableStr = tableStr.substring(0, 100) + "...";
                    }
                    htmlStr += ("<td title='" + v.tables + "'>" + tableStr + "</td>");
                    htmlStr += ("<td width=\"8%\">" + v.fileName + "</td>");
                    htmlStr += ("<td width=\"8%\">" + v.fileSize + "</td>");
                    htmlStr += ("<td width=\"8%\">" + v.ctime + "</td>");
                    htmlStr += ("<td class=\"center\" width=\"12%\"><a class=\"btn btn_archive\" href=\"/codegen/downFile?fileId=" + v.id + "\"><span>下载</span></a>&nbsp;<a class=\"btn btn_trash\" onclick=\"delFile('" + v.id + "');\" href=\"javascript:void(0);\"><span>删除</span></a>&nbsp;</td>")
                    htmlStr += "</tr>";
                });
                body.html(htmlStr);
            }
        }
    });
}

function delFile(id) {
    jConfirm('删除文件后无法恢复,确定删除?', '友情提示', function (r) {
        if (r == true) {
            jQuery.post("/codegen/delFile", "cbx_ids=" + id, function (result) {
                if (result.code == "000000") {
                    getFiles();
                    jAlert(result.data.result, "友情提示");
                } else {
                    jAlert(result.msg, "友情提示");
                }
            });
        }
    });
}

function delAllFile() {
    var cbx_ids = "";
    var index = 0;
    jQuery.each(jQuery("input:checkbox[name='fileId']:checked"), function (i, v) {
        var cbx = jQuery(this);
        if (cbx.is(":checked")) {
            cbx_ids += "," + jQuery(this).val();
            index++;
        }
    });
    if (cbx_ids == "") {
        jAlert("请选择删除的记录", "友情提示");
        return false;
    }
    jConfirm('选中[' + index + ']条记录,删除文件后无法恢复,确定删除?', '友情提示', function (r) {
        if (r == true) {
            jQuery.post("/codegen/delFile", "cbx_ids=" + cbx_ids.substring(1), function (result) {
                if (result.code == "000000") {
                    getFiles();
                    jAlert(result.data.result, "友情提示");
                } else {
                    jAlert(result.msg, "友情提示");
                }
            });
        }
    });
}

function cbx_func(obj) {
    if (jQuery("#" + obj).is(":checked")) {
        jQuery("#" + obj + "_s1").attr("class", "checked");
        jQuery("#" + obj + "_s2").attr("class", "checked");
    } else {
        jQuery("#" + obj + "_s1").attr("class", "");
        jQuery("#" + obj + "_s2").attr("class", "");
    }
}

function cbx_selAll() {
    if (jQuery("#cbx_all").is(":checked")) {
        jQuery.each(jQuery("input[name='fileId']"), function (i, v) {
            var obj = jQuery(this).attr("id");
            jQuery("#" + obj + "_s1").attr("class", "checked");
            jQuery("#" + obj + "_s2").attr("class", "checked");
            jQuery(this).attr("checked", true);
        });
    } else {
        jQuery.each(jQuery("input[name='fileId']"), function (i, v) {
            var obj = jQuery(this).attr("id");
            jQuery("#" + obj + "_s1").attr("class", "");
            jQuery("#" + obj + "_s2").attr("class", "");
            jQuery(this).removeAttr("checked");
        });
    }
}

function submitSignUpForm() {
    var username = jQuery("#username").val();
    var password = jQuery("#password").val();
    var nickname = jQuery("#nickname").val();
    var company = jQuery("#company").val();
    if (username == null || "" == username) {
        jAlert("请输入您的帐号", "友情提示");
        return false;
    }
    if (password == null || "" == password) {
        jAlert("请输入您的密码", "友情提示");
        return false;
    }
    if (nickname == null || "" == nickname) {
        jAlert("请输入您的昵称", "友情提示");
        return false;
    }
    jQuery.post("/user/signup", "username=" + username + "&password=" + password + "&nickname=" + nickname + "&company=" + company, function (result) {
        if (result.code == "000000") {
            jConfirm('注册已成功,是否前往登录?', '友情提示', function (r) {
                if (r == true) {
                    location.href = '/static/login.html';
                } else {
                    location.href = '/static/signup.html';
                }
            });
        } else {
            jAlert(result.msg, "友情提示");
        }
    });
}

var btnlock = false;

function uploadfile() {
    if (btnlock == true) {
        alert('正在处理,请耐心等待...');
        return false;
    }
    var file_status = jQuery('#file_status');
    var file_deal = jQuery('#file_deal');
    var file_down = jQuery('#file_down');
    var file_path = jQuery('#file_path');
    file_status.show();
    file_deal.show();
    btnlock = true;
    var type = jQuery("input[name='uploadtype']:checked").val();
    jQuery("#uploadfile").ajaxSubmit({
        type: 'post',
        url: '/codegen/uploadFile?uploadtype=' + type,
        success: function (data) {
            if (data.code == '000000') {
                var path = encodeURI(data.data.path);
                file_path.attr("href", "/codegen/downUploadFile?filePath=" + path);
                file_deal.hide();
                file_down.show();
            } else {
                file_status.hide();
                file_deal.hide();
                file_down.hide();
                if (data.msg == undefined) {
                    alert('您尚未登录或登录已超时');
                } else {
                    alert(data.msg);
                }
            }
            btnlock = false;
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            alert("上传或处理失败,请尝试刷新页面再操作");
            file_status.hide();
            file_deal.hide();
            file_down.hide();
            btnlock = false;
        }
    });
}