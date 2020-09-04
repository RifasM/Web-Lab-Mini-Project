jQuery(document).ready(function ($) {
    /*if( $(window).width() > 1000) {
        if( $.cookie( 'gillion.sidebartest', { path: '', domain: 'jevelin.shufflehound.com' } ) == 'opened' ) {
            $('.sh-side-demos').addClass('open');
        } else {
            $('.sh-side-demos').removeClass('open');
        }
    }*/

    $('.sh-side-options-item-trigger-demos').on('click', function () {
        $('.sh-side-options').css('transition', '0.3s all ease-in-out');

        if ($('.sh-side-options').hasClass('open')) {
            $('.sh-side-options').removeClass('open');
        } else {
            $('.sh-side-options .sh-side-demos-image').each(function () {
                $(this).attr('src', $(this).attr('data-src'));
            });
            $('.sh-side-options').addClass('open');
        }
    });

    $('.sh-side-demos-container-close').on('click', function () {
        $('.sh-side-options').css('transition', '0.3s all ease-in-out');
        $('.sh-side-options').removeClass('open');
    });
});

var c = document.body.className;
c = c.replace(/woocommerce-no-js/, 'woocommerce-js');
document.body.className = c;

/* <![CDATA[ */
var wpcf7 = {
    "apiSettings": {
        "root": "https:\/\/jevelin.shufflehound.com\/blog1\/wp-json\/contact-form-7\/v1",
        "namespace": "contact-form-7\/v1"
    }
};
/* ]]> */

/* <![CDATA[ */
var woocommerce_params = {
    "ajax_url": "\/blog1\/wp-admin\/admin-ajax.php",
    "wc_ajax_url": "\/blog1\/?wc-ajax=%%endpoint%%"
};
/* ]]> */

/* <![CDATA[ */
var wc_cart_fragments_params = {
    "ajax_url": "\/blog1\/wp-admin\/admin-ajax.php",
    "wc_ajax_url": "\/blog1\/?wc-ajax=%%endpoint%%",
    "cart_hash_key": "wc_cart_hash_2469ca9463be8c946f2508971efb1c5e",
    "fragment_name": "wc_fragments_2469ca9463be8c946f2508971efb1c5e",
    "request_timeout": "5000"
};
/* ]]> */
