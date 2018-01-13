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
            <h1 class="pagetitle">首页 > 游戏列表管理 > 查看列表</h1>
        </div><!--pageheader-->

        <div id="contentwrapper" class="contentwrapper">

            <div class="contenttitle2">
                <h3>游戏列表</h3>
            </div><!--contenttitle-->
            <table cellpadding="0" cellspacing="0" border="0" class="stdtable" id="dyntable">
                <thead>
                <tr>
                    <th class="head1">游戏序号</th>
                    <th class="head1">游戏名称</th>
                    <th class="head0">封面图片</th>
                    <th class="head1">游戏简介</th>
                    <th class="head1">玩法说明</th>
                    <th class="head0">游戏状态</th>
                    <th class="head0">维护时间</th>
                    <th class="head0">内测时间</th>
                    <th class="head0">即将开放时间</th>
                    <th class="head0">创建时间</th>
                    <th class="head0">操作选项</th>
                </tr>
                </thead>
                <tbody>
                    <#if models?? && (models?size>0)>
                        <#list models as model>
                        <tr class="gradeX">
                            <td class="center" align="center">${model.orderno!"0"}</td>
                            <td><#escape x as x?html>${model.name!""}</#escape></td>
                            <td><#if model.face??><a href="${model.face}" target="_blank">点击查看</a></#if></td>
                            <td><#escape x as x?html>${model.resume!""}</#escape></td>
                            <td><#escape x as x?html>${model.playdesc!""}</#escape></td>
                            <td>
                                <#if model.state == 1>
                                    正常
                                <#elseif model.state == 2>
                                    维护中
                                <#elseif model.state == 3>
                                    内测中
                                <#elseif model.state == 4>
                                    即将开放
                                <#else>
                                    未知类型
                                </#if>
                            </td>
                            <td><#escape x as x?html>${model.time2!""}</#escape></td>
                            <td><#escape x as x?html>${model.time3!""}</#escape></td>
                            <td><#escape x as x?html>${model.time4!""}</#escape></td>
                            <td class="center">${model.ctime?number_to_datetime}</td>
                            <td class="center" width="12%"><a class="btn btn_archive" href="/admin/game/init?id=${model.id}"><span>修改</span></a>&nbsp;<a class="btn btn_trash" onclick="return confirm('删除后无法恢复,确定删除吗?');" href="/admin/game/delete?id=${model.id}"><span>删除</span></a>&nbsp;</td>
                        </tr>
                        </#list>
                    <#else>
                        <td colspan="11" align="center" class="center">暂无数据</td>
                    </#if>
                </tbody>
            </table>

        </div><!--contentwrapper-->
    </div><!-- centercontent -->


</div><!--bodywrapper-->

</body>
</html>
