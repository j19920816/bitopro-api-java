package com.bitoex.bitopro.java.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public final class BitoProUtils {
  
  public static CloseableHttpClient createDefaultClient() {
    return HttpClients.createDefault();
  }

  public static String getPair(String base, String quote) {
    return (base + "_" + quote).toLowerCase();
  }

  public static void validatePair(String pair) {
    if (StringUtils.isBlank(pair) || !StringUtils.contains(pair, "_")) {
      throw new IllegalArgumentException("pair cannot be blank and is in form of ${base}_${quote}");
    }
  }

  public static URI appendUri(String uri, String appendQuery) throws URISyntaxException {
        URI oldUri = new URI(uri);
        return new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(),
                oldUri.getQuery() == null ? appendQuery : oldUri.getQuery() + "&" + appendQuery, oldUri.getFragment());
    }
}