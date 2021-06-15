<?php
class exam {
    public function __construct($coursemodulecontext, $coursemodule, $course){
        global $SESSION;

        $this->context = $coursemodulecontext;
        $this->course = $course;

        // Ensure that $this->coursemodule is a cm_info object (or null).
        $this->coursemodule = cm_info::create($coursemodule);

        // Temporary cache only lives for a single request - used to reduce db lookups.
        $this->cache = array();

        $this->submissionplugins = $this->load_plugins('assignsubmission');
        $this->feedbackplugins = $this->load_plugins('assignfeedback');

        // Extra entropy is required for uniqid() to work on cygwin.
        $this->useridlistid = clean_param(uniqid('', true), PARAM_ALPHANUM);

        if (!isset($SESSION->mod_assign_useridlist)) {
            $SESSION->mod_assign_useridlist = [];
        }
    }

    /**
     * View entire grading page.
     *
     * @return string
     */
    protected function view_grading_page() {
        global $CFG;

        $o = '';
        // Need submit permission to submit an assignment.
        $this->require_view_grades();
        require_once($CFG->dirroot . '/mod/assign/gradeform.php');

        $this->add_grade_notices();

        // Only load this if it is.
        $o .= $this->view_grading_table();

        $o .= $this->view_footer();

        \mod_assign\event\grading_table_viewed::create_from_assign($this)->trigger();

        return $o;
    }
}



/**
 * @return array string => lang string the options for handling overdue quiz
 *      attempts.
 */
function exam_get_professors_options():array {
    global $COURSE, $DB;
    $context = context_course::instance($COURSE->id);
    $role = $DB->get_record('role', array('shortname' => 'editingteacher'));
    $teachers = get_role_users($role->id, $context);
    $t = array();

    foreach ($teachers as $teacher){
        $name = $teacher->firstname . " " . $teacher->lastname;
        $t[$teacher->username] = $name;
    }
    return $t;
}


/**
 * @return array string => lang string the options for handling overdue quiz
 *      attempts.
 */
function exam_get_students():array {
    global $COURSE, $DB;
    $context = context_course::instance($COURSE->id);
    $role = $DB->get_record('role', array('shortname' => 'student'));
    $student = get_role_users($role->id, $context);
    return $student;
}
