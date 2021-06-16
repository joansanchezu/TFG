<?php
require(__DIR__.'/../../config.php');
global $DB;

$user_id = $_POST['user_id'];
$validacion = $_POST['validacion'];
$examid = $_POST['examid'];

$user_val = $DB->get_record('exam_validacion', array('userid'=>$user_id, 'examid'=>$examid));

$estado = false;
if ($user_val->estado >= 0) {
    $user_val->estado = $validacion;
    $DB->update_record('exam_validacion', $user_val);
    $estado = true;
}

$json = [
    'estado' => $estado,
];
echo json_encode($json, JSON_FORCE_OBJECT);
