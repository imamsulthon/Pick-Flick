package sadboy.pickflick.models;

import java.util.List;

/**
 * Created by Varun Kumar on 7/25/2016.
 */
public class Person {

    private String name;
    private String profilePath;
    private String knownForFilm;
    private String role;
    private String id;
    private List<String> images;

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getKnownForFilm() {
        return knownForFilm;
    }

    public void setKnownForFilm(String knownForFilm) {
        this.knownForFilm = knownForFilm;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {return id;}

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
