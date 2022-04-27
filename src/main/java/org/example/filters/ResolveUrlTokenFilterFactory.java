package org.example.filters;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;
import java.util.regex.Pattern;

public class ResolveUrlTokenFilterFactory extends TokenFilterFactory {

    public ResolveUrlTokenFilterFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new ResolveUrlTokenFilter(input);
    }

}