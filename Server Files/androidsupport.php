<?php
require "connection.php";

$type = $_REQUEST['type'];
if($type=='getprivatekey')
{
	$user_name = $_REQUEST['un'];
	$user_pass = $_REQUEST['pw'];
	$mysql_qry = "select id,username,password,private_key from member where username='$user_name' and password = '$user_pass'";
	$result = mysqli_query($conn,$mysql_qry);

	//$row = mysqli_num_rows($result);
	if($row = $result->fetch_assoc())
	{
		echo $row['private_key'];
	}
	else
	{
		echo "0";
	}
	
}
if($type=='logout')
{
       $user_name = $_REQUEST['un'];
       $qry = "update member set FCMID=0 where username='$user_name'";
       $result2 = mysqli_query($conn,$qry);
       echo $result2;
}
if($type=='fcmcheck')
{
       $number = $_REQUEST['number'];
       $qry="Select fcmid,public_key from member where mobile='$number'";
       $r=mysqli_query($conn,$qry);
       if($row = $r -> fetch_assoc())
       {
             $v=$row['fcmid'];
             $key=$row['public_key'];
       }
       if($v!="0")
       {
             echo $key;
       }
       else
       {
             echo "";
       }
}

if($type=='login'){

	$user_name = $_REQUEST['un'];
	$user_pass = $_REQUEST['pw'];
	$fcmid = $_REQUEST['fcmid'];
	$mysql_qry = "select id,username,password,private_key,fcmid from member where username='$user_name' and password = '$user_pass'";
	$result = mysqli_query($conn,$mysql_qry);

	//$row = mysqli_num_rows($result);
	if($row = $result->fetch_assoc())
	{
		if($row['fcmid']=="0")
		{
		$mysql_qry2 = "update member set FCMID='$fcmid' where username='$user_name'";
		$result2 = mysqli_query($conn,$mysql_qry2);
		echo "1";
		}
		else{
			echo "11";
		}
	}
	else
	{
		echo "0";
	}

}
else if($type == 'mynumber')
{
	$user_name = $_REQUEST['un'];

	$mysql_qry45 = "select mobile from member where username='$user_name'";
	$result = mysqli_query($conn,$mysql_qry45);
	if($row = $result -> fetch_assoc())
	{
		echo $row['mobile'];
	}
}
else if($type=='register'){
	$name = $_REQUEST['name'];
	$email = $_REQUEST['email'];
	$mobile = $_REQUEST['mobile'];
	$pw = $_REQUEST['pw'];
	$un = $_REQUEST['un'];
    $pb_key = $_REQUEST['public_key'];
    $pr_key = $_REQUEST['private_key'];
	$a="0";
	$mysql_qry45 = "select * from member where username='$un'";
	$result = mysqli_query($conn,$mysql_qry45);
	if($row = $result -> fetch_assoc())
	{
		$a="2";
	}
	$mysql_qry45 = "select * from member where mobile='$mobile'";
	$result = mysqli_query($conn,$mysql_qry45);
	if($row = $result -> fetch_assoc())
	{
		$a="3";
	}
	$mysql_qry45 = "select * from member where email='$email'";
	$result = mysqli_query($conn,$mysql_qry45);
	if($row = $result -> fetch_assoc())
	{
		$a="4";
	}
	if($a=="0")
	{
		$q = "insert into member(username, password, name, email, mobile,fcmid,public_key,private_key) values('$un','$pw','$name','$email','$mobile','0','$pb_key','$pr_key')";
		$result = mysqli_query($conn,$q);
		if($result){
			echo "1";
		}
		else echo "0";
	}
	else{
		echo $a;
	}
}
else if($type=='getuserlist'){
	
	$q = "select mobile from member";
	$json1 = $_REQUEST['json1'];
	$earray = array();
	$earray = json_decode($json1,true);

	

	//var_dump(json_decode($json1));

	//var_dump($earray);

	$result = mysqli_query($conn,$q);
	
	//json
    $emparray = array();
    while($row = mysqli_fetch_assoc($result))
    {
        $emparray[] = $row["mobile"];
    }
        //var_dump($emparray);

 	$r = array_intersect($earray,$emparray);
   	//echo json_encode($r);
   	$array1=array_keys($r);
   	//var_dump($array1);
   	echo json_encode($array1);
	


}

else if($type=='sendmessage'){
	$frommobile = $_REQUEST['frommobile'];
	$tomobile = $_REQUEST['tomobile'];
	$senderttl = $_REQUEST['senderttl'];
	$data = $_REQUEST['data'];
	


	$time =time();


	$q = "insert into message(frommobile, tomobile, senderttl, data, creationtime, status) values('$frommobile','$tomobile',$senderttl,'$data',$time,'Pending')";
	$result = mysqli_query($conn,$q);
	 $msg_id = mysqli_insert_id($conn);
	if($result){
	$q2 = "select * form member where tomobile= ".$tomobile;
	$result2 = mysqli_query($conn,$q2);
	

			send_multipal_push_notification_FCM(array_GCMIDs_by_AppGroupID($tomobile),json_Message_by_ID($msg_id));
		echo "1";
	}
	else echo "0";


}

function array_GCMIDs_by_AppGroupID($tomobile){
			global $conn;
		$return_arr = Array();
		//echo $tomobile;


	$q= "SELECT * from member where mobile = ".$tomobile;
			

	$res = mysqli_query($conn, $q);

	while($row = mysqli_fetch_assoc($res)) {
    array_push($return_arr,$row['FCMID']);
	}
//	echo "FCMID from function";

//			var_dump($return_arr);
			
	return $return_arr;
}

 function json_Message_by_ID($MSG_ID){
			global $conn;
		$return_arr = Array();


	//$q= "SELECT * FROM message where MessageID = {$MSG_ID}";
	$q = "SELECT  * FROM message where id = {$MSG_ID}";
	$res = mysqli_query($conn, $q);

	while($row = mysqli_fetch_assoc($res)) {
    array_push($return_arr,$row);
	}
	
	return json_encode($return_arr);
}



function send_multipal_push_notification_FCM($gcmIDS,$msg_obj)
	 {
	
			//$ids = array($gcmIDS); 

			
			//$api_key = "AIzaSyCpF1xjekytD810hoqniZq-PE-vzUqp3qU";
			//$api_key = "AIzaSyCXDg7Jnt95RhvFzPzdCKsWYZdq_j9Q8ok";
			$message = $msg_obj;
	//$url = 'https://gcm-http.googleapis.com/gcm/send';
			
			$url = 'https://fcm.googleapis.com/fcm/send';


			$fields = array('registration_ids'  => $gcmIDS,'data'=> array( "newmessage" => $message));
			//echo json_encode($ids);
			$headers = array('Authorization: key=AAAAXh1vizk:APA91bEg_j5ewAHo0f1or5lM3grNwf2rrPtsbqZqy1aCaWntHiNSKHHdPh2-MeQTEedrii-UPtZcLtQXF7Ie0yqSni65l_xpgN5JgtKJVW6M8sSipsE8TB_BFplF_Sw4g5HZiYWLE4sN','Content-Type: application/json');
		//var_dump($gcmIDS);
			// use key 'http' even if you send the request to https://...
			$options = array(
				'http' => array(
					'header'=> $headers ,
					'method'  => 'POST',
					'content' => json_encode($fields)
				),
			);
		  //var_dump(json_encode($fields));
			$context  = stream_context_create($options);
			
			$result = file_get_contents($url, false, $context);

			//sendGCM("hiii",$gcmIDS);
			
			
	//		var_dump($result);
			
	//		return $result;
	 }
	 


  ?>