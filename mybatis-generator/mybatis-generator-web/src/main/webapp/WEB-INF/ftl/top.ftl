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
<div class="left">
    <h1 class="logo">魔法信</h1>
    <span class="slogan">后台管理系统</span>

    <div class="search">
        <form action="" method="post">
            <input type="text" name="keyword" id="keyword" value="输入搜索内容"/>
            <button class="submitbutton"></button>
        </form>
    </div><!--search-->

    <br clear="all"/>

</div><!--left-->

<div class="right">
    <!--<div class="notification">
        <a class="count" href="ajax/notifications.html"><span>9</span></a>
    </div>-->
    <div class="userinfo">
        <img src="/static/images/thumbs/avatar.png" alt=""/>
        <span>管理员</span>
    </div><!--userinfo-->

    <div class="userinfodrop">
        <div class="avatar">
            <a href=""><img src="/static/images/thumbs/avatarbig.png" alt=""/></a>
            <div class="changetheme">
                切换主题: <br/>
                <a class="default"></a>
                <a class="blueline"></a>
                <a class="greenline"></a>
                <a class="contrast"></a>
                <a class="custombg"></a>
            </div>
        </div><!--avatar-->
        <div class="userdata">
            <h4>Juan</h4>
            <span class="email">youremail@yourdomain.com</span>
            <ul>
                <li><a href="#">编辑资料</a></li>
                <li><a href="#">账号设置</a></li>
                <li><a href="#">帮助</a></li>
                <li><a href="/admin/oauth/logout">退出</a></li>
            </ul>
        </div><!--userdata-->
    </div><!--userinfodrop-->
</div><!--right-->