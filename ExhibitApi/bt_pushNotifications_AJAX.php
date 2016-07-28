<?php  require_once("../../config.php");

	//hide errors...
	@error_reporting(0);

	//show errors...
	//@error_reporting(E_ALL);
	//@ini_set("display_errors", 1);

	//set time limit...
	@set_time_limit(0);
	@ignore_user_abort(true);

	//vars...
	$dtNow = fnMySqlNow();
	$strMessage = "";
	$bolDone = false;
	$bolPassed = true;
	$appGuid = fnGetReqVal("appGuid", "", $myRequestVars);
	$queueGuid = fnGetReqVal("queueGuid", "", $myRequestVars);

	//sleep for a bit to make sure loading graphic shows on parent screen...
	@sleep(1);
	
	//sent count...
	$sentCount = 0;
	$unsentCount = 0;
	

	/////////////////////////////////////////////////////////////////////////
	//Send Google Cloud Messaging message...
	function fnSendGCMMessage($googleProjectApiKey, $registatoin_ids, $message) {
        
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
            "Authorization: key=" . $googleProjectApiKey,
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
					$tmp = "UPDATE " . TBL_APN_DEVICES . " SET deviceToken = '" . $newToken . "' ";
					$tmp .= " WHERE deviceToken = '" . $oldToken . "' AND deviceType = 'android' ";
					$tmp .= " AND appGuid = '" . $appGuid . "'";
					if($useDev){
						$tmp .= " AND deviceMode = 'Design'";
					}else{
						$tmp .= " AND deviceMode = 'Live'";
					}
					fnExecuteNonQuery($tmp, APP_DB_HOST, APP_DB_NAME, APP_DB_USER, APP_DB_PASS);
					
					
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
	
	
	
	/////////////////////////////////////////////////////////////////////////
	//clean up token...
	function fnCleanUpToken($theToken){
		$theVal = str_replace(" ", "", $theToken);
		$theVal = str_replace("<", "", $theVal);
		$theVal = str_replace(">", "", $theVal);
		return $theVal;		
	}	

	/////////////////////////////////////////////////////////////////////////
	//send messages from apn queue...
	if($queueGuid != "" && $appGuid != ""){
	
		//app object...
		$objApp = new App($appGuid);
	
		//what are we sending to...
		$bolSendApple = false;
		$bolSendGoogle = false;
		$bolUseDevServer = false;
		$sendCertPath = "";
		$sendCertPassword = "";
		$certNotFoundMessage = "";
		$sendToDevices = "";
		
		//directory where certificate files are kept...
		$appDataDirectory = $objApp->fnGetAppDataDirectory($appGuid);
		$configDirectory = $appDataDirectory . "/config";

		//paths to APNS Certificates for Apple...
		$iosDevCertName = APP_CRYPTO_KEY . "_dev.pem";
		$iosProdCertName = APP_CRYPTO_KEY . "_prod.pem";
		
		//holds Google GCM Project API Key...
		$googleProjectApiKey = $objApp->infoArray["googleProjectApiKey"];

		//get sendToDevices for this queue item...
		$tmp = "SELECT sendToDevices FROM " . TBL_APN_QUEUE . " WHERE guid = '" . $queueGuid . "' AND appGuid = '" . $appGuid . "'  ";
		$sendToDevices = fnGetOneValue($tmp, APP_DB_HOST, APP_DB_NAME, APP_DB_USER, APP_DB_PASS);

		//check for iOS Dev..
		if(strpos($sendToDevices,'iosDev') !== false){
			$bolSendApple = true;
			$bolUseDevServer = true;
			$sendCertPassword = base64_decode($objApp->infoArray["applePushCertDevPassword"]);
			$sendCertPassword = mcrypt_decrypt(MCRYPT_RIJNDAEL_128, APP_CRYPTO_KEY, $sendCertPassword, MCRYPT_MODE_ECB);
			$sendCertPath = $configDirectory . "/" . $iosDevCertName;
			$certNotFoundMessage = "Could not find APNS Development Certificate";
		}

		//check for iOS Prod..
		if(strpos($sendToDevices,'iosProd') !== false){
			$bolSendApple = true;
			$bolUseDevServer = false;
			$sendCertPassword = base64_decode($objApp->infoArray["applePushCertProdPassword"]);
			$sendCertPassword = mcrypt_decrypt(MCRYPT_RIJNDAEL_128, APP_CRYPTO_KEY, $sendCertPassword, MCRYPT_MODE_ECB);
			$sendCertPath = $configDirectory . "/" . $iosProdCertName;
			$certNotFoundMessage = "Could not find APNS Production Certificate";
		}

		//check for Android..
		if(strpos($sendToDevices,'android') !== false){
			$bolSendGoogle = true;
		}
		
		//must have googleProjectApiKey certificate if sending to Android devices...
		if($bolSendGoogle){
			if(strlen($googleProjectApiKey) < 5){
				$bolPassed = false;
				$strMessage .= "<br/>Google Cloud Messaging API Key required.";
			}
		}

		//must have certificate if sending to iOS devices...
		if($bolSendApple){
			if(!is_file($sendCertPath)){
				$bolPassed = false;
				$bolSendApple = false;
				$strMessage .= "<br/>" . $certNotFoundMessage;
			}
		}

		//must have certificate passphrase if sending to ios...
		if($bolSendApple){
			if(strlen($sendCertPassword) < 1){
				$bolPassed = false;
				$strMessage .= "<br/>APNS Certificate passphrase required.";
			}
		}

		
		////////////////////////////////////////////////////////////
		//proecess removed devices from APNS Feedback Server...
		if($bolSendApple){
			if(!fnProcessAPNSFeedback($appGuid, $sendCertPath, $sendCertPassword, $bolUseDevServer)){
				$bolPassed = false;
				$strMessage .= "<br>Could not process Feedback from APNS (1)";
				$strMessage .= "<br>This is sometimes caused by an invalid APNS Certificate ";
				if($bolUseDevServer){
					$strMessage .= " (tried your Development Certificate aka:\"Sandbox\" certificate).";
				}else{
					$strMessage .= " (tried your Production Certificate).";
				}
				
				//port info...
				$strMessage .= "It could also be that your web host has blocked PORTS 2195 and 2196, both are required by the Apple Push Notification Service.";
				
				
			}
		}
		////////////////////////////////////////////////////////////
		
		//still good?
		if($bolPassed){
	
			//update this queue record to "in progress"...
			$tmp = "UPDATE " . TBL_APN_QUEUE . " SET status = 'in progress' ";
			$tmp .= " WHERE guid = '" . $queueGuid . "'";
			$tmp .= " AND appGuid = '" . $appGuid . "'";
			fnExecuteNonQuery($tmp, APP_DB_HOST, APP_DB_NAME, APP_DB_USER, APP_DB_PASS);
	
			//array of devices to send to...
			$iosTokens = array();
			$androidTokens = array();
			
			//badge, message, sound..
			$badge = "";
			$message = "";
			$sound = "";
			$payload = "";
	
			//get details about the message to send...
			$strSql = "SELECT guid, message, sound, badge, iosDeviceTokens, androidDeviceTokens ";
			$strSql .= " FROM " . TBL_APN_QUEUE;
			$strSql .= " WHERE appGuid = '" . $appGuid . "' ";
			$strSql .= " AND guid = '" . $queueGuid . "'";
			$res = fnDbGetResult($strSql, APP_DB_HOST, APP_DB_NAME, APP_DB_USER, APP_DB_PASS);
			if($res){
				$cnt = 0;
				while($row = mysql_fetch_array($res)){
					$cnt++;

					//explode ios tokens...
					$iosList = explode(",", $row["iosDeviceTokens"]);
					for($x = 0; $x < count($iosList); $x++){
						if($iosList[$x] != ""){
							$iosTokens[] = fnCleanUpToken($iosList[$x]);
						}
					}

					//explode android tokens...
					$androidList = explode(",", $row["androidDeviceTokens"]);
					for($x = 0; $x < count($androidList); $x++){
						if($androidList[$x] != ""){
							$androidTokens[] = fnCleanUpToken($androidList[$x]);
						}
					}
					
					//message...					
					$message = $row["message"];
					if(strlen($message) > 200) $message = substr($message, 0, 200);

					//badge...					
					$badge = $row["badge"];
					if(!is_numeric($badge)) $badge = 0;

					//sound...					
					$sound = $row["sound"];
					
					//payload contents...
					$body["aps"] = array("alert" => $message, "badge" => (int)$badge, "sound" => $sound);
					
					//encode payload as JSON...
					$payload = json_encode($body);
					
				}//while...
			}//res...	
				
				
			////////////////////////////////////////////////////////////
			//send to iOS devices...
			if(count($iosTokens) > 0 && $bolPassed){	
					
				/*
					the comma separted list of device tokens to send to may be very VERY long. For this reason, 
					it could take time to send all the messages. The strategy involves connecting to the Apple Push
					Notification Service, sending some messages (100 at a time), diconnecting, then repeating the
					process until all of the device tokens are processed. 
				*/
					
				//tracks sending loop...
				$bolSending = true;
				$batchStart = 0;
				
				//outer loop...
				while($bolSending){
					
					//inner batch counter, resets after each 100 sends...
					$batch = 0;
					
					//connect to APNS...
					if($fp = fnConnectToAPNS($appGuid, $sendCertPath, $sendCertPassword, $bolUseDevServer)){
						
						//loop next 100 device tokens...
						for($batchStart; $batchStart < count($iosTokens); $batchStart++){
							
							//increment this batch...
							$batch++;
							
							//get the token to send to from this element in the array...
							$iosDeviceToken =  $iosTokens[$batchStart];
							
							if(fnIsValidAPNSDeviceToken($iosDeviceToken)){
							
								//binary message...
								$msg = chr(0) . pack('n', 32) . pack('H*', $iosDeviceToken) . pack('n', strlen($payload)) . $payload;
						
								//write message to APNS Develoment server...
								$result = fwrite($fp, $msg, strlen($msg));
							}
							
							
							//if this batch is > 100...
							if($batch >= 100){
								
								//increment batch so we don't resend to the "last device"...
								$batchStart++;

								//break inner loop...
								break;
							
							}
						}//for	
					
						//disconnect from APNS...
						if($fp){
							sleep(1);
							fnDisconnectFromAPNS($fp);
						}
					
					
					}else{
					
						//if APNS disconnected...
						$bolSending = false;
					
					}//if connected...
					
					//if we've reached the end of the list, stop sending...
					if($batchStart >= count($iosTokens)){
						$bolSending = false;
					}
					
				}//outer while...
				
			}//iosTokens count...
			

			////////////////////////////////////////////////////////////
			//send to Android devices...
			if(count($androidTokens) > 0 && $bolPassed){	
				
				/*
					the comma separted list of device tokens to send to may be very VERY long. For this reason, 
					it could take time to send all the messages. The strategy involves connecting to the Google Cloud
					Messaging system, sending some messages (100 at a time), diconnecting, then repeating the
					process until all of the device tokens are processed. 
				*/
					
				//format message...
    			$messageParts = array("message" => $message);
				
				//tracks sending loop...
				$bolSending = true;
				$batchStart = 0;
				
				//outer loop...
				while($bolSending){
					
					//inner batch counter, resets after each 100 sends...
					$batch = 0;
					
					//tokens to send on this batch
					$batchTokens = array();
					
					//loop next 100 device tokens...
					for($batchStart; $batchStart < count($androidTokens); $batchStart++){
						
						//increment this batch...
						$batch++;
						
						//get the token to send to from this element in the array...
						$androidToken =  $androidTokens[$batchStart];
						
						//this batch of tokens...
						$batchTokens[] = $androidToken;
					
						//if this batch is > 100...
						if($batch >= 100){
							
							//increment batch so we don't resend to the "last device"...
							$batchStart++;

							//break inner loop...
							break;
						
						}
					}//for	
					
					//get results from GCM send for this batch of tokens...
					$gcmData = fnSendGCMMessage($googleProjectApiKey, $batchTokens, $messageParts);
						
					//parse results and remove "invalid device tokens"...
					fnProcessGCMResults($appGuid, $gcmData, $batchTokens, $bolUseDevServer);
						
					//if we've reached the end of the list, stop sending...
					if($batchStart >= count($androidTokens)){
						$bolSending = false;
					}
					
				}//outer while...
				
			}//count androidTokens...

			//update this queue record...
			$tmp = "UPDATE " . TBL_APN_QUEUE . " SET status = 'done' ";
			$tmp .= " WHERE guid = '" . $queueGuid . "'";
			$tmp .= " AND appGuid = '" . $appGuid . "'";
			fnExecuteNonQuery($tmp, APP_DB_HOST, APP_DB_NAME, APP_DB_USER, APP_DB_PASS);

			
		}//bolPassed
	
		
		//passed...
		if(!$bolPassed){
		
			echo "<img src='" . fnGetSecureURL(APP_URL) . "/images/red_dot.png' style='margin-right:5px;'><span style='color:red;'>Error sending" . $strMessage . "</span>";
			exit();
			
		}else{
			
			//controls div with send stats and try again link...
			echo "\n<div id=\"controls_" . $row["guid"] . "\">";
		
				//number sent message...
				echo "<img src='" . fnGetSecureURL(APP_URL) . "/images/green_dot.png' style='margin-right:5px;'>Notifications Sent";
				
			//end controls...
			echo "\n\n</div>";
			exit();
		
		}
	
	}
	
	echo "";
	exit();

?>