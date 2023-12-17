package com.bitoex.bitopro.java.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.bitoex.bitopro.java.model.OrderBook;
import com.bitoex.bitopro.java.model.Resolution;
import com.bitoex.bitopro.java.model.Ticker;
import com.bitoex.bitopro.java.model.Trade;
/**
 * Client for BitoPro Public API. Provides direct methods to access BitoPro
 * Public API. Default implementation can be created via {@link BitoProClientBuilder}.
 * 
 * All methods throw {@link com.bitoex.bitopro.java.exception.ApiException} when
 * server returns error. All parameter related errors will be thrown with
 * {@link com.bitoex.bitopro.java.exception.BadArgumentException}.
 * 
 * Pair is in terms of base and quote and combined in the form of @{code
 * ${base}_${quote}}.
 */
public interface BitoProPublicClient {
    /**
     * Get ticker for specific pair.
     * 
     * @param pair the pair to get ticker
     * @return ticker for the pair
     * @throws IOException when connection error occured while called the Rest API
     */
    Ticker getTicker(String pair) throws IOException;

    /**
     * Get order book for specific pair.
     * 
     * @param pair the pair to get order book
     * @return order book for the pair
     * @throws IOException when connection error occured while called the Rest API
     */
    OrderBook getOrderBook(String pair) throws IOException;

    /**
     * Get recent trades for specific pair.
     * 
     * @param pair the pair to get trades
     * @return trades for the pair
     * @throws IOException when connection error occured while called the Rest API
     */
    List<Trade> getTrades(String pair) throws IOException;

    /**
     * Get recent currencies.
     * 
     * @return the list of currencies
     * @throws IOException when connection error occured while called the Rest API
     */
    Map<String, Object> getCurrencies() throws IOException;

     /**
     * Get recent limitations and fees
     * 
     * @return the limitations and fees
     * @throws IOException when connection error occured while called the Rest API
     */
    
    Map<String, Object>  getCandlestick(String pair, Resolution resolution, long startDateTimestamp,  long endDateTimestamp) throws IOException;

    /**
     * Get list of pairs available for trade
     * 
     * @return a list of pairs available for trade
     * @throws IOException when connection error occured while called the Rest API
     */
    
    Map<String, Object>  getTradingPairs() throws IOException;

    /**
     * Get open, high, low, close data in a period
     * 
     * @return the open, high, low, close data in a period
     * @throws IOException when connection error occured while called the Rest API
     */
    
    Map<String, Object>  getLimitationsFees() throws IOException;

}
