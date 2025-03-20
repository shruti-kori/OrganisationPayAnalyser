package models;

public class Employee {
    private final String id;
    private final String managerId;
    private final double salary;

    public Employee(String id, String managerId, double salary) {
        this.id = id;
        this.managerId = managerId;
        this.salary = salary;
    }

    public String getId() { return id; }
    public String getManagerId() { return managerId; }
    public double getSalary() { return salary; }
}
