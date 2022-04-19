package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.model.ResponseCodec;
import com.torryharris.employee.crud.util.ConfigKeys;
import com.torryharris.employee.crud.util.PropertyFileUtils;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApiServer extends AbstractVerticle {
  private static final Logger logger = LogManager.getLogger(ApiServer.class);
  private static Router router;
  private EventbusEmployee eventbusEmployee;
  private Employee employees;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    eventbusEmployee = new EventbusEmployee(vertx);
    router = Router.router(vertx);
    EventBus eventBus = getVertx().eventBus();
    eventBus.registerDefaultCodec(Response.class,new ResponseCodec());
    // Attach a BodyHandler to parse request body and set upload to false
    router.route().handler(BodyHandler.create(false));

    router.get("/employees/:id")
      .handler(routingContext -> {
          String id = routingContext.request().getParam("id");
          vertx.eventBus().request("id", id, reply -> {
            System.out.println("employee details with id:" + id);
            System.out.println(reply.result().body());
            routingContext.response().end(reply.result().body().toString());
          });
        }
      );
    router.delete("/employees/:id")
      .handler(routingContext -> {
          String id = routingContext.request().getParam("id");
          vertx.eventBus().request("delete", id, reply -> {
            System.out.println("employee details with id:" + id);
            System.out.println(reply.result().body());
            routingContext.response()
              .end(reply.result().body().toString());
          });
        }
      );

    router.get("/employees").consumes("*/json")
      .handler(routingContext -> {
        vertx.eventBus().request("get", null, reply -> {
          System.out.println("employee list");
          System.out.println(reply.result().body());
          routingContext.response().end(reply.result().body().toString());
        });
      });




    router.put("/employees/:id")
      .handler(routingContext -> {
        Employee employee=Json.decodeValue(routingContext.getBody(),Employee.class);
          System.out.println(employee);
          vertx.eventBus().request("update",(Json.encode(employee)), reply -> {
            if (reply.succeeded()) {
              Response response = (Response) reply.result().body();
              System.out.println(reply.result().body());
              HttpServerResponse serverResponse = routingContext.response();
              serverResponse.putHeader("content-type", "application/json")
              .end(reply.result().body().toString());
            }
            else {
              System.out.println("failed");
              HttpServerResponse serverResponse = routingContext.response();
              ReplyException exception = (ReplyException) reply.cause();
              serverResponse.setStatusCode(exception.failureCode());
              serverResponse.putHeader("content-type", "application/json")
              .end(Utils.getErrorResponse(exception.getMessage()).encode());

            }
          });
      });
    router.delete("/employees/:id")
      .handler(routingContext -> {
        Employee employee=Json.decodeValue(routingContext.getBody(),Employee.class);
        System.out.println(employee);
        vertx.eventBus().request("delete",(Json.encode(employee)), reply -> {
          if (reply.succeeded()) {
            Response response = (Response) reply.result().body();
            System.out.println(reply.result().body());
            HttpServerResponse serverResponse = routingContext.response();
            serverResponse.putHeader("content-type", "application/json")
              .end(reply.result().body().toString());
          }
          else {
            System.out.println("failed");
            HttpServerResponse serverResponse = routingContext.response();
            ReplyException exception = (ReplyException) reply.cause();
            serverResponse.setStatusCode(exception.failureCode());
            serverResponse.putHeader("content-type", "application/json")
              .end(Utils.getErrorResponse(exception.getMessage()).encode());

          }
        });
      });

    router.post("/employees").consumes("*/json")
      .handler(routingContext -> {
        Employee employee=Json.decodeValue(routingContext.getBody(),Employee.class);

        vertx.eventBus().request("post",Json.encode(employee), reply -> {
          if (reply.succeeded()) {
            logger.info(reply.result().body());
            Response response = (Response) reply.result().body();
            HttpServerResponse serverResponse = routingContext.response();
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.put("message","employee added successfully");
            serverResponse.putHeader("content-type", "application/json")
              .end((response.getResponseBody()));

          }
          else {
            HttpServerResponse serverResponse = routingContext.response();
            ReplyException exception = (ReplyException) reply.cause();
            serverResponse.setStatusCode(exception.failureCode());
            serverResponse.putHeader("content-type", "application/json")
              .end(Utils.getErrorResponse(exception.getMessage()).encode());
          }
        });
      });



    router.get("/response/:id").handler(routingContext -> {
      String id = routingContext.request().getParam("id");

      vertx.eventBus().request("id", id, reply -> {
        if (reply.succeeded()) {

          Response response = (Response) reply.result().body();
          HttpServerResponse serverResponse = routingContext.response();
          serverResponse.putHeader("content-type","application/json");
          serverResponse.end(response.getResponseBody());
          };

      });
    });



    HttpServerOptions options = new HttpServerOptions().setTcpKeepAlive(true);
  vertx.createHttpServer(options)
    .exceptionHandler(logger::catching)
    .requestHandler(router)
    .listen(Integer.parseInt(PropertyFileUtils.getProperty(ConfigKeys.HTTP_SERVER_PORT)))
    .onSuccess(httpServer -> {
    logger.info("Server started on port {}", httpServer.actualPort());
    startPromise.tryComplete();
  })
    .onFailure(startPromise::tryFail);
}
}


