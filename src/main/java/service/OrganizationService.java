package service;

import models.Employee;
import repository.EmployeeRepositoryInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrganizationService {

    private final Map<String, Employee> employeeMap;
    private final Map<String, List<Employee>> managerToSubordinates;

    public OrganizationService(EmployeeRepositoryInterface repository) {
        List<Employee> employees = repository.getAllEmployees();
        employeeMap = employees.stream().collect(Collectors.toMap(Employee::getId, e -> e));
        managerToSubordinates = employees.stream()
                .filter(e -> e.getManagerId() != null)
                .collect(Collectors.groupingBy(Employee::getManagerId));
    }

    public Map<String, Double> analyzeSalaryCompliance() {
        Map<String, Double> results = new HashMap<>();
        for (String managerId : managerToSubordinates.keySet()) {
            Employee manager = employeeMap.get(managerId);
            List<Employee> subordinates = managerToSubordinates.get(managerId);
            double avgSalary = subordinates.stream().mapToDouble(Employee::getSalary).average().orElse(0);
            double minSalary = avgSalary * 1.2;
            double maxSalary = avgSalary * 1.5;

            if (manager.getSalary() < minSalary) {
                results.put(managerId, minSalary - manager.getSalary());
            } else if (manager.getSalary() > maxSalary) {
                results.put(managerId, manager.getSalary() - maxSalary);
            }
        }
        return results;
    }

    public Map<String, Integer> analyzeReportingLines() {
        Map<String, Integer> results = new HashMap<>();
        for (Employee employee : employeeMap.values()) {
            int levels = getReportingLevels(employee.getId());
            if (levels > 4) {
                results.put(employee.getId(), levels - 4);
            }
        }
        return results;
    }

    private int getReportingLevels(String employeeId) {
        int levels = 0;
        String currentManagerId = employeeMap.get(employeeId).getManagerId();
        while (currentManagerId != null) {
            levels++;
            Employee manager = employeeMap.get(currentManagerId);
            if (manager == null) break;
            currentManagerId = manager.getManagerId();
        }
        return levels;
    }
}
