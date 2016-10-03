
<?php 

$mittente = $_POST['mittente']; 
$destinatario = "mc.cucciniello@gmail.com"; 
$oggetto = $_POST['oggetto']; 
$messaggio = $_POST['messaggio'];

mail($destinatario, $oggetto, $messaggio, $mittente); 
?>