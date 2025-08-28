# Endeavour CLI

A command-line interface for the Endeavour library that demonstrates functional error handling using Picocli.

## Features

- **Success Example**: Demonstrates successful Outcome handling
- **Failure Example**: Shows how failures are handled gracefully
- **Chain Example**: Illustrates chaining multiple operations with error handling
- **Verbose Mode**: Provides detailed output for debugging

## Usage

### Build the project

```bash
mvn clean package
```

### Run the CLI

```bash
# Run the demo (default)
java -jar target/endeavour-cli-1.0.0-SNAPSHOT.jar

# Run specific examples
java -jar target/endeavour-cli-1.0.0-SNAPSHOT.jar success
java -jar target/endeavour-cli-1.0.0-SNAPSHOT.jar failure
java -jar target/endeavour-cli-1.0.0-SNAPSHOT.jar chain

# Enable verbose mode
java -jar target/endeavour-cli-1.0.0-SNAPSHOT.jar -v chain

# Show help
java -jar target/endeavour-cli-1.0.0-SNAPSHOT.jar --help
```

### Example Output

```
=== Endeavour CLI Demo ===

1. Success Example:
✅ Success: Hello from Endeavour!

2. Failure Example:
❌ Expected failure: This is a demonstration error
   Error code: demo.error

3. Chain Example:
✅ Chain completed successfully: initial -> step1 -> step2 -> step3
```

## Commands

- `demo` (default): Runs all examples
- `success`: Demonstrates successful Outcome handling
- `failure`: Shows failure handling
- `chain`: Demonstrates chaining operations with error handling

## Options

- `-v, --verbose`: Enable verbose output
- `-h, --help`: Show help information
- `-V, --version`: Show version information

## Dependencies

- **endeavour**: Core Endeavour library for functional error handling
- **picocli**: Command-line interface framework
- **lombok**: Reduces boilerplate code
- **logback**: Logging framework
