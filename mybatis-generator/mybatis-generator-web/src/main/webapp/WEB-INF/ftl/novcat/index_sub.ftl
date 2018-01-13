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
            <h1 class="pagetitle">首页 > 小说漫画管理 > 查看目录列表</h1>
        </div><!--pageheader-->

        <div id="contentwrapper" class="contentwrapper">

            <div class="contenttitle2">
                <h3>小说漫画目录列表&nbsp;&nbsp;<a class="btn btn_book" href="/admin/novcat/init_sub?pid=${pid!''}"><span>添加目录</span></a></h3>
            </div><!--contenttitle-->
            <table cellpadding="0" cellspacing="0" border="0" class="stdtable" id="dyntable">
                <thead>
                <tr>
                    <th class="head1">序号</th>
                    <th class="head1">名称</th>
                    <th class="head0">简介</th>
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
                            <td><#escape x as x?html>${model.name!""}</#escape></td>
                            <td><#escape x as x?html>${model.resume!""}</#escape></td>
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
                            <td class="center" width="15%"><a class="btn btn_book" href="/admin/novcatsub/index?novcatId=${model.id}"><span>详情</span></a>&nbsp;<a class="btn btn_archive" href="/admin/novcat/init_sub?id=${model.id}&pid=${model.pid}"><span>修改</span></a>&nbsp;<a class="btn btn_trash" onclick="return confirm('删除后无法恢复,确定删除吗?');" href="/admin/novcat/delete?id=${model.id}"><span>删除</span></a>&nbsp;</td>
                        </tr>
                        </#list>
                    <#else>
                        <td colspan="6" align="center" class="center">暂无数据</td>
                    </#if>
                </tbody>
            </table>

        </div><!--contentwrapper-->
    </div><!-- centercontent -->


</div><!--bodywrapper-->

</body>
</html>
