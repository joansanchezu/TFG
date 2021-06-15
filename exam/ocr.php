<?php
try {
    $url = "http://127.0.0.1:8080";
    $token = "eda55fa11b88957";

    $img_path = "numeros/n1-0.png";
    $im = file_get_contents($img_path);
    $im_encoded = base64_encode($im);

    $im_encoded = "data:image/png;base64," . $im_encoded;

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POSTFIELDS, "apikey=$token&base64Image=$im_encoded");
    $response = curl_exec($ch);

    curl_close($ch);
} catch (Exception $e) {
}