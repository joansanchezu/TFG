<?php

$conn = mysqli_connect("localhost", "JoanTest", "JoanTest", "moodle");
$result = array();

/*
var_dump($_SERVER["REQUEST_METHOD"]);
var_dump($_POST);
var_dump($_GET);
var_dump($_REQUEST);
*/

$username = $_POST['username'];
$password = $_POST['password'];

$sql = "SELECT `id`,`password` FROM `mdl_user` WHERE `username` = '$username'";
$conn->query("SET NAMES 'utf8'");
$data = mysqli_query($conn, $sql);
mysqli_data_seek ($data, 0);

$res = mysqli_fetch_assoc($data);


if (password_verify($password, $res['password'])){
    $r = [
        'valido' => 'true',
        'id_user' => $res['id']
    ];
}else{
    $r = [
        'valido' => 'false',
        'user' => $username,
        'password' => $password
        
    ];
}

echo json_encode($r, JSON_FORCE_OBJECT);

echo json_encode($result, JSON_FORCE_OBJECT);
mysqli_close($conn);