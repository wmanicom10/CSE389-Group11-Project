<?php
    use Kreait\Firebase\Factory;

    require 'C:/xampp/htdocs/CSE389_FinalProject/vendor/autoload.php';

    $firebase = (new Factory)
        ->withServiceAccount('secret.json')
        ->withDatabaseUri('https://cse389fp-default-rtdb.firebaseio.com/');

    $database = $firebase->createDatabase();

    $reference = $database->getReference('admins');
    $snapshot = $reference->getSnapshot();
    $value = $snapshot->getValue();
    $userIds = array_keys($value);
    $userPwds = array_values($value);

    $admins = array_combine($userIds, $userPwds);

    $username = $_POST['username'];
    $password = $_POST['password'];

    echo $username;
    echo $password;
    foreach($admins as $name => $pword) {
    if(strcmp($username, $name) == 0) {
        if(strcmp($password, $pword) == 0) {
            header('Location: adminInterface.php');
        }
        else {
            header('Location: adminFailedAuthentication.html');
        }
    }
    }


    

?>