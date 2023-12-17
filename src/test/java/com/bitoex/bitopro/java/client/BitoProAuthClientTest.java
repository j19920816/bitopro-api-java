package com.bitoex.bitopro.java.client;
import com.bitoex.bitopro.java.model.Balance;
import com.bitoex.bitopro.java.model.DepositStatus;
import com.bitoex.bitopro.java.model.Order;
import com.bitoex.bitopro.java.model.OrderAction;

import com.bitoex.bitopro.java.model.OrderResponse;
import com.bitoex.bitopro.java.model.OrderType;
import com.bitoex.bitopro.java.model.PaginatedList;
import com.bitoex.bitopro.java.model.WithdrawProtocol;
import com.bitoex.bitopro.java.model.WithdrawStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BitoProAuthClientTest {

  private static final Logger logger = LogManager.getLogger();
  private static final String pair = "btc_usdt";
  private static BitoProAuthClient client;

  public static void main(String[] args) {
    try {
      client = new DefaultBitoProClient("", "", "");
      
      // get balance
      List<Balance> balances = client.getAccountBalances();
      logger.info("Balances: {}\r\n", balances);

      // create limit order
      BigDecimal price = new BigDecimal("20000");
      BigDecimal amount = new BigDecimal("0.0005");
      OrderResponse orderResponse = client.createLimitOrder(pair, OrderAction.BUY, price, amount);
      logger.info("Limit order: {}\r\n", orderResponse); 

      // get order
      Optional<Order> order = client.getOrder(pair, orderResponse.getOrderId());
      logger.info("Limit order: {}\r\n", order);

      // cancel order
      Optional<OrderResponse> cancelOrder = client.cancelOrder(pair,  orderResponse.getOrderId());
      logger.info("cancel order: {}\r\n", cancelOrder);

      // create batch orders
      List<Map<String, Object>> batchOrders = new ArrayList<>();
      batchOrders.add(Map.of("pair", pair, "action", OrderAction.BUY.name(), "amount", new BigDecimal("0.0004"), "price", new BigDecimal("10000"), "timestamp", System.currentTimeMillis(), "type", OrderType.LIMIT.name()));
      batchOrders.add(Map.of("pair", pair, "action", OrderAction.BUY.name(), "amount", new BigDecimal("0.0005"), "price", new BigDecimal("20000"), "timestamp", System.currentTimeMillis(), "type", OrderType.LIMIT.name()));
      Map<String, Object> createBatchOrders = client.createBatchOrder(batchOrders);
      logger.info("create batch orders: {}\r\n", createBatchOrders);

      // cancel batch order
      Map<String, List<String>> cancelOrdersMap  = new HashMap<String, List<String>>();
      cancelOrdersMap.put(pair, new ArrayList<String>());
      List<Map<String, String>> listObj = (ArrayList<Map<String, String>>)createBatchOrders.get("data");
      
      for(int i = 0; i < listObj.size(); i++)
        cancelOrdersMap.get(pair).add(listObj.get(i).get("orderId"));

      Map<String, Object> cancelBatchOrder = client.cancelMultipleOrders(cancelOrdersMap);
      logger.info("cancel batch orders: {}\r\n", cancelBatchOrder);

      // cancel all order
      Map<String, Object> cancelAllOrder = client.cancelAllOrder(pair);
      logger.info("cancel all order: {}\r\n", cancelAllOrder);

      // get orders
      LocalDateTime startDateTime = LocalDateTime.now().minusDays(10);
      LocalDateTime endDateTime = LocalDateTime.now().minusHours(1);
      PaginatedList<Order> getOrdes = client.getOrders(pair, Timestamp.valueOf(startDateTime).getTime(), Timestamp.valueOf(endDateTime).getTime(), false, null, null, "", 100, "");
      logger.info("get orders: {}\r\n", getOrdes);

      // get trades
      Map<String, Object> getTrades = client.getTradeList(pair, Timestamp.valueOf(startDateTime).getTime(), Timestamp.valueOf(endDateTime).getTime(), "", "", 100);
      logger.info("get trades: {}\r\n", getTrades);

      // get deposit history
      Map<String, Object> getDepositHistory = client.getDepositHistory("usdt", Timestamp.valueOf(startDateTime).getTime(), Timestamp.valueOf(endDateTime).getTime(), "", 100, DepositStatus.COMPLETE);
      logger.info("get deposit history: {}\r\n", getDepositHistory);

      // get withdraw history
      Map<String, Object> getWithdrawHistory = client.getWithdrawHistory("usdt", Timestamp.valueOf(startDateTime).getTime(), Timestamp.valueOf(endDateTime).getTime(), "", 100, WithdrawStatus.COMPLETE);
      logger.info("get withdraw history: {}\r\n", getWithdrawHistory);

      // get withdraw
      String serial =((ArrayList<Map<String, String>>)getWithdrawHistory.get("data")).get(0).get("serial");
      Map<String, Object> getWithdraw = client.getWithdraw("usdt", serial);
      logger.info("get withdraw: {}\r\n", getWithdraw);

      // withdraw
      amount = new BigDecimal("10");
      Map<String, Object> withdraw = client.withdraw("usdt", WithdrawProtocol.ERC20, "", amount, "");
      logger.info("withdraw: {}\r\n", withdraw);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}