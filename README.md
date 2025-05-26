# SPL12

## Description
This project is the board game "Splendor" developed for the course "Software Engineering" at the University of Mannheim.
### Authors
- Enrico Naumann: enrico.naumann@students.uni-mannheim.de
- Leon Kuersch: leon.kuersch@students.uni-mannheim.de
- Noyan Morali: noyan.morali@students.uni-mannheim.de
- Leonardo Melodia: leonardo.melodia@students.uni-mannheim.de
- Jan Walter: jan.walter@students.uni-mannheim.de
- Lukas Ott: lukas.ott@students.uni-mannheim.de

## Requirements
1. JDK 21 is installed.
2. Maven is installed (to compile jars)

## Installation
1. Download [javafx_lib.zip](https://github.com/youruser/repo/releases/download/v1.0/javafx_lib.zip)
2. Unzip it into the project directory
3. Run
```
mvn clean install
```

## Running the jars
### Start the server (all OSs)
```
java -jar server/target/server.jar
```

### Mac (M chips)
2. Start client
```
java --module-path javafx_lib/mac_aarch64/javafx-sdk-21.0.6/lib --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED -jar client/target/client.jar
```
3. Start AI client
```
java --module-path javafx_lib/mac_aarch64/javafx-sdk-21.0.6/lib --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED -jar ai_client/target/ai_client.jar
```

### Mac (old Intel chip)
2. Start client
```
java --module-path javafx_lib/mac_x64/javafx-sdk-21.0.6/lib --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED -jar client/target/client.jar
```
3. Start AI client
```
java --module-path javafx_lib/mac_x64/javafx-sdk-21.0.6/lib --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED -jar ai_client/target/ai_client.jar
```

### Windows
2. Start client
```
java --module-path javafx_lib/win_x64/javafx-sdk-21.0.6/lib --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED -jar client/target/client.jar
```
3. Start AI client
```
java --module-path javafx_lib/win_x64/javafx-sdk-21.0.6/lib --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED -jar ai_client/target/ai_client.jar
```

### Linux
2. Start client
```
java --module-path javafx_lib/linux_x64/javafx-sdk-21.0.6/lib --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED -jar client/target/client.jar
```
3. Start AI client
```
java --module-path javafx_lib/linux_x64/javafx-sdk-21.0.6/lib --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED -jar ai_client/target/ai_client.jar
```
