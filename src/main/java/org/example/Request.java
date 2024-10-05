package org.example;

import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.util.List;

public class Request {
    private final String method;
    private final String path;
    private final List<NameValuePair> query;
    private final BufferedReader in;

    public Request(String method, String path, List<NameValuePair> query, BufferedReader in) {
        this.method = method;
        this.path = path;
        this.in = in;
        this.query = query;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public BufferedReader getIn() {
        return in;
    }
    public List<NameValuePair> getQuery() {
        return query;
    }
}
