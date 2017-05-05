<?php
$db_name = "id1144005_blumeddb";
$mysql_username="id1144005_dhvanil1976";
$mysql_password="abc123";
$server_name="localhost";
$conn = mysqli_connect($server_name,$mysql_username,$mysql_password,$db_name); 

if($conn)
{
	//echo "Connection Successfull!";
}
else
{
	//echo "Connection Fail!!";
}
?>