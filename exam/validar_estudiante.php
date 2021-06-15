<?php
require(__DIR__.'/../../config.php');


global $DB, $USER, $OUTPUT, $PAGE;

//$PAGE->set_url('/mod/exam/validar_estudiante.php', array('id'=>$id));

$students = $DB->get_records('exam_validacion', array('examid' => 2));

echo $OUTPUT->header();
echo $OUTPUT->heading('Validación de estudiante');

$parameters = array(
    'Usuario',
    'Nombre',
    'Fecha',
    'Estatus',
    'Examen',
    'Validar/Denegar',
);

echo $OUTPUT->render_from_template('exam/table1', (object)[]);

foreach ($parameters as $param) {
    echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param' => $param]);
}

echo $OUTPUT->render_from_template('exam/table2', (object)[]);

foreach ($students as $student) {
    echo $OUTPUT->render_from_template('exam/table1', (object)[]);

    $user = $DB->get_record('user', array('id' => $student->userid), '*', MUST_EXIST);
    $nombre = $user->firstname . ' ' . $user->lastname;
    echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param'=>$user->username]);
    echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param'=>$nombre]);
    echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param'=>'27 de mayo de 2021, 15:49']);
    echo $OUTPUT->render_from_template('exam/exam_validacion', (object)['param'=>$student->estado]);
    echo $OUTPUT->render_from_template('exam/exam_validacion_ref', (object)['param'=>$student->examdir]);
    echo $OUTPUT->render_from_template('exam/exam_validacion_boton', (object)[]);

    echo $OUTPUT->render_from_template('exam/table2', (object)[]);
}

echo $OUTPUT->footer();
