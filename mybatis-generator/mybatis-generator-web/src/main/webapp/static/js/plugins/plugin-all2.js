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
jQuery.noConflict();

jQuery(document).ready(function(){

    if(!jQuery(this).hasClass('togglemenu_collapsed')) {
        //if(jQuery('.iconmenu').hasClass('vernav')) {
        if(jQuery('.vernav').length > 0) {
            if(jQuery('.vernav').hasClass('iconmenu')) {
                jQuery('body').addClass('withmenucoll');
                jQuery('.iconmenu').addClass('menucoll');
            } else {
                jQuery('body').addClass('withmenucoll');
                jQuery('.vernav').addClass('menucoll').find('ul').hide();
            }
        } else if(jQuery('.vernav2').length > 0) {
            //} else {
            jQuery('body').addClass('withmenucoll2');
            jQuery('.iconmenu').addClass('menucoll2');
        }

        jQuery(this).addClass('togglemenu_collapsed');

        jQuery('.iconmenu > ul > li > a').each(function(){
            var label = jQuery(this).text();
            jQuery('<li><span>'+label+'</span></li>')
                .insertBefore(jQuery(this).parent().find('ul li:first-child'));
        });
    } else {

        //if(jQuery('.iconmenu').hasClass('vernav')) {
        if(jQuery('.vernav').length > 0) {
            if(jQuery('.vernav').hasClass('iconmenu')) {
                jQuery('body').removeClass('withmenucoll');
                jQuery('.iconmenu').removeClass('menucoll');
            } else {
                jQuery('body').removeClass('withmenucoll');
                jQuery('.vernav').removeClass('menucoll').find('ul').show();
            }
        } else if(jQuery('.vernav2').length > 0) {
            //} else {
            jQuery('body').removeClass('withmenucoll2');
            jQuery('.iconmenu').removeClass('menucoll2');
        }
        jQuery(this).removeClass('togglemenu_collapsed');

        jQuery('.iconmenu ul ul li:first-child').remove();
    }


    ///// SHOW/HIDE USERDATA WHEN USERINFO IS CLICKED /////

    // jQuery('.userinfo').click(function(){
    // 	if(!jQuery(this).hasClass('active')) {
    // 		jQuery('.userinfodrop').show();
    // 		jQuery(this).addClass('active');
    // 	} else {
    // 		jQuery('.userinfodrop').hide();
    // 		jQuery(this).removeClass('active');
    // 	}
    // 	//remove notification box if visible
    // 	jQuery('.notification').removeClass('active');
    // 	jQuery('.noticontent').remove();
    //
    // 	return false;
    // });


    ///// SHOW/HIDE NOTIFICATION /////

    jQuery('.notification a').click(function(){
        var t = jQuery(this);
        var url = t.attr('href');
        if(!jQuery('.noticontent').is(':visible')) {
            jQuery.post(url,function(data){
                t.parent().append('<div class="noticontent">'+data+'</div>');
            });
            //this will hide user info drop down when visible
            jQuery('.userinfo').removeClass('active');
            jQuery('.userinfodrop').hide();
        } else {
            t.parent().removeClass('active');
            jQuery('.noticontent').hide();
        }
        return false;
    });



    ///// SHOW/HIDE BOTH NOTIFICATION & USERINFO WHEN CLICKED OUTSIDE OF THIS ELEMENT /////


    jQuery(document).click(function(event) {
        var ud = jQuery('.userinfodrop');
        var nb = jQuery('.noticontent');

        //hide user drop menu when clicked outside of this element
        if(!jQuery(event.target).is('.userinfodrop')
            && !jQuery(event.target).is('.userdata')
            && ud.is(':visible')) {
            ud.hide();
            jQuery('.userinfo').removeClass('active');
        }

        //hide notification box when clicked outside of this element
        if(!jQuery(event.target).is('.noticontent') && nb.is(':visible')) {
            nb.remove();
            jQuery('.notification').removeClass('active');
        }
    });


    ///// NOTIFICATION CONTENT /////

    jQuery('.notitab a').live('click', function(){
        var id = jQuery(this).attr('href');
        jQuery('.notitab li').removeClass('current'); //reset current
        jQuery(this).parent().addClass('current');
        if(id == '#messages')
            jQuery('#activities').hide();
        else
            jQuery('#messages').hide();

        jQuery(id).show();
        return false;
    });



    ///// SHOW/HIDE VERTICAL SUB MENU /////

    jQuery('.vernav > ul li a, .vernav2 > ul li a').each(function(){
        var url = jQuery(this).attr('href');
        jQuery(this).click(function(){
            if(jQuery(url).length > 0) {
                if(jQuery(url).is(':visible')) {
                    if(!jQuery(this).parents('div').hasClass('menucoll') &&
                        !jQuery(this).parents('div').hasClass('menucoll2'))
                        jQuery(url).slideUp();
                } else {
                    jQuery('.vernav ul ul, .vernav2 ul ul').each(function(){
                        jQuery(this).slideUp();
                    });
                    if(!jQuery(this).parents('div').hasClass('menucoll') &&
                        !jQuery(this).parents('div').hasClass('menucoll2'))
                        jQuery(url).slideDown();
                }
                return false;
            }
        });
    });


    ///// SHOW/HIDE SUB MENU WHEN MENU COLLAPSED /////
    jQuery('.menucoll > ul > li, .menucoll2 > ul > li').live('mouseenter mouseleave',function(e){
        if(e.type == 'mouseenter') {
            jQuery(this).addClass('hover');
            jQuery(this).find('ul').show();
        } else {
            jQuery(this).removeClass('hover').find('ul').hide();
        }
    });


    ///// HORIZONTAL NAVIGATION (AJAX/INLINE DATA) /////

    jQuery('.hornav a').click(function(){

        //this is only applicable when window size below 450px
        if(jQuery(this).parents('.more').length == 0)
            jQuery('.hornav li.more ul').hide();

        //remove current menu
        jQuery('.hornav li').each(function(){
            jQuery(this).removeClass('current');
        });

        jQuery(this).parent().addClass('current');	// set as current menu

        var url = jQuery(this).attr('href');
        if(jQuery(url).length > 0) {
            jQuery('.contentwrapper .subcontent').hide();
            jQuery(url).show();
        } else {
            jQuery.post(url, function(data){
                jQuery('#contentwrapper').html(data);
                jQuery('.stdtable input:checkbox').uniform();	//restyling checkbox
            });
        }
        return false;
    });


    ///// SEARCH BOX WITH AUTOCOMPLETE /////

    var availableTags = [
        "ActionScript",
        "AppleScript",
        "Asp",
        "BASIC",
        "C",
        "C++",
        "Clojure",
        "COBOL",
        "ColdFusion",
        "Erlang",
        "Fortran",
        "Groovy",
        "Haskell",
        "Java",
        "JavaScript",
        "Lisp",
        "Perl",
        "PHP",
        "Python",
        "Ruby",
        "Scala",
        "Scheme"
    ];
    jQuery('#keyword').autocomplete({
        source: availableTags
    });


    ///// SEARCH BOX ON FOCUS /////

    jQuery('#keyword').bind('focusin focusout', function(e){
        var t = jQuery(this);
        if(e.type == 'focusin' && t.val() == 'Enter keyword(s)') {
            t.val('');
        } else if(e.type == 'focusout' && t.val() == '') {
            t.val('Enter keyword(s)');
        }
    });


    ///// NOTIFICATION CLOSE BUTTON /////

    jQuery('.notibar .close').click(function(){
        jQuery(this).parent().fadeOut(function(){
            jQuery(this).remove();
        });
    });


    ///// COLLAPSED/EXPAND LEFT MENU /////
    jQuery('.togglemenu').click(function(){
        if(!jQuery(this).hasClass('togglemenu_collapsed')) {

            //if(jQuery('.iconmenu').hasClass('vernav')) {
            if(jQuery('.vernav').length > 0) {
                if(jQuery('.vernav').hasClass('iconmenu')) {
                    jQuery('body').addClass('withmenucoll');
                    jQuery('.iconmenu').addClass('menucoll');
                } else {
                    jQuery('body').addClass('withmenucoll');
                    jQuery('.vernav').addClass('menucoll').find('ul').hide();
                }
            } else if(jQuery('.vernav2').length > 0) {
                //} else {
                jQuery('body').addClass('withmenucoll2');
                jQuery('.iconmenu').addClass('menucoll2');
            }

            jQuery(this).addClass('togglemenu_collapsed');

            jQuery('.iconmenu > ul > li > a').each(function(){
                var label = jQuery(this).text();
                jQuery('<li><span>'+label+'</span></li>')
                    .insertBefore(jQuery(this).parent().find('ul li:first-child'));
            });
        } else {

            //if(jQuery('.iconmenu').hasClass('vernav')) {
            if(jQuery('.vernav').length > 0) {
                if(jQuery('.vernav').hasClass('iconmenu')) {
                    jQuery('body').removeClass('withmenucoll');
                    jQuery('.iconmenu').removeClass('menucoll');
                } else {
                    jQuery('body').removeClass('withmenucoll');
                    jQuery('.vernav').removeClass('menucoll').find('ul').show();
                }
            } else if(jQuery('.vernav2').length > 0) {
                //} else {
                jQuery('body').removeClass('withmenucoll2');
                jQuery('.iconmenu').removeClass('menucoll2');
            }
            jQuery(this).removeClass('togglemenu_collapsed');

            jQuery('.iconmenu ul ul li:first-child').remove();
        }
    });



    ///// RESPONSIVE /////
    if(jQuery(document).width() < 640) {
        jQuery('.togglemenu').addClass('togglemenu_collapsed');
        if(jQuery('.vernav').length > 0) {

            jQuery('.iconmenu').addClass('menucoll');
            jQuery('body').addClass('withmenucoll');
            jQuery('.centercontent').css({marginLeft: '56px'});
            if(jQuery('.iconmenu').length == 0) {
                jQuery('.togglemenu').removeClass('togglemenu_collapsed');
            } else {
                jQuery('.iconmenu > ul > li > a').each(function(){
                    var label = jQuery(this).text();
                    jQuery('<li><span>'+label+'</span></li>')
                        .insertBefore(jQuery(this).parent().find('ul li:first-child'));
                });
            }

        } else {

            jQuery('.iconmenu').addClass('menucoll2');
            jQuery('body').addClass('withmenucoll2');
            jQuery('.centercontent').css({marginLeft: '36px'});

            jQuery('.iconmenu > ul > li > a').each(function(){
                var label = jQuery(this).text();
                jQuery('<li><span>'+label+'</span></li>')
                    .insertBefore(jQuery(this).parent().find('ul li:first-child'));
            });
        }
    }


    jQuery('.searchicon').live('click',function(){
        jQuery('.searchinner').show();
    });

    jQuery('.searchcancel').live('click',function(){
        jQuery('.searchinner').hide();
    });



    ///// ON LOAD WINDOW /////
    function reposSearch() {
        if(jQuery(window).width() < 520) {
            if(jQuery('.searchinner').length == 0) {
                jQuery('.search').wrapInner('<div class="searchinner"></div>');
                jQuery('<a class="searchicon"></a>').insertBefore(jQuery('.searchinner'));
                jQuery('<a class="searchcancel"></a>').insertAfter(jQuery('.searchinner button'));
            }
        } else {
            if(jQuery('.searchinner').length > 0) {
                jQuery('.search form').unwrap();
                jQuery('.searchicon, .searchcancel').remove();
            }
        }
    }
    reposSearch();

    ///// ON RESIZE WINDOW /////
    jQuery(window).resize(function(){

        if(jQuery(window).width() > 640)
            jQuery('.centercontent').removeAttr('style');

        reposSearch();

    });


    ///// CHANGE THEME /////
    jQuery('.changetheme a').click(function(){
        var c = jQuery(this).attr('class');
        if(jQuery('#addonstyle').length == 0) {
            if(c != 'default') {
                jQuery('head').append('<link id="addonstyle" rel="stylesheet" href="css/style.'+c+'.css" type="text/css" />');
                jQuery.cookie("addonstyle", c, { path: '/' });
            }
        } else {
            if(c != 'default') {
                jQuery('#addonstyle').attr('href','css/style.'+c+'.css');
                jQuery.cookie("addonstyle", c, { path: '/' });
            } else {
                jQuery('#addonstyle').remove();
                jQuery.cookie("addonstyle", null);
            }
        }
    });

    ///// LOAD ADDON STYLE WHEN IT'S ALREADY SET /////
    if(jQuery.cookie('addonstyle')) {
        var c = jQuery.cookie('addonstyle');
        if(c != '') {
            jQuery('head').append('<link id="addonstyle" rel="stylesheet" href="css/style.'+c+'.css" type="text/css" />');
            jQuery.cookie("addonstyle", c, { path: '/' });
        }
    }



});



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

    ///// FORM TRANSFORMATION /////
    jQuery('input:checkbox, input:radio, select.uniformselect, input:file').uniform();


    ///// DUAL BOX /////
    var db = jQuery('#dualselect').find('.ds_arrow .arrow');	//get arrows of dual select
    var sel1 = jQuery('#dualselect select:first-child');		//get first select element
    var sel2 = jQuery('#dualselect select:last-child');			//get second select element

    sel2.empty(); //empty it first from dom.

    db.click(function(){
        var t = (jQuery(this).hasClass('ds_prev'))? 0 : 1;	// 0 if arrow prev otherwise arrow next
        if(t) {
            sel1.find('option').each(function(){
                if(jQuery(this).is(':selected')) {
                    jQuery(this).attr('selected',false);
                    var op = sel2.find('option:first-child');
                    sel2.append(jQuery(this));
                }
            });
        } else {
            sel2.find('option').each(function(){
                if(jQuery(this).is(':selected')) {
                    jQuery(this).attr('selected',false);
                    sel1.append(jQuery(this));
                }
            });
        }
    });



    ///// FORM VALIDATION /////
    jQuery("#form1").validate({
        rules: {
            firstname: "required",
            lastname: "required",
            email: {
                required: true,
                email: true,
            },
            location: "required",
            selection: "required"
        },
        messages: {
            firstname: "Please enter your first name",
            lastname: "Please enter your last name",
            email: "Please enter a valid email address",
            location: "Please enter your location"
        }
    });

    ///// TAG INPUT /////

    jQuery('#tags').tagsInput();


    ///// SPINNER /////

    jQuery("#spinner").spinner({min: 0, max: 100, increment: 2});


    ///// CHARACTER COUNTER /////

    jQuery("#textarea2").charCount({
        allowed: 120,
        warning: 20,
        counterText: 'Characters left: '
    });


    ///// SELECT WITH SEARCH /////
    jQuery(".chzn-select").chosen();

});



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
// jQuery Alert Dialogs Plugin
//
// Version 1.1
//
// Cory S.N. LaViska
// A Beautiful Site (http://abeautifulsite.net/)
// 14 May 2009
//
// Visit http://abeautifulsite.net/notebook/87 for more information
//
// Usage:
//		jAlert( message, [title, callback] )
//		jConfirm( message, [title, callback] )
//		jPrompt( message, [value, title, callback] )
//
// History:
//
//		1.00 - Released (29 December 2008)
//
//		1.01 - Fixed bug where unbinding would destroy all resize events
//
// License:
//
// This plugin is dual-licensed under the GNU General Public License and the MIT License and
// is copyright 2008 A Beautiful Site, LLC.
//
(function($) {

    $.alerts = {

        // These properties can be read/written by accessing $.alerts.propertyName from your scripts at any time

        verticalOffset: -75,                // vertical offset of the dialog from center screen, in pixels
        horizontalOffset: 0,                // horizontal offset of the dialog from center screen, in pixels/
        repositionOnResize: true,           // re-centers the dialog on window resize
        overlayOpacity: .01,                // transparency level of overlay
        overlayColor: '#FFF',               // base color of overlay
        draggable: true,                    // make the dialogs draggable (requires UI Draggables plugin)
        okButton: '&nbsp;OK&nbsp;',         // text for the OK button
        cancelButton: '&nbsp;Cancel&nbsp;', // text for the Cancel button
        dialogClass: null,                  // if specified, this class will be applied to all dialogs

        // Public methods

        alert: function(message, title, callback) {
            if( title == null ) title = 'Alert';
            $.alerts._show(title, message, null, 'alert', function(result) {
                if( callback ) callback(result);
            });
        },

        confirm: function(message, title, callback) {
            if( title == null ) title = 'Confirm';
            $.alerts._show(title, message, null, 'confirm', function(result) {
                if( callback ) callback(result);
            });
        },

        prompt: function(message, value, title, callback) {
            if( title == null ) title = 'Prompt';
            $.alerts._show(title, message, value, 'prompt', function(result) {
                if( callback ) callback(result);
            });
        },

        // Private methods

        _show: function(title, msg, value, type, callback) {

            $.alerts._hide();
            $.alerts._overlay('show');

            $("BODY").append(
                '<div id="popup_container">' +
                '<h1 id="popup_title"></h1>' +
                '<div id="popup_content">' +
                '<div id="popup_message"></div>' +
                '</div>' +
                '</div>');

            if( $.alerts.dialogClass ) $("#popup_container").addClass($.alerts.dialogClass);

            // IE6 Fix
            var pos = ($.browser.msie && parseInt($.browser.version) <= 6 ) ? 'absolute' : 'fixed';

            $("#popup_container").css({
                position: pos,
                zIndex: 99999,
                padding: 0,
                margin: 0
            });

            $("#popup_title").text(title);
            $("#popup_content").addClass(type);
            $("#popup_message").text(msg);
            $("#popup_message").html( $("#popup_message").text().replace(/\n/g, '<br />') );

            $("#popup_container").css({
                minWidth: $("#popup_container").outerWidth(),
                maxWidth: $("#popup_container").outerWidth()
            });

            $.alerts._reposition();
            $.alerts._maintainPosition(true);

            switch( type ) {
                case 'alert':
                    $("#popup_message").after('<div id="popup_panel"><input type="button" value="' + $.alerts.okButton + '" id="popup_ok" /></div>');
                    $("#popup_ok").click( function() {
                        $.alerts._hide();
                        callback(true);
                    });
                    $("#popup_ok").focus().keypress( function(e) {
                        if( e.keyCode == 13 || e.keyCode == 27 ) $("#popup_ok").trigger('click');
                    });
                    break;
                case 'confirm':
                    $("#popup_message").after('<div id="popup_panel"><input type="button" value="' + $.alerts.okButton + '" id="popup_ok" /> <input type="button" value="' + $.alerts.cancelButton + '" id="popup_cancel" /></div>');
                    $("#popup_ok").click( function() {
                        $.alerts._hide();
                        if( callback ) callback(true);
                    });
                    $("#popup_cancel").click( function() {
                        $.alerts._hide();
                        if( callback ) callback(false);
                    });
                    $("#popup_ok").focus();
                    $("#popup_ok, #popup_cancel").keypress( function(e) {
                        if( e.keyCode == 13 ) $("#popup_ok").trigger('click');
                        if( e.keyCode == 27 ) $("#popup_cancel").trigger('click');
                    });
                    break;
                case 'prompt':
                    $("#popup_message").append('<br /><input type="text" size="30" id="popup_prompt" />').after('<div id="popup_panel"><input type="button" value="' + $.alerts.okButton + '" id="popup_ok" /> <input type="button" value="' + $.alerts.cancelButton + '" id="popup_cancel" /></div>');
                    $("#popup_prompt").width( $("#popup_message").width() );
                    $("#popup_ok").click( function() {
                        var val = $("#popup_prompt").val();
                        $.alerts._hide();
                        if( callback ) callback( val );
                    });
                    $("#popup_cancel").click( function() {
                        $.alerts._hide();
                        if( callback ) callback( null );
                    });
                    $("#popup_prompt, #popup_ok, #popup_cancel").keypress( function(e) {
                        if( e.keyCode == 13 ) $("#popup_ok").trigger('click');
                        if( e.keyCode == 27 ) $("#popup_cancel").trigger('click');
                    });
                    if( value ) $("#popup_prompt").val(value);
                    $("#popup_prompt").focus().select();
                    break;
            }

            // Make draggable
            if( $.alerts.draggable ) {
                try {
                    $("#popup_container").draggable({ handle: $("#popup_title") });
                    $("#popup_title").css({ cursor: 'move' });
                } catch(e) { /* requires jQuery UI draggables */ }
            }
        },

        _hide: function() {
            $("#popup_container").remove();
            $.alerts._overlay('hide');
            $.alerts._maintainPosition(false);
        },

        _overlay: function(status) {
            switch( status ) {
                case 'show':
                    $.alerts._overlay('hide');
                    $("BODY").append('<div id="popup_overlay"></div>');
                    $("#popup_overlay").css({
                        position: 'absolute',
                        zIndex: 99998,
                        top: '0px',
                        left: '0px',
                        width: '100%',
                        height: $(document).height(),
                        background: $.alerts.overlayColor,
                        opacity: $.alerts.overlayOpacity
                    });
                    break;
                case 'hide':
                    $("#popup_overlay").remove();
                    break;
            }
        },

        _reposition: function() {
            var top = (($(window).height() / 2) - ($("#popup_container").outerHeight() / 2)) + $.alerts.verticalOffset;
            var left = (($(window).width() / 2) - ($("#popup_container").outerWidth() / 2)) + $.alerts.horizontalOffset;
            if( top < 0 ) top = 0;
            if( left < 0 ) left = 0;

            // IE6 fix
            if( $.browser.msie && parseInt($.browser.version) <= 6 ) top = top + $(window).scrollTop();

            $("#popup_container").css({
                top: top + 'px',
                left: left + 'px'
            });
            $("#popup_overlay").height( $(document).height() );
        },

        _maintainPosition: function(status) {
            if( $.alerts.repositionOnResize ) {
                switch(status) {
                    case true:
                        $(window).bind('resize', $.alerts._reposition);
                        break;
                    case false:
                        $(window).unbind('resize', $.alerts._reposition);
                        break;
                }
            }
        }

    }

    // Shortuct functions
    jAlert = function(message, title, callback) {
        $.alerts.alert(message, title, callback);
    }

    jConfirm = function(message, title, callback) {
        $.alerts.confirm(message, title, callback);
    };

    jPrompt = function(message, value, title, callback) {
        $.alerts.prompt(message, value, title, callback);
    };

})(jQuery);