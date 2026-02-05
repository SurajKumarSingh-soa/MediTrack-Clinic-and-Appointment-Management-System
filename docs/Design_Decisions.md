# MediTrack Design Decisions

## Architecture Overview
MediTrack follows a **layered architecture** with clear separation of concerns:
- **Entity Layer**: Core domain models
- **Service Layer**: Business logic and orchestration
- **Utility Layer**: Helper classes and infrastructure
- **Interface Layer**: Contracts for common behavior

## Design Patterns Used

### 1. Singleton Pattern
**Class**: `IdGenerator`
- **Purpose**: Ensure single instance for ID generation across the application
- **Implementation**: Both eager and lazy initialization demonstrated
- **Benefit**: Thread-safe, centralized ID management

### 2. Factory Pattern
**Class**: `BillFactory` (in service layer)
- **Purpose**: Create different types of bills based on appointment type
- **Benefit**: Encapsulates object creation logic

### 3. Template Method Pattern
**Class**: `MedicalEntity` (abstract base)
- **Purpose**: Define skeleton of validation process
- **Benefit**: Reusable validation framework

### 4. Observer Pattern
**Class**: `AppointmentService` with notification listeners
- **Purpose**: Notify relevant parties when appointments change
- **Benefit**: Loose coupling between appointment system and notification system

### 5. Strategy Pattern
**Class**: Billing strategies for different payment methods
- **Purpose**: Different billing calculation strategies
- **Benefit**: Easy to add new billing types

## OOP Principles Applied

### Encapsulation
- All entity fields are private
- Access through getters/setters with validation
- `Validator` utility centralizes validation logic

### Inheritance
- `Person` → `Doctor`, `Patient`
- Constructor chaining with `super()`
- Demonstrates IS-A relationship

### Polymorphism
- Method overloading: `searchPatient(int id)`, `searchPatient(String name)`
- Method overriding: `toString()`, `equals()`, `hashCode()`
- Dynamic dispatch through interfaces

### Abstraction
- Abstract class `Person` defines common behavior
- Interfaces `Searchable`, `Payable` define contracts
- Default methods in interfaces for common implementations

## Advanced Java Features

### Immutability
**Class**: `BillSummary`
- All fields final
- No setters
- Defensive copying for mutable fields
- Thread-safe by design

### Cloning
**Classes**: `Patient`, `Appointment`
- Implement `Cloneable` interface
- Deep copy of nested objects
- Prevents unintended shared references

### Enums
- `Specialization`: Type-safe doctor specializations
- `AppointmentStatus`: State management for appointments
- Better than String constants (compile-time safety)

### Generics
**Class**: `DataStore<T>`
- Type-safe in-memory storage
- Reusable across entity types
- Eliminates casting

### Static Initialization
- Static blocks in `Constants` for application-wide configuration
- Static initialization in `IdGenerator`

## Data Management

### In-Memory Storage
- `DataStore<T>` generic class for runtime storage
- Separate stores for Doctor, Patient, Appointment

### Persistence
- **Format**: CSV (human-readable, easy to debug)
- **Classes**: `CSVUtil` for read/write operations
- **Location**: `data/` directory
- **Files**: `doctors.csv`, `patients.csv`, `appointments.csv`

### Serialization
- Implemented additional Java serialization support
- Binary format for alternative persistence

## Exception Handling

### Custom Exceptions
- `AppointmentNotFoundException`: Specific domain exception
- `InvalidDataException`: Validation failures
- **Chaining**: Preserve stack traces with `throw new Exception(message, cause)`

### Try-With-Resources
- Used in `CSVUtil` for automatic resource management
- Ensures files are properly closed

## Collections Framework

### Data Structures Used
- **ArrayList**: Dynamic lists for patients, doctors, appointments
- **HashMap**: Fast lookups by ID
- **TreeMap**: Sorted data (appointments by date)

### Comparators
- Custom `Comparator` for sorting appointments by date
- Lambda expressions for inline sorting

## Java 8+ Features

### Streams API
Used extensively for:
- Filtering doctors by specialization
- Computing statistics (average fee, appointment counts)
- Generating reports

### Lambda Expressions
- Inline comparators: `(a, b) -> a.getDate().compareTo(b.getDate())`
- Stream operations: `.filter(d -> d.getSpecialization() == spec)`
- forEach loops: `.forEach(System.out::println)`

### Method References
- `System.out::println`
- `Doctor::getName`

## AI Feature (Rule-Based)

**Class**: `AIHelper`
- **Input**: Patient symptoms (keywords)
- **Process**: Rule-based matching to specializations
- **Output**: Recommended doctor specialization
- **Example**: "chest pain" → CARDIOLOGY

### Algorithm
1. Tokenize symptoms
2. Match keywords to specialization map
3. Return best match with confidence score

## Concurrency Considerations

### Thread Safety
- `IdGenerator`: Singleton with synchronized methods
- `AtomicInteger`: For thread-safe ID generation
- Immutable objects (`BillSummary`) inherently thread-safe

### Future Enhancements
- `TimerTask` for appointment reminders (demonstrated in comments)
- Thread pool for background operations

## Code Organization

### Package Structure
```
com.airtribe.meditrack/
├── constants/      # Global constants and enums
├── entity/         # Domain models
├── exception/      # Custom exceptions
├── interface/      # Contracts
├── service/        # Business logic
├── util/           # Helpers and infrastructure
└── test/           # Manual tests
```

### Naming Conventions
- Classes: PascalCase (`PatientService`)
- Methods: camelCase (`addPatient`)
- Constants: UPPER_SNAKE_CASE (`TAX_RATE`)
- Packages: lowercase (`com.airtribe.meditrack`)

## Testing Strategy

### Manual Testing
**Class**: `TestRunner`
- Scenario-based tests
- Tests for each core feature
- Validation of edge cases

### Test Coverage
- CRUD operations for all entities
- Cloning (deep copy verification)
- Exception handling
- File I/O
- Search functionality
- Billing calculations

## SOLID Principles

### Single Responsibility
Each class has one reason to change:
- `Doctor`: Represents doctor entity only
- `DoctorService`: Manages doctor operations only
- `Validator`: Validates data only

### Open/Closed
- Open for extension (new billing strategies)
- Closed for modification (core entities stable)

### Liskov Substitution
- `Doctor` and `Patient` can substitute `Person`
- Interface implementations are substitutable

### Interface Segregation
- Small, focused interfaces (`Searchable`, `Payable`)
- Clients not forced to depend on unused methods

### Dependency Inversion
- Services depend on abstractions (interfaces)
- High-level modules don't depend on low-level details

## Performance Optimizations

- HashMap for O(1) lookups
- Lazy initialization where appropriate
- Stream operations for efficient filtering
- StringBuilder for string concatenation

## Security Considerations

- Input validation via `Validator`
- No SQL injection (no database)
- Safe file operations with path validation

## Future Enhancements

1. **Database Integration**: Replace CSV with JDBC
2. **GUI**: Swing/JavaFX interface
3. **REST API**: Spring Boot web service
4. **Authentication**: User roles and permissions
5. **Scheduling**: Advanced appointment scheduling algorithms
6. **Reports**: PDF generation for bills and reports
