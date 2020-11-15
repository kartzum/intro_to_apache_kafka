package aaa.abc.dd.k.plain.sources.simple.imdb;

public class Data {
    static class Movie {
        public final String url;
        public final String title;
        public final String description;
        public final Double rating;
        public final String genres;

        public Movie(
                String url,
                String title,
                String description,
                Double rating,
                String genres
        ) {
            this.url = url;
            this.title = title;
            this.description = description;
            this.rating = rating;
            this.genres = genres;
        }
    }
}
