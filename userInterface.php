<?php
      session_start();
?>

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
    $userPwords = array_values($value);

    $users = array_combine($userIds, $userPwords);
    
    $usernames = $database->getReference('Users')->getSnapshot()->getValue();
    $userIds = array_keys($usernames);
    
    $uName = $_SESSION["username"];

    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        if(isset($_POST['add'])) {

                if (strpos($_POST['TDList'], " ") === false) { 
                    $delUser = $database->getReference('Users')->getChild($uName)->getChild('To Do')->
                    getChild($_POST['TDList'])->set("");
                } else {
                    $splitString = explode(" ", $_POST['TDList']);
                    $splitString2 = implode("-", $splitString);
                    $delUser = $database->getReference('Users')->getChild($uName)->getChild('To Do')->
                    getChild($splitString2)->set("");
                }
            
        }

        if(isset($_POST['remove'])) {
            $taskList2 = $database->getReference('Users')->getChild($uName)->getChild('To Do')->getSnapshot()->getValue();
            foreach ($taskList2 as $taskListList  => $extra) {
                if(isset($_POST[$taskListList])){
                    $del = $database->getReference('Users')->getChild($uName)->getChild('To Do')->getChild($taskListList)->remove();
                }  
            }
        }

        if(isset($_POST['share'])) {
            $userToBeSharedWith = $_POST['shareUser'];

            $usernames = $database->getReference('Users')->getSnapshot()->getValue();
            $userIds = array_keys($usernames);

            foreach ($userIds as $name) {
                if(strcmp($name, $userToBeSharedWith) == 0) {
                    $taskList = $database->getReference('Users')->getChild($uName)->getChild('To Do')->getSnapshot()->getValue();
                    $shareUser = $database->getReference('Users')->getChild($userToBeSharedWith)->getChild('To Do')->getChild($uName)->set($taskList);
                }
            }

        }

        if(isset($_POST['otherTask'])) {
            $taskList2 = $database->getReference('Users')->getChild($uName)->getChild('To Do')->getSnapshot()->getValue();
            foreach ($taskList2 as $shareTaskListList  => $extra) {
                foreach($userIds as $name) {
                    if(strcmp($shareTaskListList, $name) == 0) {
                        $shareUserList = $database->getReference('Users')->getChild($uName)->getChild('To Do')->getChild($shareTaskListList)->getSnapshot()->getValue();
                        foreach ($shareUserList as $shareUserTask => $extra2)  {
                            $buttonName = $name.'_'.$shareUserTask;
                            if(isset($_POST[$buttonName])) {
                                if(strcmp($_POST[$buttonName], $buttonName)) {
                                    $tempVar = $buttonName;
                                    $otherUserNameTask = explode('_', $tempVar);
                                    $usersName = $otherUserNameTask[0];
                                    $usersTask = $otherUserNameTask[1];
                                    $delShareListFromOrigin =  $database->getReference('Users')->getChild($usersName)->getChild('To Do')->getChild($usersTask)->remove();
                                    $delShareListOnUser = $database->getReference('Users')->getChild($uName)->getChild('To Do')->getChild($usersName)->getChild($usersTask)->remove();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    ?>

 <form method="POST" action = "<?php echo $_SERVER['PHP_SELF'];?>">
        Add a task:
        <br><input type="text" id = "TDList" name = "TDList"/>
        <button type="submit" name="add">add</button>
    </form>

<form method="POST" action = "<?php echo $_SERVER['PHP_SELF'];?>">
    <br><br>Share your task list with another user:
        <br><input type="text" id = "shareUser" name = "shareUser"/>
        <button type="submit" name="share">share</button>
    </form>

<form method="POST" action = 'index.html'>
    <br> Log out
        <button type='log out' name = 'log out'>log out</button>
</form>
        
    <?php
        $usernames = $database->getReference('Users')->getSnapshot()->getValue();
        $userIds = array_keys($usernames);

                $taskList2 = $database->getReference('Users')->getChild($uName)->getChild('To Do')->getSnapshot()->getValue();
                foreach ($taskList2 as $taskListList  => $extra) {
                    if(in_array($taskListList, $userIds)) {
                        continue;
                    } else {
                        echo $taskListList." "."<form method ='POST' action = 'userInterface.php'><button type = 'submit' name = 'remove'>remove</button><input type='hidden' name = $taskListList value = ''></form><br>";
                    }
                }

                foreach ($taskList2 as $shareTaskListList  => $extra) {
                    foreach($userIds as $name) {
                        if(strcmp($shareTaskListList, $name) == 0) {
                            $shareUserList = $database->getReference('Users')->getChild($uName)->getChild('To Do')->getChild($shareTaskListList)->getSnapshot()->getValue();
                            echo $name."'s Tasks:"."<br>";
                            foreach ($shareUserList as $shareUserTask => $extra2)  {
                                $nameOfTask = $shareTaskListList.'_'.$shareUserTask;
                                echo $shareUserTask." "."<form method ='POST' action = 'userInterface.php'><button type = 'submit' name = 'remove'>remove</button><input type='hidden' name = $nameOfTask value = ''><input type='hidden' name = 'otherTask'></form><br>";
                            }    
                        }
                    }
                }

    ?>
</body>
</html>