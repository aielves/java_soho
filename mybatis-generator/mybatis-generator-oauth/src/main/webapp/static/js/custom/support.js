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

	///// SEARCH USER FROM RIGHT SIDEBAR /////
	jQuery('.chatsearch input').bind('focusin focusout',function(e){
		if(e.type == 'focusin') {
			if(jQuery(this).val() == 'Search') jQuery(this).val('');	
		} else {
			if(jQuery(this).val() == '') jQuery(this).val('Search');	
		}
	});
	
	///// SUBMIT A MESSAGE VIA A SUBMIT BUTTON CLICK /////
	jQuery('.messagebox button').click(function(){
		enterMessage();
	});
	
	///// SUBMIT A MESSAGE VIA AN ENTER KEY PRESS /////
	jQuery('.messagebox input').keypress(function(e){
		if(e.which == 13)
			enterMessage();
	});
	
	function enterMessage() {
		var msg = jQuery('.messagebox input').val(); //get the value of message box
		
		//display message from a message box
		if(msg != '') {
			jQuery('#chatmessageinner').append('<p><img src="images/thumbs/avatar12.png" alt="" />'
											   +'<span class="msgblock radius2"><strong>You</strong> <span class="time">- 10:14 am</span>'
											   +'<span class="msg">'+msg+'</span></span></p>');
			jQuery('.messagebox input').val('');
			jQuery('.messagebox input').focus();
			jQuery('#chatmessageinner').animate({scrollTop: jQuery('#chatmessageinner').height()});
			
			//this will create a sample response display after submitting message
			window.setTimeout(  
				function() {  
					//this is just a sample reply when somebody send a message
					jQuery('#chatmessageinner').append('<p class="reply"><img src="images/thumbs/avatar13.png" alt="" />'
													   +'<span class="msgblock radius2"><strong>Tigress:</strong> <span class="time">10:15 am</span>'
													   +'<span class="msg">This is an automated reply!!</span></span></p>', function(){
						jQuery(this).animate({scrollTop: jQuery(this).height()});
					});
				}, 1000);			
		}	
	}
	
});
