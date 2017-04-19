package com.example.moviedatabasev2;

import android.graphics.Bitmap;

/**
 * Created by Chris Mulcahy on 19/03/2017.
 */

public class Movie {
    private String name;
    private String description;
    private String realeaseDate;
    private String posterPath;
    private String language;
    private double voteAverage;
    private int numVotes;
    private int id;
    private int [] genreId;

    public Movie()
    {

    }

    public Movie(String name, String description, String realeaseDate, String posterPath, String language, double voteAverage, int numVotes, int id, int[] genreId) {
        this.name = name;
        this.description = description;
        this.realeaseDate = realeaseDate;
        this.posterPath = posterPath;
        this.language = language;
        this.voteAverage = voteAverage;
        this.numVotes = numVotes;
        this.id = id;
        this.genreId = genreId;
    }

    public int[] getGenreId() {
        return genreId;
    }

    public void setGenreId(int[] genreId) {
        this.genreId = genreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRealeaseDate() {
        return realeaseDate;
    }

    public void setRealeaseDate(String realeaseDate) {
        this.realeaseDate = realeaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

