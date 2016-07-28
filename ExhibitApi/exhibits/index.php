<?php

$exhibit1 = array();
$exhibit2 = array();
$exhibit3 = array();
$exhibit4 = array();

$exhibit1["ID"] = 1;
$exhibit1["Title"] = "Caves of Qumran";
$exhibit1["Audio"] = "audio_0699.mp3";
$exhibit1["Image"] = "0699.png";
$exhibit1["Description"] = "In early 1947, a Bedouin boy of the Ta'amireh tribe found a cave after searching for a lost animal. He stumbled onto the first cave containing scrolls from two thousand years ago. They were shown to Mar Samuel of the Monastery of Saint Mark in April 1947 and the discovery of the Dead Sea Scrolls was made known. (Wikipedia)";

$exhibit2["ID"] = 2;
$exhibit2["Title"] = "Gutenberg Press";
$exhibit2["Audio"] = "audio_0899.mp3";
$exhibit2["Image"] = "0899.png";
$exhibit2["Description"] = "Johannes Gutenberg's work on the printing press began in approximately 1436 when he partnered with Andreas Dritzehn—a man who had previously instructed in gem-cutting—and Andreas Heilmann, owner of a paper mill. (Wikipedia)";

$exhibit3["ID"] = 3;
$exhibit3["Title"] = "King James Bible";
$exhibit3["Audio"] = "audio_2199.mp3";
$exhibit3["Image"] = "2199.png";
$exhibit3["Description"] = "The King James Version (KJV), also known as the Authorized Version (AV) or King James Bible (KJB), is an English translation of the Christian Bible for the Church of England begun in 1604 and completed in 1611. The books of the King James Version include the 39 books of the Old Testament, an intertestamental section containing 14 apocrypha books, and the 27 books of the New Testament. (Wikipedia)";

$exhibit4["ID"] = 4;
$exhibit4["Title"] = "Digital Age";
$exhibit4["Audio"] = "audio_1299.mp3";
$exhibit4["Image"] = "1299.png";
$exhibit4["Description"] = "The Information Age (also known as the Computer Age, Digital Age, or New Media Age) is a period in human history characterized by the shift from traditional industry that the Industrial Revolution brought through industrialization, to an economy based on information computerization. The onset of the Information Age is associated with the Digital Revolution, just as the Industrial Revolution marked the onset of the Industrial Age. (Wikipedia)";

$featuredExhibits = array();
$featuredExhibits[0] = $exhibit1;
$featuredExhibits[1] = $exhibit2;
$featuredExhibits[2] = $exhibit3;
$featuredExhibits[3] = $exhibit4;

echo json_encode($featuredExhibits);
?>