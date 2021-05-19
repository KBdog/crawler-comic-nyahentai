package entity;

public class Comic {
    private String comicName;
    private String comicUrl;

    public Comic() {
    }

    public Comic(String comicName, String comicUrl) {
        this.comicName = comicName;
        this.comicUrl = comicUrl;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getComicUrl() {
        return comicUrl;
    }

    public void setComicUrl(String comicUrl) {
        this.comicUrl = comicUrl;
    }
}
