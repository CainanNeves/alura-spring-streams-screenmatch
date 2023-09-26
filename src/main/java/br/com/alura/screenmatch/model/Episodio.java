package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
    private Integer season;
    private String title;
    private Integer epNumber;
    private Double rating;
    private LocalDate releaseDate;

    public Integer getSeason() {
        return season;
    }

    public String getTitle() {
        return title;
    }

    public Integer getEpNumber() {
        return epNumber;
    }

    public Double getRating() {
        return rating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Episodio(Integer numSeason, DadosEpisodio dadosEpisodio) {
        season = numSeason;
        title = dadosEpisodio.title();
        epNumber = dadosEpisodio.epNumber();
        try{
            rating = Double.valueOf(dadosEpisodio.rating());
        } catch (NumberFormatException e){
            rating = 0.0;
        }

        try{
            releaseDate = LocalDate.parse(dadosEpisodio.releaseDate());
        } catch ( DateTimeParseException e){
            releaseDate = null;
        }
    }

    @Override
    public String toString() {
        return  "season=" + season +
                ", title='" + title + '\'' +
                ", epNumber=" + epNumber +
                ", rating=" + rating +
                ", releaseDate=" + releaseDate;
    }
}
