<?php
$conn = mysqli_connect("localhost", "admin", "admin", "moodle");
$result = array();

$userid = $_POST['userid'];
$sql = "SELECT * FROM mdl_exam_huella WHERE userid = $userid";
$conn->query("SET NAMES 'utf8'");
$data = mysqli_query($conn, $sql);

$res = mysqli_fetch_assoc($data);

if ($res != null) {
    $valido = "true";
} else {
    $valido = "false";
}

$response = [
    'huella' => $valido,
];

mysqli_close($conn);

echo json_encode($response, JSON_FORCE_OBJECT);
