<?php
$public = $_POST['pub'];
$signature = $_POST['signature'];
$data = $_POST['data'];
$userid = $_POST['userid'];

$res = openssl_verify($data, base64_decode($signature), $public, 'sha256WithRSAEncryption');

$challange = "";
if ($res == 1) {
    $challange = "true";
} else {
    if ($res == 0) {
        $challange = "false";
    } else {
        $challange = "An error has occurred.";
    }
}

if ($challange == 'true') {
    $conn = mysqli_connect("localhost", "JoanTest", "JoanTest", "moodle");
    $sql = "INSERT INTO `mdl_exam_huella` (`userid`, `public_key`) VALUES ('$userid', '$public')";

    if ( mysqli_query($conn, $sql) ) {
        mysqli_close($conn);
        $insert = "true";
    } else {
        mysqli_close($conn);
        $insert = "false";
    }
}

$response = [
    'challenge' => $challange,
    'insert' => $insert,
];

echo json_encode($response, JSON_FORCE_OBJECT);
