/*
 ================================================================
 * Template:  	 MXapp - App Landing Page HTML Template
 * Written by: 	 Harnish Design - (http://www.harnishdesign.net)
 * Description:   Main Custom Script File
 ================================================================
 */

(function ($) {
    "use strict";
// Preloader
$(window).on('load', function () {
	$('[data-loader="dual-ring"]').fadeOut(); // will first fade out the loading animation
	$('.preloader').delay(333).fadeOut('slow'); // will fade out the white DIV that covers the website.
	$('body').delay(333);
});

// Header Sticky
$(window).on('scroll', function () {
	var stickytop = $('#header.sticky-top .bg-transparent');
	var stickytopslide = $('#header.sticky-top-slide');

	if ($(this).scrollTop() > 1) {
		stickytop.addClass("sticky-on-top");
		stickytop.find(".logo img").attr('src', stickytop.find('.logo img').data('sticky-logo'));
	} else {
		stickytop.removeClass("sticky-on-top");
		stickytop.find(".logo img").attr('src', stickytop.find('.logo img').data('default-logo'));
	}

	if ($(this).scrollTop() > 180) {
		stickytopslide.find(".primary-menu").addClass("sticky-on");
		stickytopslide.find(".logo img").attr('src', stickytopslide.find('.logo img').data('sticky-logo'));
	} else {
		stickytopslide.find(".primary-menu").removeClass("sticky-on");
		stickytopslide.find(".logo img").attr('src', stickytopslide.find('.logo img').data('default-logo'));
	}
});

// Sections Scroll
if ($("body").hasClass("side-header")) {
	$('.smooth-scroll').on('click', function () {
		event.preventDefault();
		var sectionTo = $(this).attr('href');
		$('html, body').stop().animate({
			scrollTop: $(sectionTo).offset().top}, 1500, 'easeInOutExpo');
	});
} else {
	$('.smooth-scroll').on('click', function () {
		event.preventDefault();
		var sectionTo = $(this).attr('href');
		$('html, body').stop().animate({
			scrollTop: $(sectionTo).offset().top - 50}, 1500, 'easeInOutExpo');
	});
}

// Mobile Menu
$('.navbar-toggler').on('click', function () {
  $(this).toggleClass('show');
});
$(".navbar-nav a").on('click', function () {
  $(".navbar-collapse, .navbar-toggler").removeClass("show");
});

// Overlay Menu & Side Open Menu
$('.navbar-side-open .collapse, .navbar-overlay .collapse').on('show.bs.collapse hide.bs.collapse', function (e) {
  e.preventDefault();
}),
$('.navbar-side-open [data-bs-toggle="collapse"], .navbar-overlay [data-bs-toggle="collapse"]').on('click', function (e) {
  e.preventDefault();
  $($(this).data('bs-target')).toggleClass('show');
})

/*---------------------------------
   Carousel (Owl Carousel)
----------------------------------- */
$(".owl-carousel").each(function (index) {
    var a = $(this);
	if ($("html").attr("dir") == 'rtl') {
		var rtlVal = true
	}else{
		var rtlVal = false
    }
	$(this).owlCarousel({
		rtl: rtlVal,
		autoplay: a.data('autoplay'),
		center: a.data('center'),
		autoplayTimeout: a.data('autoplaytimeout'),
		autoplayHoverPause: a.data('autoplayhoverpause'),
		loop: a.data('loop'),
		speed: a.data('speed'),
		nav: a.data('nav'),
		dots: a.data('dots'),
		autoHeight: a.data('autoheight'),
		autoWidth: a.data('autowidth'),
		margin: a.data('margin'),
		stagePadding: a.data('stagepadding'),
		slideBy: a.data('slideby'),
		lazyLoad: a.data('lazyload'),
		navText:['<i class="fa fa-chevron-left"></i>', '<i class="fa fa-chevron-right"></i>'],
		animateOut: a.data('animateout'),
		animateIn: a.data('animatein'),
		video: a.data('video'),
		items: a.data('items'),
		responsive:{
        0:{items: a.data('items-xs'),},
        576:{items: a.data('items-sm'),},
		768:{items: a.data('items-md'),},
        992:{items: a.data('items-lg'),}
        }
    });
});

/*------------------------------------
 Magnific Popup
 -------------------------------------- */
// Image on Modal
$('.popup-img-gallery').each(function () {
	$(this).magnificPopup({
		delegate: '.popup-img:visible',
		type: "image",
		tLoading: '<div class="preloader"><div data-loader="dual-ring"></div></div>',
		closeOnContentClick: !0,
		mainClass: "mfp-fade",
		gallery: {
			enabled: true,
			navigateByImgClick: true,
			preload: [0, 1]
		},
	});
});

// Ajax On Modal 
$('.popup-ajax-gallery').each(function () {
	$(this).magnificPopup({
		delegate: '.popup-ajax:visible',
		type: "ajax",
		tLoading: '<div class="preloader"><div data-loader="dual-ring"></div></div>',
		mainClass: "mfp-fade",
		closeBtnInside: true,
		midClick: true,
		gallery: {
			enabled: true,
		},
		callbacks: {
		ajaxContentAdded: function() {
			$(".owl-carousel").each(function (index) {
			  var a = $(this);
			  if ($("html").attr("dir") == 'rtl') {
		var rtlVal = true
	}else{
		var rtlVal = false
    }
	$(this).owlCarousel({
		rtl: rtlVal,
				autoplay: a.data('autoplay'),
				center: a.data('center'),
				autoplayTimeout: a.data('autoplaytimeout'),
				autoplayHoverPause: a.data('autoplayhoverpause'),
				loop: a.data('loop'),
				speed: a.data('speed'),
				nav: a.data('nav'),
				dots: a.data('dots'),
				autoHeight: a.data('autoheight'),
				autoWidth: a.data('autowidth'),
				margin: a.data('margin'),
				stagePadding: a.data('stagepadding'),
				slideBy: a.data('slideby'),
				lazyLoad: a.data('lazyload'),
				navText:['<i class="fa fa-chevron-left"></i>', '<i class="fa fa-chevron-right"></i>'],
				animateOut: a.data('animateOut'),
				animateIn: a.data('animateIn'),
				video: a.data('video'),
				items: a.data('items'),
				responsive:{
					0:{items: a.data('items-xs'),},
					576:{items: a.data('items-sm'),},
					768:{items: a.data('items-md'),},
					992:{items: a.data('items-lg'),}
				}	
                });
            });
         }
    }
	});
});

// YouTube/Viemo Video & Gmaps
$('.popup-youtube, .popup-vimeo, .popup-gmaps').each(function () {
	$(this).magnificPopup({
		type: 'iframe',
		mainClass: 'mfp-fade',
	});
});

/*------------------------------------
 Parallax Background
 -------------------------------------- */
$(".parallax").each(function () {
	$(this).parallaxie({
		speed: 0.5,
	});
});

/*------------------------------------
 Counter
 -------------------------------------- */
$(".counter").each(function () {
	$(this).appear(function () {
		$(this).countTo({
			speed: 1800,
		});
	});
});

/*------------------------------------
    YTPlayer YouTube Background
-------------------------------------- */

$(".player").each(function () {
    $(this).mb_YTPlayer();
});

/*------------------------
   tooltips
-------------------------- */
var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl)
})

