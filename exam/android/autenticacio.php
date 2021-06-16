<?php
$signature = $_POST['signature'];
$challenge_str = $_POST['data'];
$userid = $_POST['userid'];

$conn = mysqli_connect("localhost", "admin", "admin", "moodle");
$sql = "SELECT public_key FROM mdl_exam_huella WHERE userid = $userid;";
$conn->query("SET NAMES 'utf8'");
$data = mysqli_query($conn, $sql);
$res = mysqli_fetch_assoc($data);
mysqli_close($conn);

$public = $res['public_key'];

$verify = openssl_verify($challenge_str, base64_decode($signature), $public, 'sha256WithRSAEncryption');

$challange = "";
if ($verify == 1) {
    $challange = "true";
} else {
    if ($verify == 0) {
        $challange = "false";
    } else {
        $challange = "An error has occurred.";
    }
}

$json = [
    'challenge' => $challange,
];

echo json_encode($json, JSON_FORCE_OBJECT);
