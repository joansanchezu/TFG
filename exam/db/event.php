<?php
$observers = array(
    array(
        'eventname' => '\mod_exam\event\attempt_submitted',
        'includefile' => '/mod/exam/locallib.php',
        'callback' => 'exam_attempt_submitted_handler',
        'internal' => false,
    ),
);