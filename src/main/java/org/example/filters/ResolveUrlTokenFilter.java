package org.example.filters;

import com.google.common.base.Preconditions;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.regex.Pattern;

public class ResolveUrlTokenFilter extends TokenFilter {
    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    private static final Pattern patternToMatchShortenedUrls = Pattern.compile("https://bit.ly/\\w+");
    CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();

    protected ResolveUrlTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }
        char[] term = termAttribute.buffer();
        int len = termAttribute.length();
        String token = new String(term, 0, len);
        if (patternToMatchShortenedUrls.matcher(token).matches()) {
            termAttribute.setEmpty().append(resolveShortenedUrl(token));
        }
        return true;
    }

    private String resolveShortenedUrl(String url) throws IOException {
        HttpHead request = null;
        try {
            request = new HttpHead(url);
            HttpResponse httpResponse = client.execute(request);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != 301 && statusCode != 302) {
                return url;
            }
            Header[] headers = httpResponse.getHeaders(HttpHeaders.LOCATION);
            Preconditions.checkState(headers.length == 1);
            String newUrl = headers[0].getValue();
            return newUrl;
        } catch (IllegalArgumentException uriEx) {
            return url;
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
    }
}