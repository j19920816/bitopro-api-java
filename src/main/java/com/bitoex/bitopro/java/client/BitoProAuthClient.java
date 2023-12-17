package com.bitoex.bitopro.java.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bitoex.bitopro.java.model.Balance;
import com.bitoex.bitopro.java.model.DepositStatus;
import com.bitoex.bitopro.java.model.Order;
import com.bitoex.bitopro.java.model.OrderAction;
import com.bitoex.bitopro.java.model.OrderResponse;
import com.bitoex.bitopro.java.model.OrderStatus;
import com.bitoex.bitopro.java.model.PaginatedList;
import com.bitoex.bitopro.java.model.StatusKind;
import com.bitoex.bitopro.java.model.WithdrawProtocol;
import com.bitoex.bitopro.java.model.WithdrawStatus;

/**
 * Client for BitoPro API. Provides direct methods to access BitoPro API.
 * Default implementation can be created via {@link BitoProClientBuilder}. 
 * All methods throw {@link com.bitoex.bitopro.java.exception.ApiException} when
 * server returns error. All parameter related errors will be thrown with
 * {@link com.bitoex.bitopro.java.exception.BadArgumentException}.
 * 
 * Pair is in terms of base and quote and combined in the form of @{code ${base}_${quote}}.
 */
public interface BitoProAuthClient {

  /**
   * Get account's balances.
   * 
   * @return list of balances for each symbol
   * @throws IOException when connection error occured while called the Rest API
   */
  List<Balance> getAccountBalances() throws IOException;

  /**
   * Place a market order. If action is BUY, totalAmount is in terms of quote. If
   * action is SELL, totalAmount is in terms of base.
   *
   * @param pair        the pair to place order
   * @param action      {@link OrderAction} for the order, either BUY or SELL
   * @param totalAmount total amount for the order
   * @return {@link OrderResponse} with result of the order placed
   * @throws IOException when connection error occured while called the Rest API
   */
  OrderResponse createMarketOrder(String pair, OrderAction action, BigDecimal totalAmount) throws IOException;

  /**
   * Place a limit price order.
   * 
   * @param pair   the pair to place order
   * @param action {@link OrderAction} for the order, either BUY or SELL
   * @param price  price in terms of quote for the order
   * @param amount amount in terms of quote for the order
   * @return {@link OrderResponse} with result of the order placed
   * @throws IOException when connection error occured while called the Rest API
   */
  OrderResponse createLimitOrder(String pair, OrderAction action, BigDecimal price, BigDecimal amount)
      throws IOException;

  /**
   * Cancel an order
   * {@code com.bitoex.bitopro.java.exception.BadArgumentException} is thrown.
   * 
   * @param pair the pair to cancel order
   * @param orderId id of the order to be cancelled
   * @return {@link OrderResponse} with status of the order cancelled, if present
   * @throws IOException when connection error occured while called the Rest API
   */
  Optional<OrderResponse> cancelOrder(String pair, String orderId) throws IOException;

  /**
   * Cancel all pair order
   * {@code com.bitoex.bitopro.java.exception.BadArgumentException} is thrown.
   * 
   * @param pair the pair to cancel orders
   * @return {@link OrderResponse} with status of the order cancelled, if present
   * @throws IOException when connection error occured while called the Rest API
   */
  Map<String, Object> cancelAllOrder(String pair) throws IOException;

   /**
   * Cancel multiple orders
   * {@code com.bitoex.bitopro.java.exception.BadArgumentException} is thrown.
   * 
   * @param ordersRequest multiple orders will be canceled, send a json format request to cancel multiple orders at a time.
   * @return {@link Map<String, Object>} with status of the order cancelled, if present
   * @throws IOException when connection error occured while called the Rest API
   */
  Map<String, Object> cancelMultipleOrders(Map<String, List<String>> ordersRequest) throws IOException;

  /**
   * multiple orders will be created
   * 
   * @return list of created orders
   * @throws IOException when connection error occured while called the Rest API
   */
  Map<String, Object> createBatchOrder(List<Map<String,Object>> createRequest) throws IOException;


