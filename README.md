# Media Ratings Platform 🎬🎮

Willkommen zu meiner **Media Ratings Platform**!

Das Projekt ist eine RESTful Web-Anwendung, die ich in **Java 26** geschrieben habe. Ziel der Plattform ist es, verschiedene Medientypen wie Filme, Serien und Videospiele zu verwalten, zu suchen und zu bewerten. Statt auf ein fertiges Framework wie Spring Boot zu setzen, habe ich einen eigenen HTTP-Server sowie ein eigenes Router-System implementiert, um den REST-Server von Grund auf aufzubauen.

* * *

## 🏗️ Projektstruktur

Ich habe das Projekt in einer klaren Schichtenarchitektur (Layered Architecture) aufgebaut:

    Media-Ratings-Platform/
    ├── docker-compose.yaml             # Docker-Setup für PostgreSQL
    ├── pom.xml                         # Maven Abhängigkeiten & Build-Konfiguration
    ├── request.http                    # Vorgefertigte HTTP-Requests zum Testen der API
    ├── Klassendiagramm                 # Architektur-Visualisierung
    ├── klassendiagramm.drawio          # Bearbeitbare Draw.io Datei
    └── src/
        └── main/
            └── java/
                └── com/company/
                    ├── Main.java       # Einstiegspunkt der Anwendung
                    ├── Interfaces/     # Generische Interfaces (IIdentifiable, ILikeable)
                    ├── models/         # Datenmodelle (User, MediaEntrie, Movie, Series, Game, Rating)
                    ├── repository/     # Datenbank-Repositories & Verbindungsschemata
                    ├── server/         # Mein eigener HTTP-Server & Router
                    └── service/        # Business Logic & Controller Layer
                        └── controller/ # Routing-Handler für User, Media & Ratings

### Kurze Erklärung der Ordner:

* **`models`**: Enthält die Kern-Klassen. `Movie`, `Series` und `Game` erben alle von der abstrakten Basisklasse `MediaEntrie`.
* **`repository`**: Hier befindet sich die Datenbankschicht. `DatabaseConnection` stellt die Verbindung her, `DatabaseInitializer` baut die Tabellen auf und die Repositories (z. B. `PostgresMediaRepository`) regeln die SQL-Abfragen.
* **`server`**: Enthält den selbst entwickelten HTTP-Server (`RestServer`) und die Routen-Verwaltung (`Router`).
* **`service` & `controller`**: Hier liegt die Geschäftslogik. Die Controller verarbeiten eingehende HTTP-Requests, validieren die Daten und rufen die Services auf.

* * *

## 🗄️ Datenbankaufbau

Als Datenbank nutze ich **PostgreSQL**. Die Tabellen müssen nicht manuell angelegt werden – der `DatabaseInitializer` erstellt sie automatisch beim Start der Anwendung, falls sie noch nicht vorhanden sind.

Das Datenmodell besteht aus folgenden Haupttabellen:

1. **`users`**
  
  * `id` (VARCHAR / UUID, Primary Key)
  * `username` (VARCHAR, Unique)
  * `password` (VARCHAR)
2. **`media`**
  
  * `id` (VARCHAR / UUID, Primary Key)
  * `title` (VARCHAR)
  * `description` (TEXT)
  * `media_type` (VARCHAR: `MOVIE`, `SERIES`, `GAME`)
  * `release_year` (INT)
  * `genre` (VARCHAR)
3. **`ratings`**
  
  * `id` (VARCHAR / UUID, Primary Key)
  * `user_id` (Foreign Key -> `users.id`)
  * `media_id` (Foreign Key -> `media.id`)
  * `stars` (INT, 1–5)
  * `comment` (TEXT)
  * `status` (VARCHAR: `PENDING`, `APPROVED`, `CONFIRMED`)

* * *

## 🐳 Docker-Compose Setup

Um die PostgreSQL-Datenbank ohne lokale Installation aufzusetzen, nutze ich Docker Compose.

### Datenbank starten:

Einfach im Hauptverzeichnis des Projekts folgenden Befehl ausführen:

    docker-compose up -d

Damit wird die Postgres-Datenbank im Hintergrund gestartet und ist auf Port `5432` erreichbar.

### Datenbank stoppen:

    docker-compose down

* * *

## 🚀 So startest du das Projekt

### Voraussetzungen:

* **Java 26 SDK**
* **Maven**
* **Docker & Docker Compose**

### Schritt-für-Schritt-Anleitung:

1. **PostgreSQL-Container starten:**
  
      `docker-compose up -d`
  
2. **Projekt bauen (falls per Terminal):**
  
      ```mvn clean package```
  
3. **Anwendung starten:** Du kannst die `Main.java` einfach direkt in deiner IDE (z. B. IntelliJ IDEA) ausführen oder im Terminal starten:
  
     ```java -cp target/Media-Ratings-Platform-1.0-SNAPSHOT.jar com.company.Main```
  
  *Beim Start stellt die App die Datenbankverbindung her, baut das Schema auf und startet den HTTP-Server auf Port `8080`.*
  

* * *

## 📮 API-Endpunkte mit der HTTP-Datei testen

Ich habe direkt im Projektordner eine `request.http`-Datei bereitgestellt. Über diese Datei kannst du die komplette API bequem durchtesten.

### Wie du die Requests ausführst:

* **IntelliJ IDEA:** Öffne die `request.http`, dort erscheint neben jedem Request ein kleiner grüner Play-Button `▶`. Klicke einfach darauf, um den Request abzusenden.
* **VS Code:** Installiere die Extension **REST Client**, öffne die `request.http` und klicke auf `Send Request`.

### Beispielszenario in der `request.http`:

1. User registrieren / einloggen (`POST /users`, `POST /users/login`)
2. Neue Medien anlegen (`POST /media`)
3. Medien abrufen (`GET /media`)
4. Bewertungen abgeben und anzeigen (`POST /ratings`, `GET /ratings`)
