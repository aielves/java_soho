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
            <h1 class="pagetitle">首页 > 用户数据管理 > 添加/修改用户</h1>
        </div><!--pageheader-->

        <div id="contentwrapper" class="contentwrapper">


            <div id="basicform" class="subcontent">

                <div class="contenttitle2">
                    <h3>添加/修改用户</h3>
                </div><!--contenttitle-->

                <form class="stdform" action="/admin/user/modify" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="id" value="${model.id!''}"/>
                    <p>
                        <label>帐号</label>
                        <span class="field"><input type="text" name="username" value="${model.username!''}" <#if model.id??>disabled</#if> class="smallinput"/></span>
                        <small class="desc">必填项,由字母数字下划线组成且开头必须是字母，6-15位</small>
                    </p>
                    <p>
                        <label>密码</label>
                        <span class="field"><input type="text" name="passwd" value="" class="smallinput"/></span>
                        <small class="desc">必填项(修改时非必填),请输入6-15位密码,修改状态不输入则不改变原密码</small>
                    </p>
                    <p>
                        <label>昵称</label>
                        <span class="field"><input type="text" name="nickname" value="${model.nickname!''}" class="smallinput"/></span>
                        <small class="desc">必填项</small>
                    </p>
                    <p>
                        <label>SKYPE帐号</label>
                        <span class="field"><input type="text" name="skype" value="${model.skype!''}" class="smallinput"/></span>
                        <small class="desc"></small>
                    </p>
                    <p>
                        <label>头像</label>
                        <span class="field"><input type="file" name="file" value="点击上传"/></span>
                        <small class="desc"><span></span></small>
                    </p>
                    <p>
                        <label>手机</label>
                        <span class="field"><input type="text" name="mobile" value="${model.mobile!''}" class="smallinput"/></span>
                        <small class="desc">必填项</small>
                    </p>
                    <p>
                        <label>状态</label>
                        <span class="formwrapper">
                            	<select name="state" data-placeholder="Choose a Country..." class="chzn-select" style="width:350px;" tabindex="2">
                                  <#if model.state??&&model.state==1>
                                      <option value="1" selected>正常</option>
                                  <#elseif model.state??&&model.state==2>
                                      <option value="2" selected>禁用</option>
                                  <#elseif model.state??&&model.state==3>
                                      <option value="3" selected>冻结</option>
                                  </#if>
                                  <option value="1">正常</option>
                                  <option value="2">禁用</option>
                                  <option value="3">冻结</option>
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
