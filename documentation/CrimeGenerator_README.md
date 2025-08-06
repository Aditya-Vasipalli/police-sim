# CrimeGenerator Module Documentation

## Overview
The CrimeGenerator module is responsible for generating and managing crime events in the Police Response Time Simulator. It provides comprehensive functionality for creating random or specific crimes, maintaining active crime lists, and managing crime lifecycle.

## Features

### Core Functionality
- **Random Crime Generation**: Generates crimes with realistic probabilities and distributions
- **Specific Crime Creation**: Allows creation of crimes with specific parameters
- **Active Crime Management**: Maintains and tracks all active crimes in the system
- **Crime Lifecycle Management**: Handles crime status updates and resolution
- **Crime Escalation**: Automatically escalates crimes that remain unresolved for too long

### Crime Types Supported
- THEFT
- ASSAULT
- BURGLARY
- VANDALISM
- DRUG_OFFENSE
- ROBBERY
- DOMESTIC_VIOLENCE
- FRAUD
- TRAFFIC_VIOLATION
- EMERGENCY

### Severity Levels
- **LOW** (40% probability) - Minor incidents
- **MEDIUM** (35% probability) - Standard crimes
- **HIGH** (20% probability) - Serious crimes requiring immediate attention
- **CRITICAL** (5% probability) - Emergency situations

## API Reference

### Constructor
```java
public CrimeGenerator(CityMap cityMap)
```
Initializes the crime generator with the given city map.

### Core Methods

#### Crime Generation
```java
public Crime generateCrime()
```
Generates a random crime based on current probabilities and settings.

```java
public Crime generateSpecificCrime(int locationId, String crimeType, String severity)
```
Creates a crime with specific parameters.

```java
public List<Crime> generateMultipleCrimes(int count)
```
Generates multiple crimes at once for testing or scenario setup.

#### Crime Retrieval
```java
public List<Crime> getActiveCrimes()
```
Returns all currently active crimes.

```java
public List<Crime> getCrimesBySeverity(String severity)
```
Returns crimes filtered by severity level.

```java
public List<Crime> getCrimesByType(String crimeType)
```
Returns crimes filtered by crime type.

#### Crime Management
```java
public boolean resolveCrime(int crimeId)
```
Marks a crime as resolved and removes it from active list.

```java
public boolean updateCrimeStatus(int crimeId, String status)
```
Updates the status of a specific crime.

```java
public void updateCrimes()
```
Updates all crimes, handling escalation for long-running crimes.

#### Statistics and Configuration
```java
public Map<String, Object> getCrimeStatistics()
```
Returns comprehensive statistics about current crimes.

```java
public void setGenerationRate(double rate)
```
Sets the probability of crime generation per cycle (0.0 to 1.0).

```java
public void setMaxActiveCrimes(int max)
```
Sets the maximum number of active crimes allowed.

```java
public double getGenerationRate()
public int getMaxActiveCrimes()
```
Getters for current configuration.

## Usage Examples

### Basic Usage
```java
// Initialize
CityMap cityMap = new CityMap("city_map.csv");
CrimeGenerator crimeGenerator = new CrimeGenerator(cityMap);

// Generate a random crime
Crime crime = crimeGenerator.generateCrime();

// Get all active crimes
List<Crime> activeCrimes = crimeGenerator.getActiveCrimes();

// Resolve a crime
crimeGenerator.resolveCrime(crime.getCrimeId());
```

### Advanced Usage
```java
// Generate specific high-priority crime
Crime emergencyCrime = crimeGenerator.generateSpecificCrime(42, "EMERGENCY", "CRITICAL");

// Get high-priority crimes only
List<Crime> urgentCrimes = crimeGenerator.getCrimesBySeverity("CRITICAL");

// Update crime status to in-progress
crimeGenerator.updateCrimeStatus(emergencyCrime.getCrimeId(), "IN_PROGRESS");

// Get statistics
Map<String, Object> stats = crimeGenerator.getCrimeStatistics();
System.out.println("Total active crimes: " + stats.get("totalActiveCrimes"));
```

### Configuration
```java
// Increase crime generation rate for testing
crimeGenerator.setGenerationRate(0.8);

// Allow more concurrent crimes
crimeGenerator.setMaxActiveCrimes(100);

// Generate multiple test crimes
List<Crime> testCrimes = crimeGenerator.generateMultipleCrimes(10);
```

## Integration Points

### Database Integration
- Automatically persists crimes to database via `CrimeDAO`
- Updates crime status in database when modified
- Maintains data consistency between in-memory and persistent storage

### City Map Integration
- Uses city map to generate realistic crime locations
- Respects city boundaries and valid location IDs
- Can be extended to use location-based crime probabilities

### Police Management Integration
- Provides crime data for police unit assignment
- Supports crime status updates from police response system
- Enables tracking of response times and effectiveness

## Crime Escalation System

The module includes an automatic escalation system:
- Crimes that remain active for more than 5 minutes are automatically escalated
- Escalation path: LOW → MEDIUM → HIGH → CRITICAL
- Database is automatically updated when escalation occurs
- System logs all escalation events for analysis

## Error Handling

The module includes robust error handling:
- Validates input parameters
- Handles database connection issues gracefully
- Provides meaningful error messages and logging
- Fails safely without corrupting system state

## Testing

Use the provided `CrimeGeneratorTest` class to verify functionality:
```bash
javac -cp . CrimeGeneratorTest.java
java CrimeGeneratorTest
```

The test class demonstrates all major features and validates correct operation.

## Configuration Options

### Default Settings
- Base generation rate: 0.1 (10% chance per cycle)
- Maximum active crimes: 50
- Escalation time: 5 minutes

### Customizable Parameters
- Crime generation probability
- Maximum concurrent crimes
- Crime type probabilities (modify CRIME_TYPES array)
- Severity distribution (modify SEVERITY_PROBABILITIES array)
- Escalation timing

## Performance Considerations

- Active crimes list is maintained in memory for fast access
- Database operations are optimized for batch updates
- Crime statistics are calculated on-demand
- Automatic cleanup of resolved crimes prevents memory leaks

## Future Enhancements

Potential areas for enhancement:
1. **Location-based Generation**: Different crime rates by area
2. **Time-based Patterns**: Higher crime rates during certain hours
3. **Weather Integration**: Crime patterns affected by weather
4. **Historical Analysis**: Learn from past crime patterns
5. **Event Correlation**: Related crimes or crime sprees

## Dependencies

- `models.Crime`: Crime data model
- `database.CrimeDAO`: Database access layer
- `CityMap`: City geography and location data
- Java standard libraries: `java.util.*`, `java.sql.Timestamp`

## Thread Safety

The current implementation is not thread-safe. For multi-threaded use:
- Synchronize access to `activeCrimes` list
- Use concurrent collections
- Implement proper locking for database operations

---

**Module Status**: ✅ Complete and Ready for Production

**Last Updated**: August 2025

**Author**: Development Team

**Review Status**: Ready for team leader review and integration
