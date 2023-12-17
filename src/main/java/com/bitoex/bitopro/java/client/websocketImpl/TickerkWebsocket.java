package com.bitoex.bitopro.java.client.websocketImpl;
import java.net.URISyntaxException;
import com.bitoex.bitopro.java.client.AbstractBitoProWebsocket;
import com.bitoex.bitopro.java.client.WebsocketCallback;

public class TickerkWebsocket extends AbstractBitoProWebsocket {
    public TickerkWebsocket(String pairsString, WebsocketCallback clientCallback) throws URISyntaxException {
        super("pub/tickers/"+ pairsString, "", "", "", clientCallback);
    }
}
