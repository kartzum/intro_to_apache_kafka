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

    private final String baseUrl = "https://www.imdb.com";
    private final String baseNameUrl = "https://www.imdb.com/name";
    private final String baseTitleUrl = "https://www.imdb.com/title";

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
                baseUrl + "/search/title/?release_date=%s,%s&countries=%s",
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
            return baseUrl + hrefElement.attributes().get("href");
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
                Elements runtimeElements = parentItemElement.select(".runtime");
                Elements pElements = parentItemElement.select("p");
                String href = aElement.attributes().get("href");
                String[] hrefParts = href.split("/");
                String titleUrl = href;
                String title = aElement.text();
                String description = "";
                if (textElements.size() > 1) {
                    description = textElements.get(1).text();
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
                String titleId = "";
                if (hrefParts.length > 1) {
                    titleId = hrefParts[2];
                }
                String runtime = "";
                if (runtimeElements.size() > 0) {
                    runtime = runtimeElements.get(0).text();
                }
                StringBuilder participantIds = new StringBuilder();
                StringBuilder participantNames = new StringBuilder();
                StringBuilder directorIds = new StringBuilder();
                StringBuilder directorNames = new StringBuilder();
                if (pElements.size() > 1) {
                    Element participantsElement = pElements.get(2);
                    boolean firstDirectors = participantsElement.text().startsWith("Director");
                    for (Element participantElement : participantsElement.children()) {
                        if ("|".equals(participantElement.text())) {
                            firstDirectors = false;
                        }
                        if (participantElement.attributes().hasKey("href")) {
                            String pHref = participantElement.attributes().get("href");
                            String[] pHrefParts = pHref.split("/");
                            if (pHrefParts.length > 1) {
                                if (!firstDirectors) {
                                    participantIds.append(pHrefParts[2]);
                                    participantIds.append("~");
                                    participantNames.append(participantElement.text());
                                    participantNames.append("~");
                                } else {
                                    directorIds.append(pHrefParts[2]);
                                    directorIds.append("~");
                                    directorNames.append(participantElement.text());
                                    directorNames.append("~");
                                }
                            }
                        }
                    }
                }
                items.add(new Movie(
                        titleId,
                        titleUrl,
                        title,
                        description,
                        rating,
                        genres,
                        runtime,
                        baseUrl,
                        baseNameUrl,
                        baseTitleUrl,
                        participantIds.toString(),
                        participantNames.toString(),
                        directorIds.toString(),
                        directorNames.toString()
                ));
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
