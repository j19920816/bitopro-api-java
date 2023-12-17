package com.bitoex.bitopro.java.client.websocketImpl;
import java.net.URISyntaxException;
import com.bitoex.bitopro.java.client.AbstractBitoProWebsocket;
import com.bitoex.bitopro.java.client.WebsocketCallback;

public class UserBlanceWebsocket  extends AbstractBitoProWebsocket {
    public UserBlanceWebsocket(String pairsString, String account, String apiKey, String secretKey, WebsocketCallback clientCallback) throws URISyntaxException {
        super("pub/auth/account-balance"+ pairsString, account, apiKey, secretKey, clientCallback);
    }
}

