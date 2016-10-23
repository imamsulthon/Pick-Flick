package sadboy.pickflick.http;

public class DatabaseUrl {

    private volatile static DatabaseUrl uniqueInstance;

    private final String url = "http://api.themoviedb.org/3/";
    private final String API_KEY = "cf7563465415acb07a77c5156a4cefdd";

    private DatabaseUrl() {
    }

    public static DatabaseUrl getInstance() {
        if (uniqueInstance == null) {
            synchronized (DatabaseUrl.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new DatabaseUrl();
                }
            }
        }
        return uniqueInstance;
    }

    public String getPopularPersons(String page) {
        return url + "person/popular?api_key=" + API_KEY + "&page=" + page;
    }

    public String getPopularMovies() {
        return url + "movie/popular?api_key=" + API_KEY;
    }

    public String getUpcomingMovies(String page) {
        return url + "movie/upcoming?api_key=" + API_KEY + "&page=" + page;
    }

    public String getTopRatedMovies(String page) {
        return url + "movie/top_rated?api_key=" + API_KEY + "&page=" + page;
    }

    public String getNowPlayingMovies(String page) {
        return url + "movie/now_playing?api_key=" + API_KEY + "&page=" + page;
    }

    public String getHighestGrossingMovies(String page) {
        return url + "discover/movie?api_key=" + API_KEY + "&sort_by=revenue.desc" + "&page=" + page;
    }

    public String getPopularTV() {
        return url + "tv/popular?api_key=" + API_KEY;
    }

    public String getTopRatedTV(String page) {
        return url + "tv/top_rated?api_key=" + API_KEY + "&page=" + page;
    }

    public String getAiringTodayTV(String page) {
        return url + "tv/airing_today?api_key=" + API_KEY + "&sort_by=popularity.desc" + "&page=" + page;
    }

    public String getOnTheAirTV(String page) {
        return url + "tv/on_the_air?api_key=" + API_KEY + "&sort_by=popularity.desc" + "&page=" + page;
    }

    public String getMovieDetail(String id) {
        return url + "movie/" + id + "?api_key=" + API_KEY + "&append_to_response=images,credits,similar&include_adult=false";
    }

    public String getPersonDetail(String id) {
        return url + "person/" + id + "?api_key=" + API_KEY + "&append_to_response=images,combined_credits&include_adult=false";
    }

    public String getTVDetail(String id) {
        return url + "tv/" + id + "?api_key=" + API_KEY + "&append_to_response=images,credits,similar&include_adult=false";
    }

    public String getSearchResults(String query, String page) {
        return url + "search/multi?api_key=" + API_KEY + "&query=" + query + "&sort_by=popularity" + "&page=" + page;
    }

}
