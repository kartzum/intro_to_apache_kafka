package aaa.abc.dd.k.plain.sources.simple.imdb;

import aaa.abc.dd.k.plain.sources.simple.imdb.Data.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MovieDirectScrapingServiceImpl implements MovieDirectScrapingService {
    private final String language;
    private final String startDate;
    private final String endDate;
    private final String countries;

    public MovieDirectScrapingServiceImpl(
            String language,
            String startDate,
            String endDate,
            String countries
    ) {
        this.language = language;
        this.startDate = startDate;
        this.endDate = endDate;
        this.countries = countries;
    }

    @Override
    public Collection<Movie> scrap() {
        String url = String.format(
                "https://www.imdb.com/search/title/?release_date=%s,%s&countries=%s",
                startDate, endDate, countries
        );
        List<Movie> items = new ArrayList<>();
        String nextUrl = url;
        while (true) {
            nextUrl = scrap(nextUrl, items);
            if ("".equals(nextUrl)) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }
        return items;
    }

    String scrap(String url, List<Movie> items) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).header("Accept-Language", language).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc != null) {
            collectItems(doc, items);
            return nextUrl(doc);
        }
        return "";
    }

    String nextUrl(Document doc) {
        Elements nextPageElements = doc.select(".next-page");
        if (nextPageElements.size() > 0) {
            Element hrefElement = nextPageElements.get(0);
            return "https://www.imdb.com" + hrefElement.attributes().get("href");
        }
        return "";
    }

    void collectItems(Document doc, List<Movie> items) {
        Elements itemElements = doc.select("h3.lister-item-header");
        for (Element itemElement : itemElements) {
            Element aElement = itemElement.selectFirst("a");
            if (aElement != null) {
                Element parentItemElement = itemElement.parent();
                Elements textElements = parentItemElement.select("p.text-muted");
                Element ratingElement = parentItemElement.selectFirst(".ratings-imdb-rating");
                Elements genreElements = parentItemElement.select(".genre");
                String url = "https://www.imdb.com" + aElement.attributes().get("href");
                String title = aElement.text();
                String shortText = "";
                if (textElements.size() > 1) {
                    shortText = textElements.get(1).text();
                }
                Double rating = null;
                if (ratingElement != null) {
                    try {
                        rating = Double.parseDouble(ratingElement.attributes().get("data-value"));
                    } catch (NumberFormatException e) {
                    }
                }
                String genres = "";
                if (genreElements.size() > 0) {
                    genres = genreElements.get(0).text();
                }
                items.add(new Movie(url, title, shortText, rating, genres));
            }
        }
    }

    public static void main(String[] args) {
        tests();
    }

    static void tests() {
        test1();
    }

    static void test1() {
        MovieDirectScrapingService movieDirectScrapingService =
                new MovieDirectScrapingServiceImpl("en", "2019-01-20", "2019-01-21", "us");
        Collection<Movie> movies = movieDirectScrapingService.scrap();
        System.out.println(movies.size());
    }
}
