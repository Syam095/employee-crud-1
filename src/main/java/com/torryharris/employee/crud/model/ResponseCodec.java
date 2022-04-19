package com.torryharris.employee.crud.model;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class ResponseCodec implements MessageCodec<Response, Response> {



  @Override
  public void encodeToWire(Buffer buffer, Response response) {
    JsonObject jsonToEncode = new JsonObject();
    jsonToEncode.put("statuscode",response.getStatusCode());
    jsonToEncode.put("responsebody",response.getResponseBody());
    jsonToEncode.put("Headers", response.getHeaders());

   String JsonTostring = jsonToEncode.encode();
    int length = JsonTostring.getBytes().length;
    buffer.appendInt(length);
    buffer.appendString(JsonTostring);



  }

  @Override
  public Response decodeFromWire(int pos, Buffer buffer) {

    String jsonStr = buffer.getString(pos+=4, pos+= buffer.length());
    JsonObject contentJson = new JsonObject(jsonStr);
    int statusCode = contentJson.getInteger("statuscode");
    String responsebody = contentJson.getString("responsebody");
   JsonObject headers = contentJson.getJsonObject("headers");
    Response response = new Response();
    return response ;
  }

  @Override
  public Response transform(Response response) {
    return response;
  }

  @Override
  public String name() {
    return this.getClass().getName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
