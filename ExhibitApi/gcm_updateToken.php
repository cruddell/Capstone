<?php
/**
 * Created by PhpStorm.
 * User: ChristopherRuddell
 * Date: 7/22/16
 * Time: 8:03 AM
 */

require_once("config.php");

function getPost($var) {
    $ret = "";
    if (isset($_POST[$var])) $ret = $_POST[$var];
    else if (isset($_GET[$var])) $ret = $_GET[$var];
    else if (isset($_REQUEST[$var])) $ret = $_REQUEST[$var];
    return $ret;
}

$deviceId = getPost("deviceId");
$gcmToken = getPost("token");
$timeNow = time();


if (strlen($deviceId)>0 && strlen($gcmToken)>0) {

    $sql = "UPDATE " . TABLE_REGISTER_TOKEN . " `gcmToken`=?, `updatedAt`=? WHERE deviceId='$deviceId'";
    $tmpSqli = $mysqli->prepare($sql);
    $tmpSqli->bind_param("ss", $gcmToken, $timeNow);
    $tmpSqli->execute();
    $tmpSqli->close();

    return json_encode(["status"=>"ok"]);
}
else return json_encode(["status"=>"failed"]);