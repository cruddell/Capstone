<?php
/**
 * Created by PhpStorm.
 * User: ChristopherRuddell
 * Date: 8/2/16
 * Time: 4:08 PM
 */

require_once("config.php");

function getPost($var) {
    global $data;
    $ret = "";
    if (isset($data[$var])) $ret = $data[$var];
    else if (isset($_POST[$var])) $ret = $_POST[$var];
    else if (isset($_GET[$var])) $ret = $_GET[$var];
    else if (isset($_REQUEST[$var])) $ret = $_REQUEST[$var];
    return $ret;
}

$data = json_decode(file_get_contents('php://input'), true);

$deviceId = getPost("deviceId");
$channel = getPost("channel");
$gcmToken = getPost("token");
$application = getPost("application");
$timeNow = time();
$channelId = -1;
$status = getPost("status");

//get channel id
$sql = "SELECT ID FROM " . TABLE_CHANNELS . " WHERE channelName = '$channel'";
$res = mysqli_query($mysqli, $sql);
if ($res) while ($row=mysqli_fetch_array($res)) {
    $channelId = $row["ID"];
}

$sql = "UPDATE " . TABLE_REGISTER_TOKEN . " SET active=? WHERE deviceId='$deviceId' AND application='$application' AND channel='$channelId'";
$tmpSqli = $mysqli->prepare($sql);
if ($tmpSqli===false) {
    echo $mysqli->error;
}
$tmpSqli->bind_param("i",$status);
$tmpSqli->execute();
$tmpSqli->close();

echo json_encode(["status"=>"ok"]);