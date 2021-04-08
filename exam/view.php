<?php
require(__DIR__.'/../../config.php');
require_once(__DIR__.'/lib.php');
require_once($CFG->libdir.'/completionlib.php');

$id        = optional_param('id', 0, PARAM_INT);        // Course Module ID
$bid       = optional_param('b', 0, PARAM_INT);         // Book id
//$edit      = optional_param('edit', -1, PARAM_BOOL);    // Edit mode


// =========================================================================
// security checks START - teachers edit; students view
// =========================================================================
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

echo $OUTPUT->header();
echo $OUTPUT->heading(format_string($exam->name));

if ($exam->timeopen > time()){
    // Printamos que el examen no ha comenzado
    $tiempo_restante = strtotime($exam->timeopen);
    $templatecontext = (object)[
        'timeopen' => $tiempo_restante,
    ];
    echo $OUTPUT->render_from_template('exam/manage', $templatecontext);
}else{
    // Miramos si se ha entregado ho no.
}

echo $OUTPUT->footer();