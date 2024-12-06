const firebaseConfig = {
    apiKey: "AIzaSyAvXVpgWEhHUTUb48u-2rfyR0DnSbHKYr8",
    authDomain: "java-web-server-314cc.firebaseapp.com",
    databaseURL: "https://java-web-server-314cc-default-rtdb.firebaseio.com",
    projectId: "java-web-server-314cc",
    storageBucket: "java-web-server-314cc.firebasestorage.app",
    messagingSenderId: "433975170122",
    appId: "1:433975170122:web:34c6f09925fc0d67bc42b3",
    measurementId: "G-V6BX677XN8"
  };

// initialize firebase  
firebase.initializeApp(firebaseConfig);

//reference your database
var contactFormdB = firebase.database().ref('contactForm');

document.getElementById('contactForm').addEventListener('submit', submitForm);

function submitForm(e){
    e.preventDefault();

    var name = getElementVal('name');
    var emailid = getElementVal('emailid');
    var msgContent = getElementVal('msgContent');

    console.log(name, emailid, msgContent);

    saveMessages(name, emailid, msgContent);

}

const saveMessages = (name, emailid, msgContent) => {
    var newContactForm = contactFormDB.push();

    newContactForm.set({
        name : name,
        emailid : emailid,
        msgContent : msgContent
    })
};


const getElementVal = (id) => {
    return document.getElementById(id).value;
};