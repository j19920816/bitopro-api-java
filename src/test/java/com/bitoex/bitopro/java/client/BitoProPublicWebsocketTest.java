package com.bitoex.bitopro.java.client;
import java.util.List;
import java.util.Map;
import com.bitoex.bitopro.java.client.websocketImpl.OrderBookWebsocket;
import com.bitoex.bitopro.java.client.websocketImpl.TickerkWebsocket;
import com.bitoex.bitopro.java.client.websocketImpl.TradesWebsocket;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BitoProPublicWebsocketTest {
  public static void main(String[] args) {
    try {
      final ObjectMapper om = new ObjectMapper();
      WebsocketCallback clientCallback = new WebsocketCallback(){
        @Override
        public void processResponse(String message) {
          try{
            Map<String, Object> jsonMessage = om.readValue(message, new TypeReference<Map<String, Object>>(){});
            if(jsonMessage.get("event").equals("ORDER_BOOK"))
              System.out.println("ORDER_BOOK: " + jsonMessage);
            else if(jsonMessage.get("event").equals("TICKER"))
              System.out.println("TICKER: " + jsonMessage);
            else if(jsonMessage.get("event").equals("TRADE"))
              System.out.println("TRADE: " + jsonMessage);
          }
          catch(Exception e) {
            System.err.println(e.getMessage());
          };
        }};

      Map<String,Integer> pairsLimit =  Map.of("eth_btc", 5, "BTC_TWD", 1, "ETH_TWD", 20, "BITO_ETH", 1);
      List<String> pairs = List.of("BTC_TWD", "ETH_TWD", "BITO_ETH");
      
      // [PUBLIC] GET Order book
      OrderBookWebsocket obWebsocket = new OrderBookWebsocket(AbstractBitoProWebsocket.getPairsLimitString(pairsLimit), clientCallback);
      obWebsocket.connect();
      
      // [PUBLIC] GET Ticket
      TickerkWebsocket tkWebsocket = new TickerkWebsocket(AbstractBitoProWebsocket.getPairsString(pairs), clientCallback);
      tkWebsocket.connect();
      
      // [PUBLIC] GET Trade
      TradesWebsocket tdWebsocket = new TradesWebsocket(AbstractBitoProWebsocket.getPairsString(pairs), clientCallback);
      tdWebsocket.connect();

    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
