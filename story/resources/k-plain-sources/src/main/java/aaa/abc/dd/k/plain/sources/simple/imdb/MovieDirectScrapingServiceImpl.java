package aaa.abc.dd.k.plain.sources.simple.imdb;

import aaa.abc.dd.k.plain.sources.simple.imdb.Data.Movie;
import aaa.abc.dd.k.plain.sources.simple.imdb.Data.Participant;
import aaa.abc.dd.k.plain.sources.simple.imdb.Data.MovieLink;
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
    private final Integer countMovies;
    private final boolean scrapParticipants;

    private final String baseUrl = "https://www.imdb.com";
    private final String baseNameUrl = "https://www.imdb.com/name";
    private final String baseTitleUrl = "https://www.imdb.com/title";

    public MovieDirectScrapingServiceImpl(
            String language,
            String startDate,
            String endDate,
            String countries,
            Integer countMovies,
            boolean scrapParticipants
    ) {
        this.language = language;
        this.startDate = startDate;
        this.endDate = endDate;
        this.countries = countries;
        this.countMovies = countMovies;
        this.scrapParticipants = scrapParticipants;
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
            boolean next = collectItems(doc, items);
            if (!next) {
                return "";
            }
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

    boolean collectItems(Document doc, List<Movie> items) {
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
                Double rating = 0.0;
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
                List<String> participantIdsStore = new ArrayList<>();
                StringBuilder participantRanks = new StringBuilder();
                StringBuilder participantCountMovies = new StringBuilder();
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
                                    participantIdsStore.add(pHrefParts[2]);
                                } else {
                                    directorIds.append(pHrefParts[2]);
                                    directorIds.append("~");
                                    directorNames.append(participantElement.text());
                                    directorNames.append("~");
                                }
                            }
                        }
                    }
                    if (scrapParticipants) {
                        for (String participantId : participantIdsStore) {
                            Participant participant = scrapParticipant(baseUrl, participantId, language);
                            participantRanks.append(participant.meterRank);
                            participantRanks.append("~");
                            participantCountMovies.append(participant.movieLinks.length);
                            participantCountMovies.append("~");
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
                        directorNames.toString(),
                        participantRanks.toString(),
                        participantCountMovies.toString()
                ));
                if (countMovies != null && items.size() >= countMovies) {
                    return false;
                }
            }
        }
        return true;
    }

    static Participant scrapParticipant(String baseUrl, String id, String language) {
        String url = String.format(baseUrl + "/name/%s", id);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).header("Accept-Language", language).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Data.MovieLink> movieLinksInner = new ArrayList<>();
        String nameInner = "";
        String meterRank = "";
        if (doc != null) {
            Elements nameElements = doc.select("span.itemprop");
            if (nameElements.size() > 0) {
                nameInner = nameElements.get(0).text();
            }
            Elements itemElements = doc.select("div.filmo-category-section");
            if (itemElements.size() > 0) {
                Element itemElement = itemElements.get(0);
                Elements moviesElements = itemElement.select(".filmo-row");
                for (Element movieElement : moviesElements) {
                    Elements hrefElements = movieElement.select("a");
                    if (hrefElements.size() > 0) {
                        Element hrefElement = hrefElements.get(0);
                        String[] pHrefParts = hrefElement.attr("href").split("/");
                        String tileId = pHrefParts[2];
                        String tileUrl = hrefElement.attr("href");
                        String title = hrefElement.text();
                        MovieLink movieLink = new MovieLink(tileId, tileUrl, title);
                        movieLinksInner.add(movieLink);
                    }
                }
            }
            Element meterRankElement = doc.getElementById("meterRank");
            meterRank = meterRankElement.text();
        }
        MovieLink[] movieLinks = new MovieLink[movieLinksInner.size()];
        movieLinks = movieLinksInner.toArray(movieLinks);
        return new Participant(id, url, nameInner, movieLinks, meterRank);
    }

    public static void main(String[] args) {
        tests();
    }

    static void tests() {
        test1();
        // test2();
    }

    static void test1() {
        MovieDirectScrapingService movieDirectScrapingService =
                new MovieDirectScrapingServiceImpl("en", "2019-01-20", "2019-01-20", "us", 7, false);
        Collection<Movie> movies = movieDirectScrapingService.scrap();
        System.out.println(movies.size());
    }

    static void test2() {
        Participant participant =
                scrapParticipant("https://www.imdb.com", "nm1573253", "en");
        System.out.println(participant.name);
    }
}
