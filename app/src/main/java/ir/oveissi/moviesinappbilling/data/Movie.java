package ir.oveissi.moviesinappbilling.data;

public class Movie {
    private String title;
    private String director;
    private String poster;
    private String screenshot;
    private Float rating;
    private Integer duration; // in minutes
    private String description;

    public Movie(String title, String director, String poster, String screenshot, Float rating, Integer duration, String description) {
        this.title = title;
        this.director = director;
        this.poster = poster;
        this.screenshot = screenshot;
        this.rating = rating;
        this.duration = duration;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getPoster() {
        return poster;
    }

    public Float getRating() {
        return rating;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public String getScreenshot() {
        return screenshot;
    }
}
