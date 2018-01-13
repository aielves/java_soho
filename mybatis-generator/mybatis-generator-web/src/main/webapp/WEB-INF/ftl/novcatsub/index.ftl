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
            <h1 class="pagetitle">首页 > 小说漫画管理 > 查看详情列表</h1>
        </div><!--pageheader-->

        <div id="contentwrapper" class="contentwrapper">

            <div class="contenttitle2">
                <h3>小说漫画详情列表&nbsp;&nbsp;<a class="btn btn_book" href="/admin/novcatsub/init?novcatId=${novcatId!''}"><span>添加详情</span></a></h3>
            </div><!--contenttitle-->
            <table cellpadding="0" cellspacing="0" border="0" class="stdtable" id="dyntable">
                <thead>
                <tr>
                    <th class="head1">序号</th>
                    <th class="head1">标题</th>
                    <th class="head0">简介</th>
                    <th class="head0">内容</th>
                    <th class="head1">状态</th>
                    <th class="head0">创建时间</th>
                    <th class="head0">操作选项</th>
                </tr>
                </thead>
                <tbody>
                    <#if models?? && (models?size>0)>
                        <#list models as model>
                        <tr class="gradeX">
                            <td class="center" align="center">${model.orderno!"0"}</td>
                            <td><#escape x as x?html>${model.title!""}</#escape></td>
                            <td><#escape x as x?html>${model.resume!""}</#escape></td>
                            <#if type==1>
                            <td><#escape x as x?html>${model.content!""}</#escape></td>
                            <#else>
                            <td><#if model.content??><a href="${model.content}" target="_blank">点击查看</a></#if></td>
                            </#if>
                            <td>
                                <#if model.state == 1>
                                    正常
                                <#elseif model.state == 2>
                                    禁用
                                <#else>
                                    未知类型
                                </#if>
                            </td>
                            <td class="center">${model.ctime?number_to_datetime}</td>
                            <td class="center" width="12%"><a class="btn btn_archive" href="/admin/novcatsub/init?id=${model.id}&novcatId=${novcatId}"><span>修改</span></a>&nbsp;<a class="btn btn_trash" onclick="return confirm('删除后无法恢复,确定删除吗?');" href="/admin/novcatsub/delete?id=${model.id}"><span>删除</span></a>&nbsp;</td>
                        </tr>
                        </#list>
                    <#else>
                        <td colspan="7" align="center" class="center">暂无数据</td>
                    </#if>
                </tbody>
            </table>

        </div><!--contentwrapper-->
    </div><!-- centercontent -->


</div><!--bodywrapper-->

</body>
</html>