  /**
   * Get orders for specified pair in paginated form.
   * https://github.com/bitoex/bitopro-offical-api-docs/blob/master/api/v3/private/get_orders_data.md
   * @param pair   the pair to get orders
   * @param startTimestamp: start time in unix timestamp.
   * @param endTimestamp: end time in unix timestamp.
   * @param statusKind: filter order based on status kind, OPEN, DONE, ALL.
   * @param status: filter order base on specific status.
   * @param orderId: if specified, list starts with order with id >= orderId.
   * @param limit: the number of records to retrieve.
   * @param clientId: this information help users distinguish their orders.
   * @return the paginated list of orders
   * @throws IOException when connection error occured while called the Rest API
   * @see Order
   */
  PaginatedList<Order> getOrders(String pair, long startTimestamp, long endTimestamp, boolean ignoreTimeLimitEnable, StatusKind statusKind, OrderStatus status, String order_id, int limit, String ClientId) throws IOException;

  /**
   * Get order specified by pair and id.
   * 
   * @param pair    the pair to get order
   * @param orderId id of the order
   * @return specified order
   * @throws IOException when connection error occured while called the Rest API
   */
  Optional<Order> getOrder(String pair, String orderId) throws IOException;

  /**
  * https://github.com/bitoex/bitopro-offical-api-docs/blob/master/api/v3/private/get_trades_data.md
  * 
  * @param pair the trading pair format
  * @param startTimestamp: start time in unix timestamp.
  * @param endTimestamp: end time in unix timestamp.
  * @param orderId: the id of order
  * @param trade_id: the id of first trade in the response
  * @param limit: the number of records to retrieve.
  * @return the list of orders
  * @throws IOException when connection error occured while called the Rest API
  */
  Map<String, Object> getTradeList(String pair, long startTimestamp, long endTimestamp, String orderId, String tradeId, int limit) throws IOException;

  /**
  * https://github.com/bitoex/bitopro-offical-api-docs/blob/master/api/v3/private/get_deposit_invoices_data.md
  *
  * @param currency the trading pair format
  * @param startTimestamp: start time in unix timestamp
  * @param endTimestamp: end time in unix timestamp
  * @param id: the id of the first data in the response
  * @param limit: the limit for the response
  * @param depositStatus: the status of the deposit.	
  * @return the deposit history information
  * @throws IOException when connection error occured while called the Rest API
  */
  Map<String, Object> getDepositHistory(String currency, long startTimestamp, long endTimestamp, String id, int limit, DepositStatus depositStatus) throws IOException;

  /**
  * https://github.com/bitoex/bitopro-offical-api-docs/blob/master/api/v3/private/get_withdraw_invoices_data.md
  *
  * @param currency the currency of the withdraw to get.
  * @param startTimestamp: start time in unix timestamp
  * @param endTimestamp: end time in unix timestamp
  * @param id: the id of the first data in the response
  * @param limit: the limit for the response
  * @param withdrawitStatus: the status of the withdraw.	
  * @return the withdraw history information
  * @throws IOException when connection error occured while called the Rest API
  */
  Map<String, Object> getWithdrawHistory(String currency, long startTimestamp, long endTimestamp, String id, int limit, WithdrawStatus withdrawitStatus) throws IOException;

  /**
  * https://github.com/bitoex/bitopro-offical-api-docs/blob/master/api/v3/private/get_an_withdraw_invoice_data.md
  *
  * @param currency the currency of the withdraw to get.
  * @param serial: the serial of the withdraw.
  * @return the withdraw information
  * @throws IOException when connection error occured while called the Rest API
  */
  Map<String, Object> getWithdraw(String currency, String serial) throws IOException;

  /**
  * https://github.com/bitoex/bitopro-offical-api-docs/blob/master/api/v3/private/create_an_withdraw_invoice.md
  *
  * @param currency the currency of the withdraw to get.
  * @param protocol: the protocol to send.
  * @param address: the address or bank account to send fund.
  * @param amount: the amount of fund to send.
  * @param message: the message or note to be attached with withdraw.
  * @return the withdraw information
  * @throws IOException when connection error occured while called the Rest API
  */
  Map<String, Object> withdraw(String currency, WithdrawProtocol withdrawProtocol, String address, BigDecimal amount, String message) throws IOException;
}
