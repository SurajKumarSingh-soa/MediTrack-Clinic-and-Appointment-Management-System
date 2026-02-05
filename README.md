# MediTrack — Clinic & Appointment Management System

A comprehensive, object-oriented **Clinic & Appointment Management System** built in Core Java. MediTrack demonstrates best practices in OOP design, SOLID principles, modern Java features (Streams, Lambdas), design patterns, and more.

---

## 📋 Features

### Core Features
- **Doctor Management**: Add, update, search, delete doctors with specializations
- **Patient Management**: CRUD operations with medical history and allergies tracking
- **Appointment System**: Book, cancel, complete appointments with status tracking
- **Billing System**: Generate bills with tax calculation, emergency surcharges
- **Search Functionality**: Polymorphic search by ID, name, specialization, age, etc.
- **Data Persistence**: Save/load data using CSV files

### Advanced Features
- **AI-Powered Recommendations**: Rule-based doctor recommendations based on symptoms
- **Analytics Dashboard**: Real-time statistics using Java 8 Streams
- **Observer Pattern**: Appointment notifications
- **Deep Cloning**: Proper deep copy implementation for entities
- **Immutable Objects**: Thread-safe BillSummary class
- **Exception Handling**: Custom exceptions with proper chaining

---

## 🏗️ Architecture

### Package Structure
```
src/main/java/com/airtribe/meditrack/
├── Main.java                          # Entry point
├── constants/
│   ├── Constants.java                 # Application constants
│   ├── Specialization.java           # Doctor specialization enum
│   └── AppointmentStatus.java        # Appointment status enum
├── entity/
│   ├── Person.java                    # Abstract base class
│   ├── Doctor.java                    # Doctor entity
│   ├── Patient.java                   # Patient entity (Cloneable)
│   ├── Appointment.java               # Appointment entity
│   ├── Bill.java                      # Bill entity (Payable)
│   └── BillSummary.java              # Immutable bill summary
├── service/
│   ├── DoctorService.java            # Doctor business logic
│   ├── PatientService.java           # Patient business logic
│   └── AppointmentService.java       # Appointment & billing logic
├── util/
│   ├── IdGenerator.java              # Singleton ID generator
│   ├── DataStore.java                # Generic in-memory data store
│   ├── CSVUtil.java                  # File I/O utility
│   ├── Validator.java                # Validation utility
│   ├── DateUtil.java                 # Date operations
│   └── AIHelper.java                 # AI recommendation engine
├── exception/
│   ├── AppointmentNotFoundException.java
│   └── InvalidDataException.java
├── interfaces/
│   ├── Searchable.java               # Search contract
│   └── Payable.java                  # Billing contract
└── test/
    └── TestRunner.java               # Manual test suite
```

---

## 🎯 OOP Concepts Demonstrated

### Encapsulation
- Private fields with getters/setters
- Centralized validation via `Validator` class

### Inheritance
- `Person` → `Doctor`, `Patient` hierarchy
- Constructor chaining with `super()`

### Polymorphism
- **Method Overloading**: `searchPatient(int id)`, `searchPatient(String name)`, `searchPatient(int age)`
- **Method Overriding**: `toString()`, `equals()`, `hashCode()`
- **Dynamic Dispatch**: Interface-based programming

### Abstraction
- Abstract class `Person` with abstract method `getRole()`
- Interfaces: `Searchable<T>`, `Payable`
- Default methods in interfaces

### Advanced OOP
- **Deep Cloning**: `Patient` and `Appointment` implement `Cloneable`
- **Immutability**: `BillSummary` (final class, final fields, no setters)
- **Enums**: `Specialization`, `AppointmentStatus` with display names
- **Static Blocks**: Initialization in `Constants`, `IdGenerator`

---

## 🚀 Quick Start

