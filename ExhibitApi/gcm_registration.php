<?php
/**
 * Created by PhpStorm.
 * User: ChristopherRuddell
 * Date: 7/15/16
 * Time: 8:22 AM
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


if (strlen($deviceId)>0 && strlen($channel)>0 && strlen($gcmToken)>0 && strlen($application)>0) {
    //get channel id
    $sql = "SELECT ID FROM " . TABLE_CHANNELS . " WHERE channelName = '$channel'";
    $res = mysqli_query($mysqli, $sql);
    if ($res) while ($row=mysqli_fetch_array($res)) {
        $channelId = $row["ID"];
    }

    //check if device has previously registered - if so, update token
    $newChannelRegistration = true;
    $dataDirty = false;

    $sql = "SELECT ID, channel, gcmToken FROM " . TABLE_REGISTER_TOKEN . " WHERE `deviceId`='$deviceId'";
    $res = mysqli_query($mysqli, $sql);
    if ($res) while ($row=mysqli_fetch_array($res)) {
        $ID = $row["ID"];
        $oldChannel = $row["channel"];
        $oldToken = $row["gcmToken"];

        if ($gcmToken!=$oldToken) {
            //needs to be updated
            $dataDirty = true;

            //check if old channel registration is for same channel as this request
            if ($channel==$oldChannel) $newChannelRegistration = false;
        }
    }

    if ($newChannelRegistration) {
        $sql = "INSERT INTO " . TABLE_REGISTER_TOKEN . " (`deviceId`, `channel`, `gcmToken`, `application`, `updatedAt`) VALUES (?,?,?,?,?)";
        $tmpSqli = $mysqli->prepare($sql);
        $tmpSqli->bind_param("sssss", $deviceId, $channelId, $gcmToken, $application, $timeNow);
        $tmpSqli->execute();
        $tmpSqli->close();
    }

    if ($dataDirty) {
        $sql = "UPDATE " . TABLE_REGISTER_TOKEN . " `gcmToken`=?, `updatedAt`=? WHERE deviceId='$deviceId'";
        $tmpSqli = $mysqli->prepare($sql);
        $tmpSqli->bind_param("ss", $gcmToken, $timeNow);
        $tmpSqli->execute();
        $tmpSqli->close();
    }

    echo json_encode(["status"=>"ok"]);
}
else {
    $output = ["status"=>"failed","post"=>$_POST];
    $output["deviceId"] = $deviceId;
    $output["channel"] = $channel;
    $output["gcmToken"] = $gcmToken;
    $output["application"] = $application;
    echo json_encode($output);
}