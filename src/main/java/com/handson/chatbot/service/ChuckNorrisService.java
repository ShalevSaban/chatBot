package com.handson.chatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ChuckNorrisService {

    private final RestTemplate restTemplate;

    public ChuckNorrisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JokeSearchResponse searchJokesByQuery(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.chucknorris.io/jokes/search")
                .queryParam("query", query)
                .toUriString();
        return restTemplate.getForObject(url, JokeSearchResponse.class);
    }

    public Joke getJokeById(String id) {
        String url = "https://api.chucknorris.io/jokes/" + id;
        return restTemplate.getForObject(url, Joke.class);
    }

    static class JokeSearchResponse {
        private int total;
        private List<Joke> result;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<Joke> getResult() {
            return result;
        }

        public void setResult(List<Joke> result) {
            this.result = result;
        }

    }

    static class Joke {
        private String id;
        private String value;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}
