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
								
	jQuery('#followbtn').click(function(){
		if(jQuery(this).text() == 'Follow') {
			jQuery(this).text('Following')
						.removeClass('btn_yellow')
						.addClass('btn_lime');
			
			//this is an example of updating number 
			//of following when clicking follow button
			jQuery('#following span').text('21');
			
			//use the line of code below to implement it to the server using ajax
			//uncomment the code to use it
			//var action = 'Follow';
			//var url = 'enter your url here'
			//jQuery.post(url,{action: action},function(data) {
				//the server response should be the updated number of following
			//});
			
		} else {
			jQuery(this).text('Follow')
						.removeClass('btn_lime')
						.addClass('btn_yellow');
			
			//this is an example of updating number 
			//of following when clicking following button
			jQuery('#following span').text('20'); 
			
			//use the line of code below to implement it to the server using ajax
			//uncomment the code to use it
			//var action = 'Unfollow';
			//var url = 'enter your url here'
			//jQuery.post(url,{action: action},function(data) {
				//the server response should be the updated number of following
			//});
		}
	});
	
	///// ACTIVE STATUS ON HOVER /////
	jQuery('.bq2').hover(function(){
		jQuery(this).find('.edit_status').show();	
	},function(){
		jQuery(this).find('.edit_status').hide();	
	});
	
	
	///// CONTENT SLIDER /////
	jQuery('#slidercontent').bxSlider({
		prevText: '',
		nextText: ''
	});
	

	///// AUTOGROW TEXTAREA /////
	jQuery('#comment').autogrow();


	
	
});
