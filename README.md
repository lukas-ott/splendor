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
3. If on Windows or Linus, javafx.lib is present and unzipped.

## Installation
1. Unzip javafx_lib.zip - The resulting folder must be located at the same location the zip is located. The resulting folder must have the same name (javafx_lib) and contents (4 folders: linux_x64, mac_aarch64, mac_x64 and win_x64) as the zip
2. Run
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
