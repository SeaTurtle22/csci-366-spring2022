package edu.montana.csci.csci366.strweb.ops;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class is should pass the SHA 256 calculation through to the two listed NODES in
 * the SHA 256 cloud
 *
 * It should dispatch them via a POST with a `Content-Type` header set to `text/plain`
 *
 * It should dispatch the two requests concurrently and then merge them.
 *
 */
public class CloudSha256Transformer {

    List<String> NODES = Arrays.asList("http://localhost:8001", "http://localhost:8002");
    private final String _strings;

    public CloudSha256Transformer(String strings) {
        _strings = strings;
    }

    public String toSha256Hashes() {
        try{
            int index = _strings.indexOf('\n',_strings.length()/2); //We want to split our work among two machines, so here we find the index of the middle line to split the work on

            String firstChunk = _strings.substring(0,index); //Stores the first half of the string to an intermediate variable
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create((NODES.get(0))))
                    .headers("Content-Type","text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString("op=Line+Sha256&Strings="+URLEncoder.encode(firstChunk, StandardCharsets.UTF_8.name())))
                    .build(); //Creates a request with the first half of the string asking the first node to hash what is being given to it.
            HttpResponse <String> firstResponse = client.send(request,HttpResponse.BodyHandlers.ofString()); //Sends the request and stores the response

            String secondChunk = _strings.substring(index);//Stores the second half of the string to an intermediate variable
            var request2 = HttpRequest.newBuilder()
                    .uri(URI.create((NODES.get(1))))
                    .headers("Content-Type","text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString("op=Line+Sha256&Strings="+URLEncoder.encode(secondChunk, StandardCharsets.UTF_8.name())))
                    .build(); //Creates a request with the first half of the string asking second the node to hash what is being given to it.
            HttpResponse <String> secondResponse = client.send(request,HttpResponse.BodyHandlers.ofString());

            return firstResponse.body() + secondResponse.body(); //returns a concatenation of both responses

        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
