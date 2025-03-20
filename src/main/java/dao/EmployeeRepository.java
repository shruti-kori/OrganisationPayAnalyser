package dao;

import models.Employee;
import repository.EmployeeRepositoryInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository implements EmployeeRepositoryInterface {
    private final String filePath;

    public EmployeeRepository(String filePath) {
        this.filePath = filePath;
    }
    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String id = parts[0].trim();
                    String managerId = parts[1].trim().isEmpty() ? null : parts[1].trim();
                    double salary = Double.parseDouble(parts[2].trim());
                    employees.add(new Employee(id, managerId, salary));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return employees;
    }
}
