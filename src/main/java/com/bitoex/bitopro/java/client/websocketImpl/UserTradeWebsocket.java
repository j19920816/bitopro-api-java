package com.bitoex.bitopro.java.client.websocketImpl;
import java.net.URISyntaxException;
import com.bitoex.bitopro.java.client.AbstractBitoProWebsocket;
import com.bitoex.bitopro.java.client.WebsocketCallback;

public class UserTradeWebsocket extends AbstractBitoProWebsocket {
    public UserTradeWebsocket(String pairsString, String account, String apiKey, String secretKey, WebsocketCallback clientCallback) throws URISyntaxException {
        super("pub/auth/user-trades"+ pairsString, account, apiKey, secretKey, clientCallback);
    }
}
