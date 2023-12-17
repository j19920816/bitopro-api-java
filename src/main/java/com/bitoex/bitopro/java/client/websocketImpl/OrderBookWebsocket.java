package com.bitoex.bitopro.java.client.websocketImpl;
import java.net.URISyntaxException;
import com.bitoex.bitopro.java.client.AbstractBitoProWebsocket;
import com.bitoex.bitopro.java.client.WebsocketCallback;

public class OrderBookWebsocket extends AbstractBitoProWebsocket {
    public OrderBookWebsocket(String pairLimitString, WebsocketCallback clientCallback) throws URISyntaxException {
        super("pub/order-books/"+ pairLimitString,"", "", "", clientCallback);
    }
}
