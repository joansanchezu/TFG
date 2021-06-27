<?php
require(__DIR__.'/../../config.php');

global $DB, $USER, $OUTPUT, $PAGE, $CFG;

//$PAGE->set_url('/mod/exam/validar_estudiante.php', array('id'=>$id));

$PAGE->requires->jquery();
$PAGE->requires->js( new moodle_url($CFG->wwwroot . '/mod/exam/js/validacion.js') );
$PAGE->requires->js( new moodle_url($CFG->wwwroot . '/mod/exam/js/publicar_notas.js') );
$PAGE->requires->js( new moodle_url($CFG->wwwroot . '/mod/exam/js/exportar_notas.js') );

echo $OUTPUT->header();
echo $OUTPUT->heading('Validación de estudiante');

$parameters = array(
    'Usuario',
    'Nombre',
    'Entregado el',
    'Estatus',
    'Validar/Denegar',
    'Notas',
);

$examid = $_GET['examid'];
$students = $DB->get_records('exam_validacion', array('examid' => $examid));
$exam = $DB->get_record('exam', array('id'=>$examid), '*', MUST_EXIST);

/*for ($i = 1; $i <= $exam->num_preguntas; $i++) {
    array_push($parameters, 'Ex ' . strval($i));
}*/

//array_push($parameters, 'Guardar');

echo $OUTPUT->render_from_template('exam/table1', (object)[]);

foreach ($parameters as $param) {
    echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param' => $param]);
}

echo $OUTPUT->render_from_template('exam/table2', (object)[]);

foreach ($students as $student) {
    if ($student->estado > 0) {
        echo $OUTPUT->render_from_template('exam/table1', (object)[]);

        $user = $DB->get_record('user', array('id' => $student->userid), '*', MUST_EXIST);
        $nombre = $user->firstname . ' ' . $user->lastname;
        echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param'=>$user->username]);
        echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param'=>$nombre]);

        $pdf = $DB->get_record('exam_files', array('user_id' => $student->userid, 'exam_id' => $examid), '*', MUST_EXIST);
        $fecha = explode('_', $pdf->fecha_entrega);

        $año = substr($fecha[0], 0, 4);
        $mes = substr($fecha[0], 4, 2);
        $dia = substr($fecha[0], 6, 2);

        $hora = substr($fecha[1], 0, 2);
        $min = substr($fecha[1], 2, 2);
        $seg = substr($fecha[1], 4, 2);

        switch ($mes) {
            case "01":
                $mes = "Enero";
                break;
            case "02":
                $mes = "Febrero";
                break;
            case "03":
                $mes = "Marzo";
                break;
            case "04":
                $mes = "Abril";
                break;
            case "05":
                $mes = "Mayo";
                break;
            case "06":
                $mes = "Junio";
                break;
            case "07":
                $mes = "Julio";
                break;
            case "08":
                $mes = "Agosto";
                break;
            case "09":
                $mes = "Septiembre";
                break;
            case "10":
                $mes = "Octubre";
                break;
            case "11":
                $mes = "Noviembre";
                break;
            case "12":
                $mes = "Diciembre";
                break;
        }
        $fecha_completa = $dia . " de " . $mes . " del " . $año . " a las " . $hora . ":" . $min . ":" . $seg;
        echo $OUTPUT->render_from_template('exam/exam_validacion_ref', (object)['param'=>$fecha_completa, 'href'=>$pdf->pdf_url]);

        $estado = "";
        switch ($student->estado) {
            case 1:
                $estado = "Entregado";
                break;
            case 2:
                $estado = "Validado";
                break;
            case 3:
                $estado = "Denegado";
                break;
        }

        echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param'=>$estado]);
        echo $OUTPUT->render_from_template('exam/exam_validacion_boton', (object)['userid'=>$user->id, 'examid'=>$examid]);

        if ($student->estado == 2){
            echo $OUTPUT->render_from_template('exam/exam_form1', (object)['studentid'=>$student->userid, 'examid'=>$examid]);
            if ($student->notas != null) {
                $notas = explode('-', $student->notas);

                for ($i = 0; $i < $exam->num_preguntas; $i++) {
                    $ejercicio = "Ex" . strval($i + 1);
                    echo $OUTPUT->render_from_template('exam/exam_input_notas', (object)['ejer' => $ejercicio, 'nota'=>$notas[$i]]);
                }
            } else {
                for ($i = 0; $i < $exam->num_preguntas; $i++) {
                    $ejercicio = "Ex" . strval($i + 1);
                    echo $OUTPUT->render_from_template('exam/exam_input_notas', (object)['ejer' => $ejercicio]);
                }
            }

            echo $OUTPUT->render_from_template('exam/exam_form2', (object)[]);

        }
        echo $OUTPUT->render_from_template('exam/table2', (object)[]);
    }
}

echo $OUTPUT->render_from_template('exam/exam_publicar_notas', (object)['nombreid'=>'notas', 'examid'=>$examid, 'valor'=>'Publicar notas']);
echo $OUTPUT->render_from_template('exam/exam_publicar_notas', (object)['nombreid'=>'export', 'examid'=>$examid, 'valor'=>'Exportar notas']);

echo $OUTPUT->footer();
