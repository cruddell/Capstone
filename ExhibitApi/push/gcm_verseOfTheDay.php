<?php
/**
 * Created by PhpStorm.
 * User: ChristopherRuddell
 * Date: 7/27/16
 * Time: 8:10 AM
 */
require_once("../config.php");

	//hide errors...
	@error_reporting(0);

	//show errors...
	//@error_reporting(E_ALL);
	//@ini_set("display_errors", 1);

	//set time limit...
	@set_time_limit(30000);
	@ignore_user_abort(true);

	//vars...
	$strMessage = "";
	$bolDone = false;
	$bolPassed = true;


    /////////////////////////////////////////////////////////////////////////
    //Send Firebase Notification Message...
    function fnSendFirebaseNotification($registration_ids, $message) {
        $url = "https://fcm.googleapis.com/fcm/send";
        $data = ["message"=>$message];

        //fields to send with cURL POST...
        $fields = array(
            "registration_ids" => $registration_ids,
            "data" => $data,
        );

        //headers to send with cURL POST...
        $headers = array(
            "Authorization: key=" . GCM_API_KEY,
            "Content-Type: application/json"
        );

        //configure cURL connection...
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

        //POST...
        $result = curl_exec($ch);
        if($result === FALSE){
            $ret = curl_error($ch);
        }else{
            $ret = $result;
        }

        //close cURL...
        curl_close($ch);

        //return...
        return $ret;
    }

	/////////////////////////////////////////////////////////////////////////
	//Send Google Cloud Messaging message...
	function fnSendGCMMessage($registatoin_ids, $message) {

        //result...
        $result = "";
        $ret = "";

        //URL to GCM for Android...
        $url = "https://android.googleapis.com/gcm/send";

        //fields to send with cURL POST...
        $fields = array(
            "registration_ids" => $registatoin_ids,
            "data" => $message,
        );

        //headers to send with cURL POST...
        $headers = array(
            "Authorization: key=" . GCM_API_KEY,
            "Content-Type: application/json"
        );

        //configure cURL connection...
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

        //POST...
        $result = curl_exec($ch);
        if($result === FALSE){
            $ret = curl_error($ch);
        }else{
            $ret = $result;
        }

        //close cURL...
        curl_close($ch);

        //return...
        return $ret;

    }

	//fnProcessGCMResults...
	function fnProcessGCMResults($appGuid, $gcmData, $tokens, $useDev = true){
        global $mysqli;
	    print_r($gcmData);
        exit();
        $json_response = @json_decode($gcmData);
        if(@json_last_error() == JSON_ERROR_NONE){
            $results = $json_response->results;
            $tokenIndex = 0;

            //$json_response = json_decode($results);
            foreach ($results as $device_response) {
                if(is_array($device_response)){

                    //happens when a registration id needs to be updated.
                    $oldToken = $tokens[$tokenIndex];
                    $newToken = $device_response->registration_id;

                    //update this device for next time...
                    $tmp = "UPDATE " . TABLE_REGISTER_TOKEN . " SET gcmToken = '" . $newToken . "' ";
                    $tmp .= " WHERE gcmToken = '" . $oldToken . "' ";
                    $tmp .= " AND application = '" . $appGuid . "'";
                    mysqli_query($mysqli, $tmp);

                }else{
                    if(isset($device_response->error)){
                        if($device_response->error == "NotRegistered"){

                            //delete the device from the device list...
                            $oldToken = $tokens[$tokenIndex];

                            //update this device for next time...
                            $tmp = "DELETE FROM " . TBL_APN_DEVICES . " WHERE deviceToken = '" . $oldToken . "' ";
                            $tmp .= " AND deviceType = 'android' AND appGuid = '" . $appGuid . "'";
                            if($useDev){
                                $tmp .= " AND deviceMode = 'Design'";
                            }else{
                                $tmp .= " AND deviceMode = 'Live'";
                            }
                            fnExecuteNonQuery($tmp, APP_DB_HOST, APP_DB_NAME, APP_DB_USER, APP_DB_PASS);

                        }
                        if($device_response->error == "InvalidRegistration"){

                            //delete the device from the device list...
                            $oldToken = $tokens[$tokenIndex];

                            //update this device for next time...
                            $tmp = "DELETE FROM " . TBL_APN_DEVICES . " WHERE deviceToken = '" . $oldToken . "' ";
                            $tmp .= " AND deviceType = 'android' AND appGuid = '" . $appGuid . "'";
                            if($useDev){
                                $tmp .= " AND deviceMode = 'Design'";
                            }else{
                                $tmp .= " AND deviceMode = 'Live'";
                            }
                            fnExecuteNonQuery($tmp, APP_DB_HOST, APP_DB_NAME, APP_DB_USER, APP_DB_PASS);

                        }
                        if($device_response->error == "Unavailable"){

                            //not related to a device...

                        }
                    }
                }

                //increment...
                $tokenIndex++;
            }


        }//gcm results not json...
    }

    function fnGetVerseOfTheDay() {
        $verses = [
            "In the beginning was the Word, and the Word was with God, and the Word was God. (Jn 1:1)",
            "In the beginning when God created the heavens and the earth (Gen 1:1)",
            "For I know the plans I have for you, declares the LORD, plans to prosper you and not to harm you, plans to give you hope and a future. (Jer 29:11)",
            "One thing I ask from the LORD, this only do I seek: that I may dwell in the house of the LORD all the days of my life, to gaze on the beauty of the LORD and to seek him in his temple. (Psa 27:4)",
            "A friend loves at all times, and a brother is born for a time of adversity. (Pro 17:17)"
        ];

        $books = [
            43,1,24,19,20
        ];
        $bookNames = ["John","Genesis","Jeremiah","Psalms","Proverbs"];

        $chapters = [1,1,29];
        $verseNumbers = [1,1,11];

        $index = rand(0,count($verses)-1);
        $message = $verses[$index];
        $book = $books[$index];
        $bookName = $bookNames[$index];
        $chapter = $chapters[$index];
        $verse = $verseNumbers[$index];
        $retVal = [
            "book"=>$book,
            "bookName"=>$bookName,
            "chapter"=>$chapter,
            "verse"=>$verse,
            "text"=>$message
        ];
        return $retVal;
    }

	/////////////////////////////////////////////////////////////////////////
	//clean up token...
	function fnCleanUpToken($theToken){
        $theVal = str_replace(" ", "", $theToken);
        $theVal = str_replace("<", "", $theVal);
        $theVal = str_replace(">", "", $theVal);
        return $theVal;
    }

	/////////////////////////////////////////////////////////////////////////
    //get registered devices and send message

    $sql = "SELECT DISTINCT gcmToken FROM " . TABLE_REGISTER_TOKEN . " WHERE channel = '" . CHANNEL_VERSE_OF_THE_DAY . "' AND application='" . UDACITY_APP_ID . "' AND active='1'";
    $res = mysqli_query($mysqli, $sql);
    $tokens = array();
    if ($res) while ($row=mysqli_fetch_array($res)) {
        $tokens[count($tokens)] = $row["gcmToken"];
    }

    $message = fnGetVerseOfTheDay();
    $gcmData = fnSendFirebaseNotification($tokens, $message);

    fnProcessGCMResults(UDACITY_APP_ID, $gcmData, $tokens);

	exit();

?>