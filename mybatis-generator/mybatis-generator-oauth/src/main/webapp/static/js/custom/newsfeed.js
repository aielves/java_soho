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
	
	///// AUTOGROW TEXTAREA /////
	jQuery('#statustext, #statusphoto').autogrow();
	
	///// PREVIEW IMAGE /////
	jQuery('.updatecontent .photo a').colorbox();
	
	///// ANIMATE IMAGE HOVER /////
	jQuery('.updatecontent .photo').hover(function(){
		jQuery(this).find('img').stop().animate({opacity: 0.75});
	}, function(){
		jQuery(this).find('img').stop().animate({opacity: 1});
	});
	
	///// FORM TRANSFORMATION /////
	jQuery('input:file').uniform();
	
	///// POST STATUS /////
	jQuery('#poststatus,#postphoto').submit(function(){
		var t = jQuery(this);
		var url = t.attr('action');											 
		var msg = t.find('textarea').val();
		jQuery.post(url,{message: msg},function(data){
			jQuery(data).insertBefore('.updatelist li:first-child');
			t.find('textarea').val('');
		});
		return false;
	});

});