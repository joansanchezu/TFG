<?php
$conn = mysqli_connect("localhost", "admin", "admin", "moodle");
$result = array();

$username = $_POST['username'];
$password = $_POST['password'];

$sql = "SELECT * FROM mdl_user WHERE username = $username";
$conn->query("SET NAMES 'utf8'");
$data = mysqli_query($conn, $sql);

$res = mysqli_fetch_assoc($data);

$valido = password_verify($password, $res['password']);
$r = [
    'valido' => $valido,
    'user' => $res,
];
mysqli_close($conn);

echo json_encode($r, JSON_FORCE_OBJECT);