<?php
	// prelevo l'id

$number = $_GET['id'];

// controllo che l'id sia quello prestabilito

if($number == 1){
	$percorso = 'EsempioDataFeed-C#.txt';
	}

else if($number == 2){
	$percorso = 'ConnectDataFeed.java';
	}

else if ( $number == 3 ) {
	$percorso = 'EsempioDataFeed-C.txt';
	}

else if($number == 4) {
	$percorso = 'TradingConnect.java';
}

else if($number == 5){
	$percorso = 'TbtConnect.java';
}

else if($number == 5){
	$percorso = 'TbtConnect.java';
}

else if($number == 6){
	$percorso = 'ExampleDataFeed-C.txt';
}

else if($number == 7){
	$percorso = 'ConnectDataFeeden.java';
}

else if($number == 8){
	$percorso = 'ExampleDataFeed-C#.txt';
}

else if($number == 9){
	$percorso = 'TradingConnecten.java';
}

else if($number == 10){
	$percorso = 'TbtConnecten.java';
}
else if($number == 11)
	$percorso = 'packExcel.zip';
else if($number == 12){
	$percorso = 'MANUAL-LAST.pdf';
}

// preparo gli header

header("Cache-Control: public");
header("Content-Description: File Transfer");
header("Content-Disposition: attachment; filename= " . $percorso);
header('Content-type: text/plain');
header("Content-Transfer-Encoding: binary");
 
// leggo il file

  readfile($percorso);
 

?>