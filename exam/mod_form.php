<?php

if (!defined('MOODLE_INTERNAL')) {
    die('Direct access to this script is forbidden.');    ///  It must be included from a Moodle page
}

require_once($CFG->dirroot.'/course/moodleform_mod.php');
require_once($CFG->dirroot.'/mod/exam/lib.php');
require_once($CFG->dirroot.'/mod/exam/locallib.php');

class mod_exam_mod_form extends moodleform_mod {
    public static $datefieldoptions = array('optional' => true);

    function definition() {
        $mform = $this->_form;

        $mform->addElement('header', 'general', get_string('general', 'form'));

        $mform->addElement('text', 'name', get_string('examname', 'exam'), array('size'=>'64'));
        $mform->setType('name', PARAM_TEXT);
        $mform->addRule('name', null, 'required', null, 'client');
        $mform->addRule('name', get_string('maximumchars', '', 255), 'maxlength', 255, 'client');

        //$mform->addElement('header', 'timing', get_string('timing', 'form'));

        // Open and close dates.
        $mform->addElement('date_time_selector', 'timeopen', get_string('examopen', 'exam'), self::$datefieldoptions);
        $mform->setDefault('timeopen', get_string('strftimedatetimeshort', 'langconfig'));
        $mform->addRule('timeopen', null, 'required', null,'client');
        //$mform->addHelpButton('timeopen', 'quizopenclose', 'quiz');

        $mform->addElement('date_time_selector', 'timeclose', get_string('examclose', 'exam'), self::$datefieldoptions);
        $mform->setDefault('timeopen', get_string('strftimedatetimeshort', 'langconfig'));
        $mform->addRule('timeclose', null, 'required', null,'client');

        $mform->addElement('select', 'teacherid', get_string('examteacher', 'exam'), exam_get_professors_options());
        $mform->addRule('teacherid', null, 'required', null,'client');

        $mform->addElement('select', 'num_preguntas', get_string('exampreguntas', 'exam'), [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]);
        $mform->addRule('num_preguntes', null, 'required', null,'client');

        $this->standard_coursemodule_elements();

        $this->add_action_buttons();
    }
}