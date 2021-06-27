<?php

$PdfUploadFolder = 'app_uploads/';
 
$ServerURL = 'http://192.168.1.34//moodle/mod/exam/'.$PdfUploadFolder;
$userID = $_POST['userID'];
$examID = $_POST['examID'];
$fecha = $_POST['fechaEntrega'];

if($_SERVER['REQUEST_METHOD']=='POST'){
 
    if(isset($_POST['name']) and isset($_FILES['pdf']['name'])){

        $conn = mysqli_connect("localhost", "admin", "admin", "moodle");
 
        $PdfName = $_POST['name'];
 
        $PdfInfo = pathinfo($_FILES['pdf']['name']);
 
        $PdfFileExtension = $PdfInfo['extension'];
 
        $PdfFileURL = $ServerURL . GenerateFileNameUsingID() . '.' . $PdfFileExtension;
 
        $PdfFileFinalPath = $PdfUploadFolder . GenerateFileNameUsingID() . '.'. $PdfFileExtension;
 
        try{
            move_uploaded_file($_FILES['pdf']['tmp_name'],$_SERVER['DOCUMENT_ROOT'] . "/moodle/mod/exam/" . $PdfFileFinalPath);
            
            $InsertTableSQLQuery = "INSERT INTO `mdl_exam_files` (`id`, `pdf_url`, `pdf_name`, `user_id`, `exam_id`, `fecha_entrega`) 
            VALUES (NULL, '$PdfFileURL', '$PdfName', '$userID', '$examID', '$fecha')";

            mysqli_query($conn,$InsertTableSQLQuery);



        }catch(Exception $e){} 
            mysqli_close($conn);
        
        }
}


function GenerateFileNameUsingID(){
    $conn2 = mysqli_connect("localhost", "admin", "admin", "moodle");
    
    $GenerateFileSQL = "SELECT max(id) as id FROM mdl_exam_files";
    
    $Holder = mysqli_fetch_array(mysqli_query($conn2,$GenerateFileSQL));

    mysqli_close($conn2);
    
    if($Holder['id']==null){
        return 1;
    }
    else{
        return ++$Holder['id'];
    }
}