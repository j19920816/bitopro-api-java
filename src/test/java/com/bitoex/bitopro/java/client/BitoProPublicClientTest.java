package com.bitoex.bitopro.java.client;
import com.bitoex.bitopro.java.model.OrderBook;
import com.bitoex.bitopro.java.model.Resolution;
import com.bitoex.bitopro.java.model.Ticker;
import com.bitoex.bitopro.java.model.Trade;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BitoProPublicClientTest {

  private static final Logger logger = LogManager.getLogger();
  private static final String pair = "btc_twd";
  private static BitoProPublicClient client;

  public static void main(String[] args) {
    try {
      client = BitoProClientBuilder.withDefaultClient().createPublic();
      
      OrderBook ob = client.getOrderBook(pair);
      logger.info("order book: {}\r\n", ob);

      Ticker ticker = client.getTicker(pair);
      logger.info("{} ticker: {}\r\n", pair, ticker);

      List<Trade> trades = client.getTrades(pair);
      logger.info("trades: {}\r\n", trades);

      Map<String, Object> currencyInfo = client.getCurrencies();
      logger.info("currencyInfo: {}\r\n", currencyInfo);

      Map<String, Object> limitationsFees = client.getLimitationsFees();
      logger.info("limitations and Fees: {}\r\n", limitationsFees);

      Map<String, Object> candleSticks = client.getCandlestick(pair, Resolution._1d, 1650707415, 1678355415);
      logger.info("Candle Sticks: {}\r\n", candleSticks);

      Map<String, Object> tradingPairs = client.getTradingPairs();
      logger.info("Trading pairs: {}\r\n", tradingPairs);

    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}