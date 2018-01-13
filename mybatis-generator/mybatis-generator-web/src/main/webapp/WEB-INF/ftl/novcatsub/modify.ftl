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
            <h1 class="pagetitle">首页 > 小说漫画管理 > 添加/修改小说漫画详情</h1>
        </div><!--pageheader-->

        <div id="contentwrapper" class="contentwrapper">


            <div id="basicform" class="subcontent">

                <div class="contenttitle2">
                    <h3>添加/修改小说动漫详情</h3>
                </div><!--contenttitle-->

                <form class="stdform" action="/admin/novcatsub/modify" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="id" value="${model.id!''}"/>
                    <input type="hidden" name="novcatId" value="${model.novcatId!''}"/>
                    <input type="hidden" name="type" value="${type!''}"/>
                    <p>
                        <label>标题</label>
                        <span class="field"><input type="text" name="title" value="${model.title!''}" class="smallinput"/></span>
                    </p>

                    <p>
                        <label>序号</label>
                        <span class="field"><input type="text" name="orderno" value="${model.orderno!''}" class="smallinput"/></span>
                        <small class="desc">小说动漫详情列表中的排序位置,请输入数字编号</small>
                    </p>
                    <p>
                        <label>简介</label>
                        <span class="field"><textarea cols="80" rows="5" name="resume" class="longinput">${model.resume!""}</textarea></span>
                    </p>
                    <p <#if type==1>style="display: none"</#if>>
                        <label>漫画内容</label>
                        <span class="field"><input type="file" name="file" <#if model.id??><#else>multiple="true"</#if> value="点击上传"/></span>
                        <small class="desc"><span>选择漫画图片组,请将图片名称按有规则排序后上传(如1.jpg,2.jpg,3.jpg),多个图片会生成多条漫画图片记录</span></small>
                    </p>
                    <p <#if type==2>style="display: none"</#if>>
                        <label>小说内容</label>
                        <span class="field"><textarea cols="80" rows="5" name="content" class="longinput">${model.content!""}</textarea></span>
                    </p>
                    <p>
                        <label>状态</label>
                        <span class="formwrapper">
                            	<select name="state" data-placeholder="Choose a Country..." class="chzn-select" style="width:350px;" tabindex="2">
                                  <#if model.state??&&model.state==1>
                                      <option value="1" selected>正常</option>
                                  <#elseif model.state??&&model.state==2>
                                      <option value="2" selected>禁用</option>
                                  </#if>
                                  <option value="1">正常</option>
                                  <option value="2">禁用</option>
                                </select>
                            </span>
                    </p>

                    <br clear="all"/><br/>

                    <p class="stdformbutton">
                        <button class="submit radius2">提交数据</button>
                        <input type="reset" class="reset radius2" value="恢复数据"/>
                    </p>
                </form>
                <br/>
            </div><!--subcontent-->


        </div><!--contentwrapper-->
    </div><!-- centercontent -->


</div><!--bodywrapper-->

</body>
</html>