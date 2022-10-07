package api;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class HttpRequests {



    public String requests(String medicine) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest getRequest = (HttpRequest) HttpRequest.newBuilder()
                .uri(new URI("https://drug-info-and-price-history.p.rapidapi.com/1/druginfo?drug='"+medicine+"'"))
                .header("X-RapidAPI-Key", "5b65f7218cmsh1e5c751d3a923e7p1726c6jsn4f1ae757b82b")
                .header("X-RapidAPI-Host", "drug-info-and-price-history.p.rapidapi.com")
                .build();


        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(getResponse.body());
        if(getResponse.body() != null){
            return "founded";
        }else  {
            return "null";
        }

    }

}