/*------------------------
 Scroll to top
 -------------------------- */
$(function () {
	$(window).on('scroll', function () {
		if ($(this).scrollTop() > 400) {
			$('#back-to-top').fadeIn();
		} else {
			$('#back-to-top').fadeOut();
		}
	});
});
$('#back-to-top').on("click", function () {
	$('html, body').animate({scrollTop: 0}, 'slow');
	return false;
});


/*------------------------
   Subscribe Form
--------------------------- */
var subscribeForm = $('.subscribe-form');
var submitbtn = $('#subscribe-form-submit');
if (subscribeForm.length >= 1) {
subscribeForm.each( function(){
	var el = $(this),
		elResult = el.find('.subscribe-form-result'),
		alertType;
	el.find('form').validate({
		submitHandler: function(form) {
			elResult.hide();
			var loadingText = '<i role="status" aria-hidden="true" class="spinner-border spinner-border-sm"></i>';
			if (submitbtn.html() !== loadingText) {
				submitbtn.data('original-text', submitbtn.html());
				submitbtn.html(loadingText);
			}
			$(form).ajaxSubmit({
				target: elResult,
				dataType: 'json',
				resetForm: true,
				success: function( data ) {
					if( data.alert == 'error' ) {
						alertType = 'alert-danger';
					} else {
						alertType = 'alert-success';
					}
					elResult.addClass( 'alert ' + alertType ).html( data.message ).slideDown( 300 );
					submitbtn.html(submitbtn.data('original-text'));// reset submit button text
					setTimeout(function(){
						elResult.slideUp('slow', function(){
							$(this).removeClass( 'alert ' + alertType );
						});
					}, 5100);
				}
			});
		}
	});
});
}

