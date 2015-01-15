/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//$(document).ready(function(){ function getUrlVars() { var vars = {}; var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) { vars[key] = value; }); return vars; } var action = getUrlVars()["action"]; if (action != "") { $('a[href="#' + action + '"]').click() }; });

$(function(){

   //initialize sliders
   if($('.slider').length){
        $('.slider').slider();
     }  ;
    //display slider value
     $(".slider-1").on("slide", function(slideEvt) {
         $('#sliderValLabel-1').text(slideEvt.value);
     });

     //initialize tooltips
       if($("[data-toggle='tooltip']").length){
              $("[data-toggle='tooltip']").tooltip();
          } ;
// Javascript to enable link to tab
    /*
    var hash = document.location.hash;
    var prefix = "tab_";

    if (hash) {
        hash = hash.replace(prefix,'');
        var hashPieces = hash.split('?');
        activeTab = $('.nav-tabs a[href=' + hashPieces[0] + ']');
        activeTab && activeTab.tab('show');
    }*/

// Change hash for page-reload
    $('.nav-tabs a').on('shown.bs.tab', function (e) {
        window.location.hash = e.target.hash.replace("#", "#" + prefix);
    });

    // Javascript to enable link to tab from same page
    $("[data-toggle='url-tab']").on('click', function(e){
        window.location.reload(true);
    });
    /// img modal
    // This code is not even almost production ready. It's 2am here, and it's a cheap proof-of-concept if anything.
    $(".img-modal-btn.right").on('click', function(e){
        e.preventDefault();
        cur = $(this).parent().find('img:visible()');
        next = cur.next('img');
        par = cur.parent();
        if (!next.length) { next = $(cur.parent().find("img").get(0)) }
        cur.addClass('hidden');
        next.removeClass('hidden');

        return false;
    })

    $(".img-modal-btn.left").on('click', function(e){
        e.preventDefault();
        cur = $(this).parent().find('img:visible()');
        next = cur.prev('img');
        par = cur.parent();
        children = cur.parent().find("img");
        if (!next.length) { next = $(children.get(children.length-1)) }
        cur.addClass('hidden');
        next.removeClass('hidden');

        return false;
    })

});