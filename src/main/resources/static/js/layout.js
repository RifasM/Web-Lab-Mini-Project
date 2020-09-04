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