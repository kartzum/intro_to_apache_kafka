package aaa.abc.dd.fs.et.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImdbA {
    public interface ImdbASimple {
        Collection<ImdbAItem> scrap();
    }

    public interface ImdbASimpleFactory {
        ImdbASimple create(String language, String startDate, String endDate, String countries);
    }

    public static class ImdbASimpleScraping implements ImdbASimple {
        private final String language;
        private final String startDate;
        private final String endDate;
        private final String countries;

        public ImdbASimpleScraping(String language, String startDate, String endDate, String countries) {
            this.language = language;
            this.startDate = startDate;
            this.endDate = endDate;
            this.countries = countries;
        }

        @Override
        public Collection<ImdbAItem> scrap() {
            String url = String.format(
                    "https://www.imdb.com/search/title/?release_date=%s,%s&countries=%s",
                    startDate, endDate, countries
            );
            List<ImdbAItem> items = new ArrayList<>();
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

        String scrap(String url, List<ImdbAItem> items) {
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

        void collectItems(Document doc, List<ImdbAItem> items) {
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
                    ImdbAItem item = new ImdbAItem(url, title, shortText, rating, genres);
                    items.add(item);
                }
            }
        }
    }

    public static class ImdbAItem {
        public final String url;
        public final String title;
        public final String shortText;
        public final Double rating;
        public final String genres;

        public ImdbAItem(String url, String title, String shortText, Double rating, String genres) {
            this.url = url;
            this.title = title;
            this.shortText = shortText;
            this.rating = rating;
            this.genres = genres;
        }
    }


    public static void main(String[] args) {
        tests();
    }

    static void tests() {
        test1();
    }

    static void test1() {
        ImdbASimpleScraping imdbASimpleScraping =
                new ImdbASimpleScraping("en", "2019-01-20", "2019-01-21", "us");
        Collection<ImdbAItem> imdbAItems = imdbASimpleScraping.scrap();
        System.out.println(imdbAItems.size());
    }
}
