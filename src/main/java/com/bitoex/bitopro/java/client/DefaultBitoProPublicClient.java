package com.bitoex.bitopro.java.client;

import static com.bitoex.bitopro.java.util.BitoProUtils.validatePair;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import com.bitoex.bitopro.java.model.OrderBook;
import com.bitoex.bitopro.java.model.Resolution;
import com.bitoex.bitopro.java.model.ResponseWrapper;
import com.bitoex.bitopro.java.model.Ticker;
import com.bitoex.bitopro.java.model.Trade;
import com.bitoex.bitopro.java.util.BitoProUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation for {@link BitoProPublicClient} using HttpClient.
 */
public class DefaultBitoProPublicClient extends AbstractBitoProClient implements BitoProPublicClient {

    public DefaultBitoProPublicClient(CloseableHttpClient client) {
        super(client);
    }

    public DefaultBitoProPublicClient() {
        this(BitoProUtils.createDefaultClient());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticker getTicker(String pair) throws IOException {
        validatePair(pair);
        String url = getUrl("tickers/" + pair);
        HttpGet get = createGet(url);

        try (CloseableHttpResponse resp = client.execute(get)) {

            checkStatus(resp.getStatusLine(), resp.getEntity());
            ResponseWrapper<Ticker> wrap = om.readValue(resp.getEntity().getContent(),
                    new TypeReference<ResponseWrapper<Ticker>>() {});
            return wrap.getData();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderBook getOrderBook(String pair) throws IOException {
        validatePair(pair);
        String url = getUrl("order-book/" + pair);
        HttpGet get = createGet(url);

        try (CloseableHttpResponse resp = client.execute(get)) {

            checkStatus(resp.getStatusLine(), resp.getEntity());
            return om.readValue(resp.getEntity().getContent(), OrderBook.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Trade> getTrades(String pair) throws IOException {
        validatePair(pair);
        String url = getUrl("trades/" + pair);
        HttpGet get = createGet(url);

        try (CloseableHttpResponse resp = client.execute(get)) {

            checkStatus(resp.getStatusLine(), resp.getEntity());
            ResponseWrapper<List<Trade>> wrap = om.readValue(resp.getEntity().getContent(),
                    new TypeReference<ResponseWrapper<List<Trade>>>() {});
            return wrap.getData();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getCurrencies() throws IOException {
        String url = getUrl("provisioning/currencies");
        HttpGet get = createGet(url);

        Map<String, Object> mapping;
        try (CloseableHttpResponse resp = client.execute(get)) {
            checkStatus(resp.getStatusLine(), resp.getEntity());
            String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
            mapping = new ObjectMapper().readValue(content, new TypeReference<Map<String, Object>>(){});
        }
         return mapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getLimitationsFees() throws IOException {
        String url = getUrl("provisioning/limitations-and-fees" );
        HttpGet get = createGet(url);

        Map<String, Object> mapping;
        try (CloseableHttpResponse resp = client.execute(get)) {
            checkStatus(resp.getStatusLine(), resp.getEntity());
            String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
            mapping = new ObjectMapper().readValue(content, new TypeReference<Map<String, Object>>(){});
        }
         return mapping;
    }

    @Override
    public Map<String, Object> getCandlestick(String pair, Resolution resolution, long startDateTimestamp,  long endDateTimestamp) throws IOException {
        String url = getUrl("trading-history/" +  pair);
        HttpGet httpGet = createGet(url);
        
        String appendString = "";
        appendString += "resolution=" + resolution.name().replace("_", "") + "&";
        appendString += "from=" + startDateTimestamp + "&";
        appendString += "to=" +  endDateTimestamp;
           
        try {
            URI appendUri =  BitoProUtils.appendUri(url, appendString);
            httpGet.setURI(appendUri);
          }
        catch(Exception e) {
            System.err.println(e.getMessage());
        };


        Map<String, Object> mapping;
        try (CloseableHttpResponse resp = client.execute(httpGet)) {
            checkStatus(resp.getStatusLine(), resp.getEntity());
            String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
            mapping = new ObjectMapper().readValue(content, new TypeReference<Map<String, Object>>(){});
        }
        return mapping;
    }

    @Override
    public Map<String, Object> getTradingPairs() throws IOException {
        String url = getUrl("provisioning/trading-pairs" );
        HttpGet get = createGet(url);

        Map<String, Object> mapping;
        try (CloseableHttpResponse resp = client.execute(get)) {
            checkStatus(resp.getStatusLine(), resp.getEntity());
            String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
            mapping = new ObjectMapper().readValue(content, new TypeReference<Map<String, Object>>(){});
        }
         return mapping;
    }
}
