package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.model.Employee;
import io.vertx.core.AbstractVerticle;

import java.sql.*;

public class VerticleWorker  extends AbstractVerticle {
  Connection connection = null;

  public void getConnection() {
    try {
      Class.forName("org.mariadb.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/employee", "root", "root@123");
    } catch (Exception e) {
      e.printStackTrace();
    }


    String sql = "INSERT INTO emp VALUES (?,?,?)";
    try {
      PreparedStatement ps = connection.prepareStatement(sql);
      ps.executeUpdate();
      ps.close();
      connection.close();
    } catch (
      SQLException e) {
      e.printStackTrace();
    }
    System.out.println("Record Inserted Successfully");
  }

  public Employee getId(Long emp_id) {
    String id = "select * from employees where id=?";
    Employee employee = new Employee();
    try {
      Statement statement = connection.createStatement();
      ResultSet resultset = statement.executeQuery(id);
      if (resultset.next()) {
        employee.setId(resultset.getLong("id"));
        employee.setName(resultset.getString("name"));
        employee.setDesignation(resultset.getString("Designation"));
        employee.setSalary(resultset.getDouble("salary"));
        employee.setUsername(resultset.getString("username"));
        employee.setPassword(resultset.getString("password"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return employee;
  }
  public static void main(String[] args) throws Exception {

    int id = 99;

//		// Step#1. Load the Driver class
//		Class.forName("org.mariadb.jdbc.Driver");// Maria DB
//		// Class.forName("com.mysql.jdbc.Driver");//MySQL DB
//
//		// Step#2. Establish the connection
//		Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/boot_camp_db", "root",
//				"root"); // Maria DB
//		// DriverManager.getConnection("jdbc:mysql://localhost:3306/boot_camo_db",
//		// "root", "root"); //MySQL DB

    Connection connection = null;

    // Step#3. Write the SQL queries
    String sql = "DELETE FROM emp WHERE id=?";

    // Step#4. Get a carrier
    PreparedStatement ps = connection.prepareStatement(sql);

    ps.setInt(1, id);

    // Step#5 Execute the SQL query.
    ps.executeUpdate();

    // Step#6 Close the resources.
    ps.close();
    connection.close();
    System.out.println("Record Deleted Successfully");

  }


}
