# SPL12

## Description

This project is a digital implementation of the board game **Splendor**, developed for the course *Practical Software Engineering* at the University of Mannheim.

### Authors

* Enrico Naumann – [enrico.naumann@students.uni-mannheim.de](mailto:enrico.naumann@students.uni-mannheim.de)
* Leon Kuersch – [leon.kuersch@students.uni-mannheim.de](mailto:leon.kuersch@students.uni-mannheim.de)
* Noyan Morali – [noyan.morali@students.uni-mannheim.de](mailto:noyan.morali@students.uni-mannheim.de)
* Leonardo Melodia – [leonardo.melodia@students.uni-mannheim.de](mailto:leonardo.melodia@students.uni-mannheim.de)
* Jan Walter – [jan.walter@students.uni-mannheim.de](mailto:jan.walter@students.uni-mannheim.de)
* Lukas Ott – [lukas.ott@students.uni-mannheim.de](mailto:lukas.ott@students.uni-mannheim.de)

---

## Requirements

* **JDK 21** must be installed.
  ⚠️ If using a JavaFX-bundled JDK like **Liberica Full JDK**, you can skip using `javafx_lib.zip` and omit the `--module-path` and related parameters (see below).

---

## Installation Options

### Option 1: Build from Source (Recommended for Developers)

1. Ensure Maven is installed.
2. Download [`javafx_lib.zip`](https://github.com/lukas-ott/splendor/releases/download/Alpha/javafx_lib.zip).
3. Extract it into the project directory.
4. In the project root, run:

   ```bash
   mvn clean install
   ```

### Option 2: Use Prebuilt JARs

1. Download the latest JARs from the [Releases page](https://github.com/lukas-ott/splendor/releases).
2. (Optional) Download and extract `javafx_lib.zip` if you're not using a JavaFX-bundled JDK.

For detailed run instructions and multiplayer info, refer to `docs/other_artefacts/User Manual.md`.

---

## Running the Game

### Start the Server (All OS)

```bash
java -jar server/target/server.jar
```

### Start the Clients (Platform-Specific)

> Replace `javafx_lib/...` paths as needed depending on your platform and CPU architecture.

#### macOS (Apple Silicon)

```bash
# Client
java --module-path javafx_lib/mac_aarch64/javafx-sdk-21.0.6/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
     -jar client/target/client.jar

# AI Client
java --module-path javafx_lib/mac_aarch64/javafx-sdk-21.0.6/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
     -jar ai_client/target/ai_client.jar
```

#### macOS (Intel)

```bash
# Client
java --module-path javafx_lib/mac_x64/javafx-sdk-21.0.6/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
     -jar client/target/client.jar

# AI Client
java --module-path javafx_lib/mac_x64/javafx-sdk-21.0.6/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
     -jar ai_client/target/ai_client.jar
```

#### Windows

```bash
# Client
java --module-path javafx_lib/win_x64/javafx-sdk-21.0.6/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
     -jar client/target/client.jar

# AI Client
java --module-path javafx_lib/win_x64/javafx-sdk-21.0.6/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
     -jar ai_client/target/ai_client.jar
```

#### Linux

```bash
# Client
java --module-path javafx_lib/linux_x64/javafx-sdk-21.0.6/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
     -jar client/target/client.jar

# AI Client
java --module-path javafx_lib/linux_x64/javafx-sdk-21.0.6/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
     -jar ai_client/target/ai_client.jar
```

---
