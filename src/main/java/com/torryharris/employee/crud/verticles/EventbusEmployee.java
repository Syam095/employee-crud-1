package com.torryharris.employee.crud.verticles;
import com.torryharris.employee.crud.dao.Dao;
import com.torryharris.employee.crud.dao.impl.EmployeeJdbcDao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class EventbusEmployee extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(EventbusEmployee.class);
  private final Dao<Employee> employeeDao;
  public EventbusEmployee(Vertx vertx) {
    employeeDao = new EmployeeJdbcDao(vertx) {
      @Override
      public void delete(String id) {

      }
    };
  }



  @Override
  public void start(Promise<Void> promise) throws Exception {

    vertx.eventBus().consumer("id", message -> {
      Response response = new Response();
      String id = ((String) message.body());
      employeeDao.get(id)
        .future()
        .onSuccess(emp -> {
            response.setStatusCode(200).setResponseBody(emp.toString());
            message.reply(response);
          })
        .onFailure(handler -> {
        LOGGER.catching(handler);
        });
    });
    vertx.eventBus().consumer("get", employee -> {
      employeeDao.getAll()
        .future()
        .onSuccess(emp -> {
          employee.reply(Json.encode(emp));
          System.out.println("employee list");
        });

    });


    vertx.eventBus().consumer("delete", message -> {
      Promise<Response> responsePromise = Promise.promise();
      String id = ((String) message.body()).toString();
      employeeDao.delete(id);
      message.reply("deleted successfully");
    });
    vertx.eventBus().consumer("post", message -> {
      String emp= ((String) message.body());
      Response response = new Response();
      Employee employee = Json.decodeValue(emp, Employee.class);
      LOGGER.info(employee);
      employeeDao.save(employee)
          .future()
            .onSuccess(employees -> {
              response.setStatusCode(200).setResponseBody(Json.encode(employee));
      message.reply(response);

    })
        .onFailure(throwable -> {
          response.setStatusCode(400).setResponseBody(Utils.getErrorResponse("employee id not found").encode());
        });
          });


    vertx.eventBus().consumer("update", message -> {
      String messages = ((String) message.body());
      Employee employee = Json.decodeValue(messages, Employee.class);
      LOGGER.info(message);
      employeeDao.update(employee);
      message.reply(Json.encode(employee));
      message.reply("Employee updated successfully");

      });

  }
  }








