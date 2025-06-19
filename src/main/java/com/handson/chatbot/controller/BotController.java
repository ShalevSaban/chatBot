package com.handson.chatbot.controller;



import com.handson.chatbot.service.ChuckNorrisService;
import com.handson.chatbot.service.ImdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
@
        RequestMapping("/bot")
public class BotController {

    @Autowired
    ImdbService imdbService;
    @Autowired
    ChuckNorrisService chuckJokes;

    @RequestMapping(value = "/imdb", method = RequestMethod.GET)
    public ResponseEntity<?> getTitle(@RequestParam String keyword) throws IOException {
        return new ResponseEntity<>(imdbService.searchTitles(keyword), HttpStatus.OK);
    }


    @RequestMapping(value = "/jokes", method = RequestMethod.GET)
    public ResponseEntity<?> getJoke(@RequestParam String keyword) throws IOException {
        return new ResponseEntity<>(chuckJokes.getRandomJokeString(keyword), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = { RequestMethod.POST})
    public ResponseEntity<?> getBotResponse(@RequestBody BotQuery query) throws IOException {
        HashMap<String, String> params = query.getQueryResult().getParameters();
        String res = "Not found";
        if (params.containsKey("word")) {
            res = chuckJokes.getRandomJokeString(params.get("word"));
        } else if (params.containsKey("title")) {
            res = imdbService.searchTitles(params.get("title"));
        }
        return new ResponseEntity<>(BotResponse.of(res), HttpStatus.OK);
    }

    static class BotQuery {
        private QueryResult queryResult;

        public BotQuery() {} // קונסטרקטור ברירת מחדל

        public QueryResult getQueryResult() {
            return queryResult;
        }

        public void setQueryResult(QueryResult queryResult) {
            this.queryResult = queryResult;
        }
    }

    static class QueryResult {
        private HashMap<String, String> parameters;

        public QueryResult() {}

        public HashMap<String, String> getParameters() {
            return parameters;
        }

        public void setParameters(HashMap<String, String> parameters) {
            this.parameters = parameters;
        }
    }

    static class BotResponse {
        private String fulfillmentText;
        private String source = "BOT";

        public BotResponse() {}

        public String getFulfillmentText() {
            return fulfillmentText;
        }

        public void setFulfillmentText(String fulfillmentText) {
            this.fulfillmentText = fulfillmentText;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public static BotResponse of(String fulfillmentText) {
            BotResponse res = new BotResponse();
            res.setFulfillmentText(fulfillmentText);
            return res;
        }
    }


}