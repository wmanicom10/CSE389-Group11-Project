<!doctype html>
<html lang="en">
  <head>
</head>
<body>
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
    $adminIds = array_keys($value);
    $userIds = array_keys($value);

    $newUserData = [
        'test' => 123
    ];
    

    $delUserData = 'test';
    $usernames = $database->getReference('Users')->getSnapshot()->getValue();
    $userIds = array_keys($usernames);
    
    if ($_SERVER["REQUEST_METHOD"] == "POST") {

        if(isset($_POST['add'])) {
            $newUserData = [
                "Username" => $_POST['username'],
                "Password" => $_POST['password'],

            ];
            $newUserRef = $database->getReference('Users')->getChild($_POST['username'])->set($newUserData);
        }

        if(isset($_POST['remove'])) {
            foreach ($userIds as $name){
                if(isset($_POST[$name])) {
                    $delUserData = $name;
                    $delUser = $database->getReference('Users')->getChild($delUserData)->remove();
                }
            }
        }

    }
    ?>

 <form method="POST" action = "<?php echo $_SERVER['PHP_SELF'];?>">
        Add a user:
        <br><input type="text" id = "username" name = "username" required />
        <input type="password" id="password" name="password" required/>
        <button type="submit" name="add">add</button>
    </form>

    <?php
        $usernames = $database->getReference('Users')->getSnapshot()->getValue();
        $userIds = array_keys($usernames);
    
        $string = 'PHP_SELF';

        foreach ($userIds as $name) {
            $password = $database->getReference('Users')->getChild($name)->getChild("Password")->getSnapshot()->getValue();
            echo "<table>
            <tr>
            <td> 
            Username: $name Password: $password 
            <form method ='POST' action = 'adminInterface.php'>
            <button type = 'submit' name = 'remove'>remove</button>
            <input type='hidden' name = $name value = ''>
            </form>
            </td> 
            </tr>
            </table>";
        }

        echo "<br><br><form method ='POST' action = 'index.html'>
            <button type = 'submit' name =>Log out</button>
            </form>";
    ?>
</body>
</html>