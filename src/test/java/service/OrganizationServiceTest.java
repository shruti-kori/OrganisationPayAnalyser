package service;

import models.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.EmployeeRepositoryInterface;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationServiceTest {
    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        List<Employee> employees = List.of(
                new Employee("1", null, 100000), // CEO
                new Employee("2", "1", 45000), // Manager (Compliant)
                new Employee("3", "2", 30000), // Employee
                new Employee("4", "2", 35000)  // Employee
        );
        EmployeeRepositoryInterface mockRepo = () -> employees;
        organizationService = new OrganizationService(mockRepo);
    }

    @Test
    void testSalaryCompliance_AllManagersCompliant() {
        Map<String, Double> salaryIssues = organizationService.analyzeSalaryCompliance();
        assertFalse(salaryIssues.isEmpty(), "One manager should be flagged for salary issues");
    }

    @Test
    void testSalaryCompliance_ManagerUnderpaid() {
        List<Employee> employees = List.of(
                new Employee("1", null, 100000),
                new Employee("2", "1", 35000), // Underpaid Manager
                new Employee("3", "2", 30000),
                new Employee("4", "2", 35000)
        );
        EmployeeRepositoryInterface mockRepo = () -> employees;
        OrganizationService testService = new OrganizationService(mockRepo);

        Map<String, Double> salaryIssues = testService.analyzeSalaryCompliance();
        assertFalse(salaryIssues.isEmpty());
        assertTrue(salaryIssues.containsKey("2")); // Underpaid manager should be flagged
    }

    @Test
    void testSalaryCompliance_ManagerOverpaid() {
        List<Employee> employees = List.of(
                new Employee("1", null, 100000),
                new Employee("2", "1", 80000), // Overpaid Manager
                new Employee("3", "2", 30000),
                new Employee("4", "2", 35000)
        );
        EmployeeRepositoryInterface mockRepo = () -> employees;
        OrganizationService testService = new OrganizationService(mockRepo);

        Map<String, Double> salaryIssues = testService.analyzeSalaryCompliance();
        assertFalse(salaryIssues.isEmpty());
        assertTrue(salaryIssues.containsKey("2")); // Overpaid manager should be flagged
    }

    @Test
    void testReportingLines_DeepHierarchy() {
        List<Employee> employees = List.of(
                new Employee("1", null, 100000),
                new Employee("2", "1", 60000),
                new Employee("3", "2", 50000),
                new Employee("4", "3", 40000),
                new Employee("5", "4", 30000),
                new Employee("6", "5", 20000) // This employee is too deep
        );
        EmployeeRepositoryInterface mockRepo = () -> employees;
        OrganizationService testService = new OrganizationService(mockRepo);

        Map<String, Integer> reportingIssues = testService.analyzeReportingLines();
        assertFalse(reportingIssues.isEmpty());
        assertTrue(reportingIssues.containsKey("6"));
        assertEquals(1, reportingIssues.get("6")); // Should be flagged for excessive depth
    }

    @Test
    void testReportingLines_NoDeepHierarchy() {
        List<Employee> employees = List.of(
                new Employee("1", null, 100000),
                new Employee("2", "1", 60000),
                new Employee("3", "2", 30000),
                new Employee("4", "2", 35000)
        );
        EmployeeRepositoryInterface mockRepo = () -> employees;
        OrganizationService testService = new OrganizationService(mockRepo);

        Map<String, Integer> reportingIssues = testService.analyzeReportingLines();
        assertTrue(reportingIssues.isEmpty());
    }

    @Test
    void testEmptyEmployeeList() {
        EmployeeRepositoryInterface mockRepo = Collections::emptyList;
        OrganizationService testService = new OrganizationService(mockRepo);

        assertTrue(testService.analyzeSalaryCompliance().isEmpty());
        assertTrue(testService.analyzeReportingLines().isEmpty());
    }
}