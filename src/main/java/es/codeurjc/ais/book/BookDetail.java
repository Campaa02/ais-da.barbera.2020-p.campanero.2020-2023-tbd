package es.codeurjc.ais.book;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.codeurjc.ais.review.Review;

public class BookDetail extends Book {

    private String description;

    private String imageUrl;

    private String[] subjects;

    private List<Review> reviews = new ArrayList<>();

    private static final int MAX_DESCRIPTION_LENGTH = 950;

    private static final Logger logger = LoggerFactory.getLogger(BookDetail.class);

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            logger.warn("Description too long, truncating...");
            this.description = description.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
        } else {
            this.description = description;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "BookDetail [description=" + description + ", imageUrl=" + imageUrl + "]";
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String[] getSubjects() {
        return subjects;
    }

    public void setSubjects(String[] subjects) {
        this.subjects = subjects;
    }
}