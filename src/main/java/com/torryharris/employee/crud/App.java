package com.torryharris.employee.crud;

import com.torryharris.employee.crud.verticles.ApiServer;
import com.torryharris.employee.crud.verticles.EventbusEmployee;
import com.torryharris.employee.crud.verticles.VerticleWorker;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
  private static final Logger LOGGER = LogManager.getLogger(App.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(ApiServer.class.getName());
    vertx.deployVerticle(new EventbusEmployee(vertx));
    DeploymentOptions deploymentOptions=new DeploymentOptions().setWorker(true);
    vertx.deployVerticle(new VerticleWorker(vertx),deploymentOptions)
      .onFailure(throwable -> LOGGER.error("Error while starting API server", throwable));


  }

}
