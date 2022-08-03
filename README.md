# hamnetPortal
#### Ein Portal in Form einer Webseite für das Hamnet ausschließlich in Java programmiert

## Funktionen
- Registrierung von Funkamateuren für den Zugang zum Portal
- Freischaltung neuer Registrierungen
- Web-Proxy ins Hamnet
- Verschiedene Anleitungen für den Zugang zum Hamnet

## Voraussetzungen
- Java 8

## Quellcode kompilieren
Projekt in IntelliJ IDEA öffnen und das Artefakt "hamnetPortal:jar" erstellen (dies wird beim normalen Build auch mit erstellt).

Die kompilierte JAR Datei befindet sich dann in `out/artifacts/hamnetPortal_jar/hamnetPortal.jar`. Die Datei hat (bis auf Java 8) keine weiteren Abhängigkeiten und kann verschoben oder auf anderen Systemen verwendet werden.

## Programm ausführen
Die JAR Datei in der Konsole öffnen mit
`java -jar hamnetPortal.jar`

#### Konfiguration
In der Datei hamnetPortal.jar befindet sich die Datei `settings.json`, welche wie folgt aufgebaut ist:
```
{
"dbHost": "localhost",
"dbPort": 3306,
"dbUser": "hamnetPortal",
"dbPass": "hamnetPortal",
"dbName": "hamnetPortal"
}
```
Dort können unter anderem die Einstellungen für die Verbindung zur MySQL Datenbank vorgenommen werden. JAR Dateien lassen sich mit Packprogrammen wie 7zip oder winrar öffnen und bearbeiten.
#### Mögliche (optionale) Parameter:
- -p / --port (definiert den port für den Webserver, standard ist 8000)