package sadboy.pickflick.models;

/**
 * Created by Varun Kumar on 8/19/2016.
 */
public class SearchResult  {

    String id;
    String mediaType;
    String mediaName;
    String mediaImage;
    String mediaDate;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaImage(String mediaImage) {
        this.mediaImage = mediaImage;
    }

    public String getMediaImage()
    {
        return mediaImage;
    }

    public void setMediaDate(String mediaDate) {
        this.mediaDate = mediaDate;
    }

    public String getMediaDate() {
        return mediaDate;
    }
}
