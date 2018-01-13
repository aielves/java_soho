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

    <div class="centercontent tables">
        <div class="pageheader notab">
            <h1 class="pagetitle">首页 > 用户数据管理 > 查看列表</h1>
        </div><!--pageheader-->

        <div id="contentwrapper" class="contentwrapper">

            <div class="contenttitle2">
                <h3>用户列表</h3>
            </div><!--contenttitle-->
            <table cellpadding="0" cellspacing="0" border="0" class="stdtable" id="dyntable">
                <thead>
                <tr>
                    <th class="head1">ID</th>
                    <th class="head1">帐号</th>
                    <th class="head0">头像</th>
                    <th class="head1">昵称</th>
                    <th class="head1">SKYPE帐号</th>
                    <th class="head1">手机</th>
                    <th class="head1">魔法石</th>
                    <th class="head1">钱包余额</th>
                    <th class="head1">状态</th>
                    <th class="head0">创建时间</th>
                    <th class="head0">操作选项</th>
                </tr>
                </thead>
                <tbody>
                    <#if models?? && (models?size>0)>
                        <#list models as model>
                        <tr class="gradeX">
                            <td class="center" align="center">${model.id!"0"}</td>
                            <td>${model.username!""}</td>
                            <td><#if model.headimg??><a href="${model.headimg}" target="_blank">点击查看</a></#if></td>
                            <td><#escape x as x?html>${model.nickname!""}</#escape></td>
                            <td><#escape x as x?html>${model.skype!""}</#escape></td>
                            <td><#escape x as x?html>${model.mobile!""}</#escape></td>
                            <td><#escape x as x?html>${model.mfs!""}</#escape></td>
                            <td><#escape x as x?html>${model.money!""}</#escape></td>
                            <td>
                                <#if model.state == 1>
                                    正常
                                <#elseif model.state == 2>
                                    禁用
                                <#elseif model.state == 3>
                                    冻结
                                <#else>
                                    未知类型
                                </#if>
                            </td>
                            <td class="center">${model.ctime?number_to_datetime}</td>
                            <td class="center" width="12%"><a class="btn btn_archive" href="/admin/user/init?id=${model.id}"><span>修改</span></a>&nbsp;<a class="btn btn_trash" onclick="return confirm('删除后无法恢复,确定删除吗?');" href="/admin/init/delete?id=${model.id}"><span>删除</span></a>&nbsp;</td>
                        </tr>
                        </#list>
                    <#else>
                        <td colspan="10" align="center" class="center">暂无数据</td>
                    </#if>
                </tbody>
            </table>

        </div><!--contentwrapper-->
    </div><!-- centercontent -->


</div><!--bodywrapper-->

</body>
</html>
