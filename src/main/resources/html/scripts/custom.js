//Get the button
let mybutton = document.getElementById("btn-back-to-top");

// When the user scrolls down 20px from the top of the document, show the button
window.onscroll = function () {
scrollFunction();
};

function scrollFunction() {
if (
document.body.scrollTop > 20 ||
document.documentElement.scrollTop > 20
) {
mybutton.style.display = "block";
} else {
mybutton.style.display = "none";
}
}
// When the user clicks on the button, scroll to the top of the document
mybutton.addEventListener("click", backToTop);

function backToTop() {
document.body.scrollTop = 0;
document.documentElement.scrollTop = 0;
}

function validatePassword(){
  var password = document.getElementById("rPass"), confirm_password = document.getElementById("rPass2");
  if(password.value != confirm_password.value) {
    confirm_password.setCustomValidity("Passwörter stimmen nicht überein");
  } else {
    confirm_password.setCustomValidity('');
  }
}