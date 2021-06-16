(function ( $ ){
    $( '.export' ).on("click", function (){
        var res = $(this).attr("id").split("-");
        $.ajax({
            type: "post",
            url: "/moodle/mod/exam/exportar_notas.php",
            data: {
                examid: res[1],
            },
            dataType: "json",
        });
    });
})( jQuery );