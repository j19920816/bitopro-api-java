package com.bitoex.bitopro.java.client;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.bitoex.bitopro.java.signature.Signature;
import java.util.logging.Logger;
import java.util.Map;

public class AbstractBitoProWebsocket extends WebSocketClient {
    private String _apiKey;
    private String _secretKey;
    private String _account;
    private Signature signature;
    private WebsocketCallback websocketCallback;
    protected Logger logger = Logger.getLogger("Websocket logging");
    public AbstractBitoProWebsocket(String functionUrl, String account, String apiKey, String secretKey, WebsocketCallback clientCallback) throws URISyntaxException {
        super(new URI("wss://stream.bitopro.com:443/ws/v1/"+functionUrl));
        websocketCallback = clientCallback;

        _apiKey = apiKey;
        _secretKey = secretKey;
        _account = account;
    }

    public static String getPairsLimitString(Map<String, Integer> pairsLimitMap)
    {
        String result = "";
        for (Map.Entry<String, Integer> entry : pairsLimitMap.entrySet()) {
            result+=String.format("%s:%d,", entry.getKey(), entry.getValue());
		}
        return result;
    }

    public static String getPairsString(List<String> pairs)
    {
        String result = "";
        for (String pair : pairs) {
            result += String.format("%s,", pair);
        }
        return result;
    }

    public void initWebsocket()
    {
        if(_apiKey!="" && _secretKey!="" && _account!="")
        {
            signature = new Signature(_account, _secretKey);
            Signature.BitoProPayload bitoproPayload = signature.signDefaultRequest(System.currentTimeMillis());

            this.clearHeaders();
            this.addHeader("X-BITOPRO-APIKEY", _apiKey);
            this.addHeader("X-BITOPRO-PAYLOAD", bitoproPayload.getPayload());
            this.addHeader("X-BITOPRO-SIGNATURE", bitoproPayload.getSignature());
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info(String.format("websocket connected, %s\r\n", serverHandshake.getHttpStatusMessage()));
    }

    @Override
    public void onMessage(String message) {
        if(websocketCallback!=null)
            websocketCallback.processResponse(message);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.info(String.format("%s, disconnected by server: %s\r\n",s, b));
    }

    @Override
    public void onError(Exception e) {
        logger.warning(String.format("websocket error: %s\r\n", e.getMessage()));
    }
}