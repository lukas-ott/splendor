# ConstantsManager Documentation

## ConstantsManager.java

Central utility class for storing and accessing game-wide constant values.

## Key Methods

### Accessors
- `getConstant(String key)`: Returns the value associated with a given constant key.
- `loadConstants()`: Loads constants from a configuration source.
- `updateConstant(String key, String value)`: Updates the value of a specified constant.

## Attributes

- `constants`: A map storing key-value pairs of configuration constants.

## Usage in Game Flow

`ConstantsManager` is used across the application to ensure consistent access to configuration settings such as default values, thresholds, or labels.

jan.philip.walter May 23. 2025