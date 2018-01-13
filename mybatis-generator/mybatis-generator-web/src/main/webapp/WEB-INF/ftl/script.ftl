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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>魔法信-后台管理</title>
<link rel="stylesheet" href="/static/css/style.default.css" type="text/css"/>
<script type="text/javascript" src="/static/js/plugins/jquery-1.7.min.js"></script>
<script type="text/javascript" src="/static/js/plugins/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="/static/js/plugins/jquery.cookie.js"></script>
<script type="text/javascript" src="/static/js/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="/static/js/plugins/jquery.uniform.min.js"></script>
<script type="text/javascript" src="/static/js/custom/general.js"></script>
<script type="text/javascript" src="/static/js/custom/tables.js"></script>
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
    function menuClickFun(id){
        jQuery("#left_menu li").removeAttr("class");
        jQuery("#li_"+id).attr("class", "current");
        jQuery("#"+id).show();
        jQuery.post("/admin/menu/click", "menuId="+id, function (data) {

        });
    }
</script>