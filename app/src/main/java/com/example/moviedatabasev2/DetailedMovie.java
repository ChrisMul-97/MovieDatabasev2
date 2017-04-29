package com.example.moviedatabasev2;

/**
 * Created by Chris Mulcahy on 03/04/2017.
 */

public class DetailedMovie extends Movie {

    private String tagline;
    private String runtime;
    private String buget;
    private String revenue;
    private String [] genreArray;

    public DetailedMovie(String name, String description, String realeaseDate, String posterPath, String language, double voteAverage, int numVotes, int id, int[] genreId, String tagline, String runtime, String budget, String revenue, String [] genreIdName)
    {
        super(name, description, realeaseDate, posterPath, language, voteAverage, numVotes, id, genreId);
        this.tagline = tagline;
        this.runtime = runtime;
        this.buget = budget;
        this.revenue = revenue;
        this.genreArray = genreIdName;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getBuget() {
        return buget;
    }

    public void setBuget(String buget) {
        this.buget = buget;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String[] getGenreArray() {
        return genreArray;
    }

    public void setGenreArray(String[] genreArray) {
        this.genreArray = genreArray;
    }
}
