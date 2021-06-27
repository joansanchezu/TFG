<?php

$conn = mysqli_connect("localhost", "admin", "admin", "moodle");
$result = array();

$id_curso = $_POST['id_curso'];

$sql = "SELECT `id`,`name`, DATE_FORMAT(FROM_UNIXTIME(`timeopen`),'%Y-%m-%d %k:%i') AS abierto, DATE_FORMAT(FROM_UNIXTIME(`timeclose`),'%Y-%m-%d %k:%i') AS cerrado 
FROM `mdl_exam` 
WHERE `courseid` = '$id_curso'
ORDER BY `abierto` ASC";


$conn->query("SET NAMES 'utf8'");
$res = mysqli_query($conn, $sql);
mysqli_data_seek ($res, 0);

if(mysqli_num_rows($res) > 0 ){
    while ($result_array = mysqli_fetch_assoc($res)) {
        $r = [
            'id' => $result_array['id'],
            'name' => $result_array['name'],
            'abierto' => $result_array['abierto'],
            'cerrado' => $result_array['cerrado']
        ];

        array_push($result, $r);
        }
       
}

echo json_encode($result, JSON_FORCE_OBJECT, JSON_UNESCAPED_UNICODE);

mysqli_close($conn);