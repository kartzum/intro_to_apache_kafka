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
        public final String participantRanks;
        public final String participantCountMovies;

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
                String directorNames,
                String participantRanks,
                String participantCountMovies
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
            this.participantRanks = participantRanks;
            this.participantCountMovies = participantCountMovies;
        }
    }

    public static class MovieLink {
        public final String titleId;
        public final String titleUrl;
        public final String title;

        public MovieLink(String titleId, String titleUrl, String title) {
            this.titleId = titleId;
            this.titleUrl = titleUrl;
            this.title = title;
        }
    }

    public static class Participant {
        public final String nmId;
        public final String nmUrl;
        public final String name;
        public final MovieLink[] movieLinks;
        public final String meterRank;

        public Participant(String nmId, String nmUrl, String name, MovieLink[] movieLinks, String meterRank) {
            this.nmId = nmId;
            this.nmUrl = nmUrl;
            this.name = name;
            this.movieLinks = movieLinks;
            this.meterRank = meterRank;
        }
    }
}
