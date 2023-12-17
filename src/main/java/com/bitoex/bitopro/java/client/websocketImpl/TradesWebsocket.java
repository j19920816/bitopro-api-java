package com.bitoex.bitopro.java.client.websocketImpl;
import java.net.URISyntaxException;
import com.bitoex.bitopro.java.client.AbstractBitoProWebsocket;
import com.bitoex.bitopro.java.client.WebsocketCallback;

public class TradesWebsocket extends AbstractBitoProWebsocket {
    public TradesWebsocket(String pairsString, WebsocketCallback clientCallback) throws URISyntaxException {
        super("pub/trades/"+ pairsString, "", "", "", clientCallback);
    }
}
