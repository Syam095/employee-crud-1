package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.dao.Dao;
import com.torryharris.employee.crud.dao.impl.EmployeeJdbcDao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
public class VerticleWorker  extends AbstractVerticle {

  private static final Logger LOGGER = LogManager.getLogger(VerticleWorker.class);
  private final Dao<Employee> employeeDao;
  public VerticleWorker(Vertx vertx) {
    employeeDao = new EmployeeJdbcDao(vertx);
  }

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("getc", message -> {
LOGGER.info("hiigg");
      String id = (String) message.body();
      Utils.get(Long.valueOf(id));
    });
  }
}
