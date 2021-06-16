<?php
/**
 * @package exam
 * @author Víctor
 * @var stdClass $plugin
 */

defined('MOODLE_INTERNAL') || die;
require_once(__DIR__.'/../../lib/moodlelib.php');

/**
 * Adds an exam instance
 *
 * This is done by calling the add_instance() method of the assignment type class
 * @param stdClass $data
 * @return int The instance id of the new assignment
 * @throws dml_exception
 */
function exam_add_instance($data){
    /*
     * Crear un objeto de la c
     *
     * */
    global $DB, $COURSE, $USER;

    if ($data->timeopen >= $data->timeclose){
        // Error
        \core\notification::error(get_string('examerrortime', 'exam'));
    }else{
        $data->timecreated = time();
        $data->timemodified = $data->timecreated;
        $data->courseid = $COURSE->id;
        $data->userid = $USER->id;
        $data->publicar_notas = 0;

        $data->teacherid = $DB->get_record_sql('SELECT id FROM {user} WHERE username = ?', array($data->teacherid))->id;
        $id = $DB->insert_record('exam', $data);

        $context = context_course::instance($COURSE->id);
        $role = $DB->get_record('role', array('shortname' => 'student'));
        $students = get_role_users($role->id, $context);
        $validacio = new stdClass();

        foreach ($students as $student){
            $validacio->courseid = $COURSE->id;
            $validacio->userid = $student->id;
            $validacio->examid = $id;
            $validacio->examdir = '/';
            $validacio->estado = "0";
            $validacio->validado = "0";
            $id_v = $DB->insert_record('exam_validacion', $validacio);
        }

        return $id;
    }
}

/**
 * Update exam auth instance.
 *
 * @param stdClass $data
 * @param stdClass $mform
 * @return bool true
 */
function exam_update_instance($data, $mform) {
    global $DB;
    $data->timemodified = time();
    $data->id = $data->instance;

    $DB->update_record('exam', $data);

    return true;
}

/**
 * Delete exam instance by activity id
 *
 * @param int $id
 * @return bool success
 */
function exam_delete_instance($id) {
    global $DB;

    if (!$exam = $DB->get_record('exam', array('id'=>$id))) {
        return false;
    }

    $cm = get_coursemodule_from_instance('exam', $id);
    \core_completion\api::update_completion_date_event($cm->id, 'exam', $id, null);

    $DB->delete_records('validacion', array('exam'=>$exam->id));
    $DB->delete_records('exam', array('id'=>$exam->id));

    return true;
}

/**
 * extend an assigment navigation settings
 *
 * @param settings_navigation $settings
 * @param navigation_node $navref
 * @return void
 */
function exam_extend_settings_navigation(settings_navigation $settings, navigation_node $navref) {
    /*global $USER, $PAGE, $OUTPUT;

    if ($booknode->children->count() > 0) {
        $firstkey = $booknode->children->get_key_list()[0];
    } else {
        $firstkey = null;
    }

    $params = $PAGE->url->params();

    if ($PAGE->cm->modname === 'book' and !empty($params['id']) and !empty($params['chapterid'])
        and has_capability('mod/book:edit', $PAGE->cm->context)) {
        if (!empty($USER->editing)) {
            $string = get_string("turneditingoff");
            $edit = '0';
        } else {
            $string = get_string("turneditingon");
            $edit = '1';
        }
        $url = new moodle_url('/mod/book/view.php', array('id'=>$params['id'], 'chapterid'=>$params['chapterid'], 'edit'=>$edit, 'sesskey'=>sesskey()));
        $editnode = navigation_node::create($string, $url, navigation_node::TYPE_SETTING);
        $booknode->add_node($editnode, $firstkey);
        $PAGE->set_button($OUTPUT->single_button($url, $string));
    }

    $plugins = core_component::get_plugin_list('booktool');
    foreach ($plugins as $plugin => $dir) {
        if (file_exists("$dir/lib.php")) {
            require_once("$dir/lib.php");
        }
        $function = 'booktool_'.$plugin.'_extend_settings_navigation';
        if (function_exists($function)) {
            $function($settingsnav, $booknode);
        }
    }*/
}