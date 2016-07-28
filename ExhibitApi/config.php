<?php
/**
 * Created by PhpStorm.
 * User: ChristopherRuddell
 * Date: 7/22/16
 * Time: 7:41 AM
 */

define("DB_HOST", "localhost");
define("DB_USER", "root");
define("DB_PASS", "Zimadog04");
define("DB_NAME", "udacity");

//tables
define("TABLE_REGISTER_TOKEN", "gcm_registrations");
define("TABLE_CHANNELS", "channels");
define("CHANNEL_VERSE_OF_THE_DAY", 2);
define("UDACITY_APP_ID", "com.ruddell.museumofthebible");
define("GCM_API_KEY_BROWSER", "AIzaSyD2Vlj2FYItdO53HIgEgmYmAx_W3hcpSBY");
define("GCM_API_KEY_SERVER", "AIzaSyC0SaEAH2MaPxsaSb3g146QrvtWZtVwmhc");
define("GCM_API_KEY_ANDROID", "AIzaSyCZKMTP83pwkipu3u2xbE_TAB0Fw6PtQRM");
define("GCM_API_KEY",GCM_API_KEY_SERVER);
define("APP_ERROR_REPORTING", "1");

//turn error warning on / off...
if(defined("APP_ERROR_REPORTING")){
    if(APP_ERROR_REPORTING == "1"){
        @error_reporting(E_ALL);
        @ini_set("display_errors", "1");
    }else{
        @error_reporting(0);
        @ini_set("display_errors", "0");
    }
}else{
    @error_reporting(0);
    @ini_set("display_errors", "0");
}

//php error handler..
function handleShutdown(){
    $error = error_get_last();
    if($error !== NULL){

        // handle error..email it, print it, whatever floats your boat...the "info" variable holds the details...
        $info = "File: " . $error['file'];
        $info .= "<br/>Line: " . $error['line'];
        $info .= "<br/>Message: " . $error['message'] . PHP_EOL;

        //UNCOMMENT THIS TO SHOW PHP ERROR AND WARNING DETAILS ON EACH SCREEN
        if(APP_ERROR_REPORTING == "1"){
            echo "<div style='border:1px solid red;padding:10px;margin:10px;background-color:#FFFFFF;'>";
            echo "Oops, a PHP error was trapped.";
            echo "<hr>";
            echo $info;
            echo "<br>";
            $file = $error['file'];
            $lines = file( $file );
            echo "Offending line:" . $lines[$error['line']];

            echo "<div>";
            exit();
        }
    }
}


//begin mysql connection
$con=mysqli_connect(DB_HOST,DB_USER,DB_PASS);
// Check connection
if (mysqli_connect_errno())
{
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

//begin mysqli connection
$mysqli = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
/* check connection */
if ($mysqli->connect_errno) {
    printf("mysqli connect failed: %s\n", $mysqli->connect_error);
    exit();
}

//register the php error handler...
@register_shutdown_function('handleShutdown');