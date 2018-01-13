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
<#include "../script.ftl">
</head>

<body class="withvernav">
<div class="bodywrapper">
    <div class="topheader"><#include "../top.ftl"></div><!--topheader-->


    <div class="header"><#include "../header.ftl"></div><!--header-->

    <div class="vernav2 iconmenu">
    <#include "../left.ftl">
    </div><!--leftmenu-->

    <div id="main_show" class="centercontent tables">
        <div class="pageheader notab">
            <h1 class="pagetitle">首页 > 小说漫画管理 > 操作结果</h1>
        </div><!--pageheader-->

        <div id="contentwrapper" class="contentwrapper">


            <div id="basicform" class="subcontent">

                <div class="contenttitle2">
                    <h3>友情提示</h3>
                </div><!--contenttitle-->

                <form class="stdform" action="" method="post">
                    <p>
                        <div class="notibar msginfo">
                                <#--<a class="close"></a>-->
                                <p>${resultMsg}</p>
                        </div><!-- notification msginfo -->
                    </p>
                </form>
                <br/>
            </div><!--subcontent-->


        </div><!--contentwrapper-->
    </div><!-- centercontent -->


</div><!--bodywrapper-->

</body>
</html>
