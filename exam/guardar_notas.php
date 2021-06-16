<?php
require(__DIR__.'/../../config.php');
# $ids = $_POST['ids'];

global $DB, $CFG;

$i = 0;
$notas = "";
$nota_final = 0;
foreach ($_POST as $post) {
    if ($i == 0) {
        $ids = explode('-', $_POST['ids']);
        $examid = $ids[0];
        $studentid = $ids[1];
        $result = $DB->get_record('exam_validacion', array('examid'=>$examid, 'userid'=>$studentid), '*', MUST_EXIST);
    } else {
        if ($i != count($_POST)-1) {
            $notas = $notas . $post . "-";
        } else {
            $notas = $notas . $post;
        }

        $nota_final = $nota_final + floatval($post);
        echo " ";
    }
    $i++;
}
$result->notas = $notas;
$result->nota_final = $nota_final;

$DB->update_record('exam_validacion', $result);
$url =$CFG->wwwroot . '/mod/exam/validar_estudiante.php?examid=' . $examid;
try {
    redirect($url);
} catch (moodle_exception $e) {
}