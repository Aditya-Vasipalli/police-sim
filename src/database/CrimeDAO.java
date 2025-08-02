package database;
import models.Crime;
import java.util.List;

public class CrimeDAO {
    public void insertCrime(Crime crime) {
        // INSERT INTO Crimes VALUES (...)
    }
    public Crime getCrimeById(int crimeId) {
        // SELECT * FROM Crimes WHERE crime_id = ?
        return null;
    }
    public List<Crime> getAllCrimes() {
        // SELECT * FROM Crimes
        return null;
    }
    public void updateCrimeStatus(int crimeId, String status) {
        // UPDATE Crimes SET status = ? WHERE crime_id = ?
    }
}
