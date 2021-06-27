<?php

global $CFG;

require(__DIR__.'/../../config.php');
require_once(__DIR__.'/lib.php');
require_once($CFG->libdir.'/completionlib.php');

$id        = optional_param('id', 0, PARAM_INT);        // Course Module ID
$bid       = optional_param('b', 0, PARAM_INT);         // Exam id
//$edit      = optional_param('edit', -1, PARAM_BOOL);    // Edit mode

// =========================================================================
// security checks START - teachers edit; students view
// =========================================================================

global $USER, $DB, $PAGE, $OUTPUT;

if ($id) {
    $cm = get_coursemodule_from_id('exam', $id, 0, false, MUST_EXIST);
    $course = $DB->get_record('course', array('id'=>$cm->course), '*', MUST_EXIST);
    $exam = $DB->get_record('exam', array('id'=>$cm->instance), '*', MUST_EXIST);
} else {
    $exam = $DB->get_record('exam', array('id'=>$bid), '*', MUST_EXIST);
    $cm = get_coursemodule_from_instance('exam', $exam->id, 0, false, MUST_EXIST);
    $course = $DB->get_record('course', array('id'=>$cm->course), '*', MUST_EXIST);
    $id = $cm->id;
}

require_login();

$PAGE->set_url('/mod/exam/view.php', array('id'=>$id));

$context = context_course::instance($course->id);
$role = $DB->get_record('role', array('shortname' => 'editingteacher'));
$teachers = get_role_users($role->id, $context);

foreach ($teachers as $t){
    if ($t->id == $exam->teacherid){
        $teacher = $t;
        break;
    }
}

$role = $DB->get_record('role', array('shortname' => 'student'));
$students = get_role_users($role->id, $context);

// Unset all page parameters.
unset($id);
unset($bid);
//unset($edit);

// prepare header
$pagetitle = $exam->name;
$PAGE->set_title($pagetitle);
$PAGE->set_heading($course->fullname);

// =====================================================
// Book display HTML code
// =====================================================

if ($USER->id == $teacher->id){
    echo $OUTPUT->header();
    echo $OUTPUT->heading(format_string($exam->name));

    echo $OUTPUT->heading(format_string("Sumario de calificaciones"));

    $noentregado = $DB->get_records('exam_validacion', array('examid'=>$exam->id, 'estado'=>0));
    $entregado = $DB->get_records('exam_validacion', array('examid'=>$exam->id, 'estado'=>1));
    $validado = $DB->get_records('exam_validacion', array('examid'=>$exam->id, 'estado'=>2));
    $denegado = $DB->get_records('exam_validacion', array('examid'=>$exam->id, 'estado'=>3));

    $url = '/mod/exam/validar_estudiante.php?examid=' . $exam->id;
    $templatecontext = (object)[
        'validarurl' => new moodle_url($url),
        'numpart' => count($students),
        'numnoent' => count($noentregado),
        'nument' => count($entregado),
        'numval' => count($validado),
        'numden' => count($denegado),
    ];

    echo $OUTPUT->render_from_template('exam/exam_view', $templatecontext);

    echo $OUTPUT->footer();
} else{
    echo $OUTPUT->header();
    echo $OUTPUT->heading(format_string($exam->name));

    if (false){//$exam->timeopen > time()){
        // Printamos que el examen no ha comenzado
        $tiempo_restante = strtotime($exam->timeopen);
        $templatecontext = (object)[
            'timeopen' => $tiempo_restante,
        ];
        echo $OUTPUT->render_from_template('exam/manage', $templatecontext);
    }else{
        // Miramos si se ha entregado ho no.
        $res = $DB->get_record('exam_validacion', array('examid'=>$exam->id, 'userid'=>$USER->id), '*', MUST_EXIST);
        if ($exam->publicar_notas) {
            echo $OUTPUT->render_from_template('exam/exam_notas_publicadas', (object)[] );
        } else {
            if ($res->estado == 2) {
                echo "HAOIHIOAH";
            }
        }
    }

    echo $OUTPUT->footer();
}
