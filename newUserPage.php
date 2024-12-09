<?php

use Kreait\Firebase\Factory;

require 'C:/xampp/htdocs/CSE389_FinalProject/vendor/autoload.php';

$firebase = (new Factory)
    ->withServiceAccount('secret.json')
    ->withDatabaseUri('https://cse389fp-default-rtdb.firebaseio.com/');

$database = $firebase->createDatabase();

if(isset($_POST['add'])) {
    if (strpos($_POST['username'], " ") === false) { 
        $newUserData = [
            "Username" => $_POST['username'],
            "Password" => $_POST['password'],

        ];
        $newUserRef = $database->getReference('Users')->getChild($_POST['username'])->set($newUserData);
    } else {
        $splitString = explode(" ", $_POST['username']);
        $splitString2 = implode("-", $splitString);
        $newUserData = [
            "Username" => $splitString2,
            "Password" => $_POST['password'],

        ];
        $newUserRef = $database->getReference('Users')->getChild($splitString2)->set($newUserData);
    }
            
            echo "User successfully added.";
        }
header("Location: newUserPage.html");

?>