### Prerequisites
- **Java JDK 11+** ([Download](https://adoptium.net/))
- Windows, macOS, or Linux

### Compilation

1. **Create the output directory**:
   ```bash
   mkdir bin
   ```

2. **Compile all Java files**:
   ```bash
   # Windows PowerShell
   javac -d bin -sourcepath src\main\java src\main\java\com\airtribe\meditrack\*.java src\main\java\com\airtribe\meditrack\constants\*.java src\main\java\com\airtribe\meditrack\entity\*.java src\main\java\com\airtribe\meditrack\exception\*.java src\main\java\com\airtribe\meditrack\interfaces\*.java src\main\java\com\airtribe\meditrack\service\*.java src\main\java\com\airtribe\meditrack\util\*.java src\main\java\com\airtribe\meditrack\test\*.java

   # macOS/Linux
   javac -d bin -sourcepath src/main/java src/main/java/com/airtribe/meditrack/*.java src/main/java/com/airtribe/meditrack/constants/*.java src/main/java/com/airtribe/meditrack/entity/*.java src/main/java/com/airtribe/meditrack/exception/*.java src/main/java/com/airtribe/meditrack/interfaces/*.java src/main/java/com/airtribe/meditrack/service/*.java src/main/java/com/airtribe/meditrack/util/*.java src/main/java/com/airtribe/meditrack/test/*.java
   ```

### Running the Application

```bash
# Run the main application
java -cp bin com.airtribe.meditrack.Main

# Load with sample data
java -cp bin com.airtribe.meditrack.Main --loadData
```

### Running Tests

```bash
# Run the test suite
java -cp bin com.airtribe.meditrack.test.TestRunner
```

---

## 💡 Usage Examples

### Menu Navigation
1. **Manage Doctors**: Add, view, search, update, delete doctors
2. **Manage Patients**: Patient CRUD operations with allergy tracking
3. **Manage Appointments**: Book, cancel, complete appointments
4. **Billing**: Generate bills with tax calculations
5. **View Analytics**: Real-time statistics and reports
6. **AI Features**: Get doctor recommendations based on symptoms
7. **Save/Load Data**: Persist data to CSV files

### AI Doctor Recommendation Example
```
Enter symptoms: chest pain and palpitation
→ Recommended Specialization: Cardiology (Confidence: 90%)
→ Available Doctors:
  - Dr. Rajesh Kumar (Fee: ₹1500)
```

---

## 📚 Documentation

Detailed documentation is available in the `docs/` directory:

- **[Setup Instructions](docs/Setup_Instructions.md)**: Complete setup guide with screenshots
- **[JVM Report](docs/JVM_Report.md)**: Deep dive into JVM internals
- **[Design Decisions](docs/Design_Decisions.md)**: Architecture and patterns explained

---

## 🧪 Testing

The project includes a comprehensive manual test suite (`TestRunner.java`) covering:

✅ CRUD Operations (Doctor, Patient, Appointment)  
✅ Billing System & Payable Interface  
✅ Polymorphism (Method Overloading)  
✅ Deep Copy (Cloning)  
✅ Immutability (BillSummary)  
✅ Enums (Specialization, AppointmentStatus)  
✅ Exception Handling (Custom Exceptions)  
✅ Validation  
✅ Observer Pattern (Notifications)  
✅ Java 8 Streams & Lambdas  
✅ AI Feature (Symptom-based Recommendations)  
✅ File I/O & Persistence  

Run all tests:
```bash
java -cp bin com.airtribe.meditrack.test.TestRunner
```

---

## 🎨 Design Patterns

1. **Singleton**: `IdGenerator` for centralized ID generation
2. **Factory**: Bill creation with different types
3. **Observer**: Appointment notifications
4. **Template Method**: Validation in `Person` class
5. **Strategy**: Different billing calculation strategies

---

## 📊 Java Features Demonstrated

### Collections
- `ArrayList`, `HashMap`, `TreeMap`
- Generic `DataStore<T>` class

### Generics
- Type-safe collections
- Generic methods and classes

### Java 8+ Features
- **Streams API**: Filtering, mapping, sorting, analytics
- **Lambda Expressions**: Inline functional implementations
- **Method References**: `System.out::println`, `Doctor::getName`
- **Default Methods**: In `Searchable` and `Payable` interfaces

### Concurrency
- `AtomicInteger` for thread-safe ID generation
- Singleton pattern with thread safety

### File I/O
- **CSV Parsing**: Custom CSV utility
- **Try-with-Resources**: Automatic resource management
- **Serialization**: Support for Java object serialization

---

## 📁 Data Persistence

Data is saved in CSV format in the `data/` directory:
- `data/doctors.csv`
- `data/patients.csv`
- `data/appointments.csv`

Use menu options 7 (Save) and 8 (Load) to persist and restore data.

---

## 🏆 Project Highlights

- ✨ **100% Core Java** (No external dependencies)
- 📦 **Modular Design** with clear separation of concerns
- 🎯 **SOLID Principles** applied throughout
- 🧪 **Comprehensive Testing** with 50+ test cases
- 📖 **Well-Documented** with JavaDoc comments
- 🚀 **Production-Ready** code quality

---

## 👥 Contributors

- **Your Name** - [GitHub](https://github.com/yourusername)

---

## 📄 License

This project is created for educational purposes as part of the Airtribe Java mentorship program.

---

## 🔗 Resources

- [Java Documentation](https://docs.oracle.com/en/java/)
- [Design Patterns](https://refactoring.guru/design-patterns)
- [Clean Code Principles](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)

---

**Built with ❤️ using Core Java**
