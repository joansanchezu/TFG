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
        # $pdf = $DB->get_record('exam_pdf', array('userid'=>$student->id,'examid'=>$examid), '*', MUST_EXIST);
        echo $OUTPUT->render_from_template('exam/exam_validacion_ref', (object)['param'=>'27 de mayo de 2021, 15:49', 'href'=>$student->examdir]);

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