/*------------------------
  Contact Form
--------------------------- */
	
var form = $('#contact-form'); // contact form
var submit = $('#submit-btn'); // submit button

// form submit event
form.on('submit', function (e) {
	e.preventDefault(); // prevent default form submit

	if (typeof $('#google-recaptcha-v3').val() != "undefined") {
		grecaptcha.ready(function () {
			var site_key = $('#google-recaptcha-v3').attr('src').split("render=")[1];
			grecaptcha.execute(site_key, {action: 'contact'}).then(function (token) {
				var gdata = form.serialize() + '&g-recaptcha-response=' + token;
				$.ajax({
					url: 'php/mail.php',  // form action url
					type: 'POST', 		  // form submit method get/post
					dataType: 'json', 	  // request type html/json/xml
					data: gdata, 		  // serialize form data
					beforeSend: function () {
						submit.attr("disabled", "disabled");
						var loadingText = '<span role="status" aria-hidden="true" class="spinner-border spinner-border-sm me-2"></span>Sending.....'; // change submit button text
						if (submit.html() !== loadingText) {
							submit.data('original-text', submit.html());
							submit.html(loadingText);
						}
					},
					success: function (data) {
						submit.before(data.Message).fadeIn("slow"); // fade in response data 
						submit.html(submit.data('original-text'));// reset submit button text
						submit.removeAttr("disabled", "disabled");
						if (data.response == 'success') {
							form.trigger('reset'); // reset form
						}
						setTimeout(function () {
							$('.alert-dismissible').fadeOut('slow', function(){
								$(this).remove();
							});
						}, 4000);
					},
					error: function (e) {
						console.log(e)
					}
				});
			});
		});
	} else {
		$.ajax({
			url: 'php/mail.php', // form action url
			type: 'POST', // form submit method get/post
			dataType: 'json', // request type html/json/xml
			data: form.serialize(), // serialize form data
			beforeSend: function () {
				submit.attr("disabled", "disabled");
				var loadingText = '<span role="status" aria-hidden="true" class="spinner-border spinner-border-sm me-2"></span>Sending.....'; // change submit button text
				if (submit.html() !== loadingText) {
					submit.data('original-text', submit.html());
					submit.html(loadingText);
				}
			},
			success: function (data) {
				submit.before(data.Message).fadeIn("slow"); // fade in response data 
				submit.html(submit.data('original-text'));// reset submit button text
				submit.removeAttr("disabled", "disabled");
				if (data.response == 'success') {
					form.trigger('reset'); // reset form
				}
				setTimeout(function () {
					$('.alert-dismissible').fadeOut('slow', function(){
						$(this).remove();
					});
				}, 4000);
				if (typeof $('#recaptcha-v2').val() != "undefined") {
					grecaptcha.reset(); // reset reCaptcha
				}
			},
			error: function (e) {
				console.log(e)
			}
		});
	}
});

/*------------------------------------
   Customizations
-------------------------------------- */

$(".year").each(function () {
    const now = new Date();
    const year = now.getFullYear();
    $(this).text(year);
});

})(jQuery)