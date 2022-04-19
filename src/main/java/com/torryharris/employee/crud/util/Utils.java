package com.torryharris.employee.crud.util;

import com.torryharris.employee.crud.model.Employee;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {
  /**
   * Get an error response object with the given error message
   *
   * @param errorMessage error message to set
   * @return {@link JsonObject}
   */
  public static JsonObject getErrorResponse(String errorMessage) {
    return new JsonObject()
      .put("error", new JsonObject().put("message", errorMessage));
  }

  public static Object getJsonMessage(String errorMessage) {
    return new JsonObject()
      .put(errorMessage, new JsonObject()).put("message", errorMessage);
  }

  public static List<Employee> get(Long id) {
    List<Employee> employees = new ArrayList<>();
    Employee employee = new Employee();
    Connection connection = null;

    try {
      Class.forName("org.mariadb.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/employee", "root", "Chanup@123");


      Statement statement = connection.createStatement();
      ResultSet res = statement.executeQuery("select * from employees where id=" + id);
      if (res.next()) {
        employee.setId(res.getLong("id"));
        employee.setName(res.getString("name"));
        employee.setDesignation(res.getString("designation"));
        employee.setSalary(res.getDouble("salary"));
        employee.setUsername(res.getString("username"));
        employee.setPassword(res.getString("password"));
        System.out.println("hello");
        employees.add(employee);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return employees;
  }
}

