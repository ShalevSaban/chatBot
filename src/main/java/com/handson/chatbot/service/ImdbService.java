package com.handson.chatbot.service;

import org.springframework.stereotype.Service;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

@Service
public class ImdbService {

    private final OkHttpClient client = new OkHttpClient();

    private String getHtml(String keyword) throws IOException {
        String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
        String url = "https://www.imdb.com/find?q=" + encodedKeyword + "&s=tt&exact=true&ref_=fn_ttl_ex";

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .header("Referer", "https://www.imdb.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String searchTitles(String keyword) throws IOException {
        String html = getHtml(keyword);
        return parseTitlesHtml(html);
    }

    private String parseTitlesHtml(String html) {
        Document doc = Jsoup.parse(html);
        Elements items = doc.select("li.ipc-metadata-list-summary-item"); // כל רשומת סרט

        StringBuilder sb = new StringBuilder();

        for (Element item : items) {
            Element titleElement = item.selectFirst("a.ipc-metadata-list-summary-item__t");
            if (titleElement == null) continue;
            String titleText = titleElement.text();
            String href = titleElement.attr("href");
            String fullLink = "https://www.imdb.com" + href;

            Element yearElement = item.selectFirst("span.ipc-metadata-list-summary-item__li.ipc-btn--not-interactable");
            String year = (yearElement != null) ? yearElement.text().replaceAll("[^0-9]", "") : "Unknown year";

            Elements actorsElements = item.select("ul.ipc-inline-list > li > span.ipc-metadata-list-summary-item__li.ipc-btn--not-interactable");
            String actors = "Unknown actors";
            if (!actorsElements.isEmpty()) {
                actors = actorsElements.stream()
                        .map(Element::text)
                        .filter(text -> !text.matches(".*\\d{4}.*")) // מסנן טקסט עם שנה
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Unknown actors");
            }

            sb.append(titleText).append(" (").append(year).append(")<br>\n");
            sb.append("Actors: ").append(actors).append("<br>\n");
            sb.append("Link: ").append(fullLink).append("<br>\n\n");
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        ImdbService service = new ImdbService();
        String result = service.searchTitles("the godfather");
        System.out.println(result);
    }
}
