# Progetto EmotionalSongs. 

Laboratorio interdiscplinare B, Laurea Triennale in Informatica, Università degli Studi dell'Insubria

PROGETTO REALIZZATO DA: 

* ###### **Marco Canopoli**, matricola `731108`, sede: Varese

*********************************

## EmotionalSongs
    EmotionalSongs è una piattaforma di associazione di tag emozionali a canzoni.
    Offre la possibilità di sfogliare il catalogo di canzoni, di creare playlist e di
    associare emozioni e commenti alle singole canzoni.

*********************************

### REQUISITI DI SISTEMA

Per poter eseguire l'applicazione è necessario aver installato sul
proprio computer:

▪ `Java 17`

▪ `PostgreSQL (solo per applicazione server)`

▪ `Maven (per compilazione da sorgente)`

▪ `Risoluzione: 1280 x 720 pixel (HD) o superiore`

▪ `RAM: 4GB o superiore`

▪ `Spazio su disco disponibile: 2 GB o superiore`

▪ `Processore: 1 GHz o superiore`


##### Sistema operativo minimo richiesto

L'applicazione è stata testata in ambiente Windows e Linux (Ubuntu)

▪ `Windows 10`

▪ `Ubuntu Linux 18.04`

Non si garantisce la corretta esecuzione su sistemi operativi più datati.

### Compilazione con Maven

Per la compilazione Maven tramite riga di comando, posizionarsi nella directory principale /emotionalsongs,
eliminare la cartella /out ed eseguire i seguenti comandi:

`mvn clean` 

`mvn validate` 

`mvn build`

`mvn package`

Sarà creata una cartella /out contenente i file jar eseguibili.

*********************************

##### Avviare l'applicazione

Per avviare l'applicazione è sufficiente fare doppio click sui file nella cartella /out della directory pricipale: 

▪ `EmotionalSongsServer-1.0.jar` per l'applicazione **Server**; 

▪ `EmotionalSongs-1.0.jar` per l'applicazione **Client**. 

In alternativa è possibile avviare il programma da terminale, posizionandosi nella directory /out, con il seguente comando:

▪ `java -jar {nomefile}.jar`

*********************************