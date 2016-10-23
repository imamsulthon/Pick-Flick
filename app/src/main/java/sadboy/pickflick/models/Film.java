package sadboy.pickflick.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Varun Kumar on 7/20/2016.
 */
public class Film {

    private String title;
    private String posterPath;
    private String backDropPath;
    private String overview;
    private String id;



    public Film() {
    }

    public String getOverview(){return overview;}

    public void setOverview(String overview){this.overview = overview;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackDropPath()
    {
        return backDropPath;
    }

    public void setBackDropPath(String backDrop)
    {
        this.backDropPath = backDrop;
    }

    public String getId()
    {return id;}

    public void setId(String id){this.id = id;}
}
