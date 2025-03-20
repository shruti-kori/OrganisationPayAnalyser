import dao.EmployeeRepository;
import service.OrganizationService;

import java.util.Map;
import java.util.Scanner;

public class OrganisationPayAnalyser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the employee CSV file: ");
        String filePath = scanner.nextLine();
        scanner.close();

        EmployeeRepository employeeRepository = new EmployeeRepository(filePath);
        OrganizationService organizationService = new OrganizationService(employeeRepository);

        System.out.println("\nSalary Compliance Analysis:");
        Map<String, Double> salaryIssues = organizationService.analyzeSalaryCompliance();
        if (salaryIssues.isEmpty()) {
            System.out.println("All managers have appropriate salaries.");
        } else {
            salaryIssues.forEach((managerId, discrepancy) -> {
                String issueType = (discrepancy > 0) ? "underpaid" : "overpaid";
                System.out.println("Manager " + managerId + " is " + issueType + " by " + Math.abs(discrepancy));
            });
        }

        System.out.println("\nReporting Line Analysis:");
        Map<String, Integer> reportingIssues = organizationService.analyzeReportingLines();
        if (reportingIssues.isEmpty()) {
            System.out.println("No excessive reporting lines detected.");
        } else {
            reportingIssues.forEach((employeeId, excessLevels) ->
                    System.out.println("Employee " + employeeId + " has a reporting line that is too long by " + excessLevels + " levels."));
        }
    }
}
