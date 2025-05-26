# DatabaseConnector Documentation

## DatabaseConnector.java

Handles database connections and connectivity logic.

## Key Methods

- `connect()`: Establishes a connection to the database.
- `disconnect()`: Closes the active database connection.
- `getConnection()`: Returns the current database connection instance.

## Attributes

- `connection`: Stores the active JDBC connection.
- `databaseUrl`: Path or URI of the target database.

## Usage in Game Flow

`DatabaseConnector` is used by various components that require persistent data access.

jan.philip.walter May 23. 2025