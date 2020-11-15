package aaa.abc.dd.k.plain.sources.simple.imdb;

public class Data {
    static class Movie {
        public final String titleId;
        public final String titleUrl;
        public final String title;
        public final String description;
        public final Double rating;
        public final String genres;
        public final String runtime;
        public final String baseUrl;
        public final String baseNameUrl;
        public final String baseTitleUrl;
        public final String participantIds;
        public final String participantNames;
        public final String directorIds;
        public final String directorNames;

        public Movie(
                String titleId,
                String titleUrl,
                String title,
                String description,
                Double rating,
                String genres,
                String runtime,
                String baseUrl,
                String baseNameUrl,
                String baseTitleUrl,
                String participantIds,
                String participantNames,
                String directorIds,
                String directorNames
        ) {
            this.titleId = titleId;
            this.titleUrl = titleUrl;
            this.title = title;
            this.description = description;
            this.rating = rating;
            this.genres = genres;
            this.runtime = runtime;
            this.baseUrl = baseUrl;
            this.baseNameUrl = baseNameUrl;
            this.baseTitleUrl = baseTitleUrl;
            this.participantIds = participantIds;
            this.participantNames = participantNames;
            this.directorIds = directorIds;
            this.directorNames = directorNames;
        }
    }
}
