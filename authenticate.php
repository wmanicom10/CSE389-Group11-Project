<?php
      session_start();
?>

<?php
      use Kreait\Firebase\Factory;

      require 'C:/xampp/htdocs/CSE389_FinalProject/vendor/autoload.php';
  
      $firebase = (new Factory)
          ->withServiceAccount('secret.json')
          ->withDatabaseUri('https://cse389fp-default-rtdb.firebaseio.com/');
  
      $database = $firebase->createDatabase();
  
      $reference = $database->getReference('Users');
      $snapshot = $reference->getSnapshot();
      $value = $snapshot->getValue();
      $userIds = array_keys($value);

      $username = $_POST['username'];
      $password = $_POST['password'];

      foreach($userIds as $name) {
        $pword = $database->getReference('Users')->getChild($name)->getChild("Password")->getSnapshot()->getValue();
        if(strcmp($username, $name) == 0) {
            if(strcmp($password, $pword) == 0) {
              header('Location: userInterface.php');
              $_SESSION["username"] = $username;
              exit();
            }
        }
      }
      header('Location: userFailedAuthentication.html');
      exit();
      
    
?>