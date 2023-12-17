package com.bitoex.bitopro.java.client;
import java.util.Map;
import com.bitoex.bitopro.java.client.websocketImpl.UserBlanceWebsocket;
import com.bitoex.bitopro.java.client.websocketImpl.UserHistoryOrdersWebsocket;
import com.bitoex.bitopro.java.client.websocketImpl.UserOrdersWebsocket;
import com.bitoex.bitopro.java.client.websocketImpl.UserTradeWebsocket;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BitoProAuthWebsocketTest {
    public static void main(String[] args) {
    try {
      final ObjectMapper om = new ObjectMapper();
      WebsocketCallback clientCallback = new WebsocketCallback(){
        @Override
        public void processResponse(String message) {
          try{
            Map<String, Object> jsonMessage = om.readValue(message, new TypeReference<Map<String, Object>>(){});
            if(jsonMessage.get("event").equals("ACCOUNT_BALANCE"))
              System.out.println("ACCOUNT_BALANCE: " + jsonMessage);
            else if(jsonMessage.get("event").equals("ACTIVE_ORDERS"))
              System.out.println("ACTIVE_ORDERS: " + jsonMessage);
            else if(jsonMessage.get("event").equals("USER_TRADE"))
              System.out.println("USER_TRADE: " + jsonMessage);
            else if(jsonMessage.get("event").equals("RECENT_HISTORY_ORDERS"))
              System.out.println("RECENT_HISTORY_ORDERS: " + jsonMessage);
          }
          catch(Exception e) {
            System.err.println(e.getMessage());
          };
        }};

      String account="";
      String apiKey="";
      String apiSecret="";
      
      // [Private] GET active orders
      UserOrdersWebsocket uoWebsocket = new UserOrdersWebsocket(apiSecret, account, apiKey, apiSecret, clientCallback);
      uoWebsocket.initWebsocket();
      uoWebsocket.connect();
      
      // [Private] GET account balance
      UserBlanceWebsocket ubWebsocket = new UserBlanceWebsocket(apiSecret, account, apiKey, apiSecret, clientCallback);
      ubWebsocket.initWebsocket();
      ubWebsocket.connect();

      // [Private] GET user trade
      UserTradeWebsocket utWebsocket = new UserTradeWebsocket(apiSecret, account, apiKey, apiSecret, clientCallback);
      utWebsocket.initWebsocket();
      utWebsocket.connect();

      // [Private] GET history orders
      UserHistoryOrdersWebsocket uhWebsocket = new UserHistoryOrdersWebsocket(apiSecret, account, apiKey, apiSecret, clientCallback);
      uhWebsocket.initWebsocket();
      uhWebsocket.connect();

    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
