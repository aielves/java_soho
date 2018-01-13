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
	
	///// PRODUCT DISPLAY INFO WHEN HOVERING THUMBNAILS /////
	jQuery('.prodlist li').hover(function(){
		jQuery(this).find('.contentinner').stop().animate({marginTop: 0});
	},function(){
		jQuery(this).find('.contentinner').stop().animate({marginTop: '132px'});
	});
	
	///// SWITCHING LIST FROM 3 COLUMNS TO 2 COLUMN LIST /////
	function rearrangelist() {
		if(jQuery(window).width() < 480) {
			if(jQuery('.prodlist li.one_half').length == 0) {
				var count = 0;
				jQuery('.prodlist li').removeAttr('class');
				jQuery('.prodlist li').each(function(){
					jQuery(this).addClass('one_half');
					if(count%2 != 0) jQuery(this).addClass('last');
					count++;
				});	
			}
			
			if(jQuery(window).width() < 400)
				jQuery('.prodlist li').removeAttr('class');
		
		} else {
			if(jQuery('.prodlist li.one_third').length == 0) {
				var count = 0;
				jQuery('.prodlist li').removeAttr('class');
				jQuery('.prodlist li').each(function(){
					jQuery(this).addClass('one_third');
					if(count == 2){
						jQuery(this).addClass('last');
						count = 0;
					} else {
						count++;
					}
				});	
			}
		}
	}
	
	rearrangelist();
	
	///// ON RESIZE WINDOW /////
	jQuery(window).resize(function(){
		rearrangelist();
	});
});
