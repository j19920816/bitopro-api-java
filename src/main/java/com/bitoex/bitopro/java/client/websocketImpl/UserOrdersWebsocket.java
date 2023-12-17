package com.bitoex.bitopro.java.client.websocketImpl;
import java.net.URISyntaxException;
import com.bitoex.bitopro.java.client.AbstractBitoProWebsocket;
import com.bitoex.bitopro.java.client.WebsocketCallback;

public class UserOrdersWebsocket extends AbstractBitoProWebsocket {
    public UserOrdersWebsocket(String pairsString, String account, String apiKey, String secretKey, WebsocketCallback clientCallback) throws URISyntaxException {
        super("pub/auth/orders"+ pairsString, account, apiKey, secretKey, clientCallback);
    }
}

