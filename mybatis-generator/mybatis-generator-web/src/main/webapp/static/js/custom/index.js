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
jQuery(document).ready(function(){
								
	///// TRANSFORM CHECKBOX /////							
	jQuery('input:checkbox').uniform();
	
	///// LOGIN FORM SUBMIT /////
	jQuery('#login').submit(function(){
	
		if(jQuery('#username').val() == '' && jQuery('#password').val() == '') {
			jQuery('.nousername').fadeIn();
			jQuery('.nopassword').hide();
			return false;	
		}
		if(jQuery('#username').val() != '' && jQuery('#password').val() == '') {
            jQuery('.nopassword').fadeIn().find('.userlogged h4, .userlogged a span').text(jQuery('#username').val());
            jQuery('.nousername,.username').hide();
            return false;;
        }
	});
	
	///// ADD PLACEHOLDER /////
	jQuery('#username').attr('placeholder','Username');
	jQuery('#password').attr('placeholder','Password');
    jQuery('#nickname').attr('placeholder','Nickname');
    jQuery('#mobile').attr('placeholder','Mobile');
});
