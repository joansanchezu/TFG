<?php

require(__DIR__.'/../../config.php');
global $DB;

$result = $DB->get_record('exam', array('id'=>$_POST['examid']), '*', MUST_EXIST);
$result->publicar_notas = 1;

$DB->update_record('exam', $result);
