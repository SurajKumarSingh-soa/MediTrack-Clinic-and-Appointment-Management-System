# MediTrack Setup Instructions

## Prerequisites
- **Java Development Kit (JDK)**: Version 11 or higher
- **Operating System**: Windows, macOS, or Linux
- **Terminal/Command Prompt**: For running commands

## Step 1: Install Java (JDK)

### For Windows:
1. Download JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. Run the installer and follow the installation wizard
3. Set JAVA_HOME environment variable:
   - Right-click "This PC" → Properties → Advanced System Settings
   - Click "Environment Variables"
   - Under System Variables, click "New"
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-<version>` (your JDK installation path)
4. Add Java to PATH:
   - Edit the "Path" variable under System Variables
   - Add: `%JAVA_HOME%\bin`

### For macOS:
```bash
# Using Homebrew
brew install openjdk@11
```

### For Linux:
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

## Step 2: Verify Installation

Open a terminal/command prompt and run:
```bash
java -version
javac -version
```

Expected output should show Java version 11 or higher.

**Screenshot Placeholder**: [Screenshot of java -version output]

## Step 3: Compile the Project

Navigate to the project directory:
```bash
cd c:/Users/Subhojit/OneDrive/Desktop/MediTrack
```

Compile all Java files:
```bash
javac -d bin -sourcepath src/main/java src/main/java/com/airtribe/meditrack/Main.java src/main/java/com/airtribe/meditrack/**/*.java
```

Or use the provided compile script:
```bash
# Windows
compile.bat

# macOS/Linux
./compile.sh
```

## Step 4: Run the Application

```bash
java -cp bin com.airtribe.meditrack.Main
```

Or use the run script:
```bash
# Windows
run.bat

# macOS/Linux
./run.sh
```

### Command-Line Arguments

Load data from CSV files on startup:
```bash
java -cp bin com.airtribe.meditrack.Main --loadData
```

## Step 5: Run Tests

```bash
java -cp bin com.airtribe.meditrack.test.TestRunner
```

## Project Structure

```
MediTrack/
├── src/main/java/com/airtribe/meditrack/
│   ├── Main.java
│   ├── constants/
│   ├── entity/
│   ├── service/
│   ├── util/
│   ├── exception/
│   ├── interface/
│   └── test/
├── docs/
├── data/          # CSV files for persistence
└── bin/           # Compiled .class files
```

## Troubleshooting

### "javac not recognized"
- Ensure JAVA_HOME is set correctly
- Verify Java bin directory is in PATH

### ClassNotFoundException
- Ensure you're running from the correct directory
- Verify the classpath (-cp bin) is correct

### CSV files not found
- Create a `data/` directory in the project root
- Files will be auto-created on first save

## Next Steps
- Run the application and explore the menu options
- Try adding doctors, patients, and appointments
- Test the billing functionality
- Explore the AI-based doctor recommendation feature
