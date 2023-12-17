package com.bitoex.bitopro.java.client;

import static com.bitoex.bitopro.java.util.BitoProUtils.validatePair;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bitoex.bitopro.java.exception.BadArgumentException;
import com.bitoex.bitopro.java.model.Balance;
import com.bitoex.bitopro.java.model.DepositStatus;
import com.bitoex.bitopro.java.model.Order;
import com.bitoex.bitopro.java.model.OrderAction;
import com.bitoex.bitopro.java.model.OrderRequest;
import com.bitoex.bitopro.java.model.OrderResponse;
import com.bitoex.bitopro.java.model.OrderStatus;
import com.bitoex.bitopro.java.model.OrderType;
import com.bitoex.bitopro.java.model.PaginatedList;
import com.bitoex.bitopro.java.model.ResponseWrapper;
import com.bitoex.bitopro.java.model.StatusKind;
import com.bitoex.bitopro.java.model.WithdrawProtocol;
import com.bitoex.bitopro.java.model.WithdrawStatus;
import com.bitoex.bitopro.java.signature.Signature;
import com.bitoex.bitopro.java.util.BitoProUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for {@link BitoProAuthClient} using HttpClient.
 */
public class DefaultBitoProClient extends AbstractBitoProClient implements BitoProAuthClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractBitoProClient.class);
    private final Header apiKeyHeader;
    private final Signature signature;

    public DefaultBitoProClient(String apiKey, String secret, String email, CloseableHttpClient client) {
        super(client);

        this.apiKeyHeader = new BasicHeader("X-BITOPRO-APIKEY", apiKey);
        this.signature = new Signature(email, secret);
    }

    public DefaultBitoProClient(String apiKey, String secret, String email) {
        this(apiKey, secret, email, BitoProUtils.createDefaultClient());
    }

    @Override
    public List<Balance> getAccountBalances() throws IOException {
        ResponseWrapper<List<Balance>> wrapper = executeRequest("/accounts/balance", HttpGet.METHOD_NAME,
                Optional.empty(), new TypeReference<ResponseWrapper<List<Balance>>>() {});
        return wrapper.getData();
    }

    @Override
    public OrderResponse createMarketOrder(String pair, OrderAction action, BigDecimal totalAmount) throws IOException {

        validatePair(pair);
        OrderRequest or = new OrderRequest();

        or.setType(OrderType.MARKET);
        or.setAction(action);
        or.setPrice(BigDecimal.ONE);
        or.setAmount(totalAmount);
        or.setTimestamp(System.currentTimeMillis());
        return executeRequest("/orders/" + pair, HttpPost.METHOD_NAME, Optional.of(or), OrderResponse.class);
    }

    @Override
    public OrderResponse createLimitOrder(String pair, OrderAction action, BigDecimal price, BigDecimal amount) throws IOException {

        validatePair(pair);
        OrderRequest or = new OrderRequest();
        or.setType(OrderType.LIMIT);
        or.setAction(action);
        or.setPrice(price);
        or.setAmount(amount);
        or.setTimestamp(System.currentTimeMillis());
        return executeRequest("/orders/" + pair, HttpPost.METHOD_NAME, Optional.of(or), OrderResponse.class);
    }

    @Override
    public Optional<OrderResponse> cancelOrder(String pair, String orderId) throws IOException {
        String parameteString;
        try {
            parameteString = "/orders/" + pair + "/" + orderId;

            return Optional.of(executeRequest(parameteString, HttpDelete.METHOD_NAME, Optional.empty(), OrderResponse.class));
        } catch(BadArgumentException e) {
            log.debug("failed to cancel order: ", orderId, e);
            return Optional.empty();
        }
    }

    @Override
    public Map<String, Object> cancelAllOrder(String pair) throws IOException
    {
        String parameteString;
        try {
            parameteString = "/orders/" + pair;
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("identity", "");
            parameterMap.put("nonce", System.currentTimeMillis());
            return executeRequest(parameteString, HttpDelete.METHOD_NAME, Optional.of(parameterMap), new TypeReference<Map<String, Object>>(){});
        } catch(BadArgumentException e) {
            log.debug("failed to cancel orders: ", e);
            return null;
        }
    }

    @Override
    public PaginatedList<Order> getOrders(String pair, long startTimestamp, long endTimestamp, boolean ignoreTimeLimitEnable, StatusKind statusKind, OrderStatus status, String orderId, int limit, String ClientId) throws IOException {
        validatePair(pair);

        String appendString = "";
        appendString += "startTimestamp=" + startTimestamp + "&";
        appendString += "endTimestamp=" + endTimestamp + "&";
        appendString += "ignoreTimeLimitEnable=" + ignoreTimeLimitEnable + "&";
        if(statusKind!=null)
            appendString += "statusKind=" + statusKind.name() + "&";
        if(status!=null)
            appendString += "status=" + status.ordinal() + "&";
        if(orderId!="")
            appendString += "orderId=" + orderId + "&";
        if(ClientId!="")
            appendString += "clientId=" + ClientId + "&";
        appendString += "limit=" + limit;

        PaginatedList<Order> list = executeRequest("/orders/all/" + pair + "?" + appendString, HttpGet.METHOD_NAME, Optional.empty(), new TypeReference<PaginatedList<Order>>(){});
        return list;
    }

    /*
     * createRequest parameter example:
    [
        {
            pair: "BTC_TWD",
            action: "BUY",
            type: "LIMIT",
            price: "210000",
            amount: "1",
            timestamp: 1504262258000,
            timeInForce: "GTC",
        }, 
        {
            pair: "BTC_TWD",
            action: "SELL",
            type: "MARKET",
            amount: "2",
            timestamp: 1504262258000
        }
    ]
     */ 
    @Override
    public Map<String, Object> createBatchOrder(List<Map<String,Object>> createRequest) throws IOException{
        return executeRequest("/orders/batch", HttpPost.METHOD_NAME, Optional.of(createRequest), new TypeReference<Map<String, Object>>(){});
    }

     /*
     * ordersRequest parameter example: {"BTC_USDT": ["12234566","12234567"],"ETH_USDT": ["44566712","24552212"]}
     */ 
    @Override
    public Map<String, Object> cancelMultipleOrders(Map<String, List<String>> ordersRequest) throws IOException{
        return executeRequest("/orders", HttpPut.METHOD_NAME, Optional.of(ordersRequest), new TypeReference<Map<String, Object>>(){});
    }

    @Override
    public Optional<Order> getOrder(String pair, String id) throws IOException {
        validatePair(pair);
        Order order = executeRequest("orders/" + pair + "/" + id, HttpGet.METHOD_NAME, Optional.empty(), Order.class);
        if (StringUtils.isBlank(order.getId())) {
            return Optional.empty();
        } else {
            return Optional.of(order);
        }
    }

    @Override
    public Map<String, Object> getTradeList(String pair, long startTimestamp, long endTimestamp, String orderId, String tradeId, int limit) throws IOException{
        validatePair(pair);

        String appendString = "";
        appendString += "startTimestamp=" + startTimestamp + "&";
        appendString += "endTimestamp=" + endTimestamp + "&";
        if(orderId!="")
            appendString += "orderId=" + orderId + "&";
        if(tradeId!="")
            appendString += "tradeId=" + tradeId + "&";
        appendString += "limit=" + limit;

        return executeRequest("/orders/trades/" + pair + "?" + appendString, HttpGet.METHOD_NAME, Optional.empty(), new TypeReference<Map<String, Object>>(){});
    }
    
    @Override
    public Map<String, Object> getDepositHistory(String currency, long startTimestamp, long endTimestamp, String id, int limit, DepositStatus depositStatus) throws IOException{
        String appendString = "";
        appendString += "startTimestamp=" + startTimestamp + "&";
        appendString += "endTimestamp=" + endTimestamp + "&";
        if(id!="")
            appendString += "id=" + id + "&";
        if(depositStatus!=null)
            appendString += "statuses=" + depositStatus.name() + "&";
        appendString += "limit=" + limit;

        return executeRequest("/wallet/depositHistory/" + currency + "?" + appendString, HttpGet.METHOD_NAME, Optional.empty(), new TypeReference<Map<String, Object>>(){});
    }

    @Override
    public Map<String, Object> getWithdrawHistory(String currency, long startTimestamp, long endTimestamp, String id, int limit, WithdrawStatus withdrawitStatus) throws IOException{
        String appendString = "";
        appendString += "startTimestamp=" + startTimestamp + "&";
        appendString += "endTimestamp=" + endTimestamp + "&";
        if(id!="")
            appendString += "id=" + id + "&";
        if(withdrawitStatus!=null)
            appendString += "statuses=" + withdrawitStatus.name() + "&";
        appendString += "limit=" + limit;

        return executeRequest("/wallet/withdrawHistory/" + currency + "?" + appendString, HttpGet.METHOD_NAME, Optional.empty(), new TypeReference<Map<String, Object>>(){});
    }

    @Override
    public Map<String, Object> getWithdraw(String currency, String serial) throws IOException{
        return executeRequest("/wallet/withdraw/" + currency + "/" + serial, HttpGet.METHOD_NAME, Optional.empty(), new TypeReference<Map<String, Object>>(){});
    }

    @Override
    public Map<String, Object> withdraw(String currency, WithdrawProtocol withdrawProtocol, String address, BigDecimal amount, String message) throws IOException{
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("protocol", withdrawProtocol.name());
        parameterMap.put("address", address);
        parameterMap.put("amount", amount);
        parameterMap.put("timestamp", System.currentTimeMillis());
        if(message != "")
            parameterMap.put("message", message);
        return executeRequest("/wallet/withdraw/" + currency, HttpPost.METHOD_NAME, Optional.of(parameterMap), new TypeReference<Map<String, Object>>(){});
    }

    private <T> T executeRequest(String path, String method, Optional<Object> body, Class<T> clazz) throws IOException {
        try (CloseableHttpResponse resp = execute(path, method, body)) {
            checkStatus(resp.getStatusLine(), resp.getEntity());
            return om.readValue(resp.getEntity().getContent(), clazz);
        }
    }

    private <T> T executeRequest(String path, String method, Optional<Object> body, TypeReference<T> typeReference) throws IOException {
        try (CloseableHttpResponse resp = execute(path, method, body)) {
            checkStatus(resp.getStatusLine(), resp.getEntity());
            return om.readValue(resp.getEntity().getContent(), typeReference);
        }
    }

    private CloseableHttpResponse execute(String path, String method, Optional<Object> body) throws IOException {

        Signature.BitoProPayload payload;

        RequestBuilder builder = RequestBuilder.create(method).setUri(getUrl(path));
        String json = "";
        if (body.isPresent()) {
            json = om.writeValueAsString(body.get());
            payload = signature.signJsonBody(json);
            HttpEntity jsonEntity = new StringEntity(json, StandardCharsets.UTF_8);
            builder.setEntity(jsonEntity);
        } else {
            payload = signature.signDefaultRequest(System.currentTimeMillis());
        }

        builder
            .addHeader(apiKeyHeader)
            .addHeader("X-BITOPRO-PAYLOAD", payload.getPayload())
            .addHeader("X-BITOPRO-SIGNATURE", payload.getSignature())
            .addHeader(CLIENT_HEADER);
        log.debug("url: {}, method: {}, signature: {}, body: {}, payload: {}", path, method, payload.getSignature(), json, payload.getPayload());

        return client.execute(builder.build());
    }
}
