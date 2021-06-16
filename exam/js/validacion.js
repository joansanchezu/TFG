(function ( $ ){
    $( '.btn' ).on("click", function (){
        var res = $(this).attr("id").split("-");
        var val = 0;

        var action = res[0];
        var examid = res[1];
        var id = res[2];

        if (action == 'val'){
            val = 2;
        } else {
            val = 3;
        }

       $.ajax({
           type: "post",
           url: "/moodle/mod/exam/validacion_exam.php",
           data: {
               user_id: id,
               examid: examid,
               validacion: val,
           },
           dataType: "json",
           success: function(response){
               var estado = response.estado;
               if (estado) {
                   location.reload();
               }
           }
       });
    });
})( jQuery );