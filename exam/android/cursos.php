<?php

$conn = mysqli_connect("localhost", "JoanTest", "JoanTest", "moodle");
$result = array();

$id_user = $_POST['id_user'];

$sql = "SELECT u.id, u.firstname, c.id, c.fullname, c.idnumber 
FROM mdl_user u 
INNER JOIN mdl_role_assignments ra ON ra.userid = u.id 
INNER JOIN mdl_context ct ON ct.id = ra.contextid 
INNER JOIN mdl_course c ON c.id = ct.instanceid 
INNER JOIN mdl_role r ON r.id = ra.roleid 
WHERE u.id = $id_user";


$conn->query("SET NAMES 'utf8'");
$res = mysqli_query($conn, $sql);
mysqli_data_seek ($res, 0);

if(mysqli_num_rows($res) > 0 ){
    while ($result_array = mysqli_fetch_assoc($res)) {
        $r = [
            'id' => $result_array['id'],
            'name' => $result_array['fullname'],
            'idnumber' => $result_array['idnumber']
        ];

        array_push($result, $r);
        }
       
}

echo json_encode($result, JSON_FORCE_OBJECT, JSON_UNESCAPED_UNICODE);

mysqli_close($conn);