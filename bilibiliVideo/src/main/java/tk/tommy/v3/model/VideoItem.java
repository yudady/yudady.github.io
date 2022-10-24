package tk.tommy.v3.model;

public class VideoItem {
    public String path;
    public String name;
    public Integer index;
    public String title;
    public String video;
    public String audio;

    @Override
    public String toString() {
        return "VideoItem{" +
                "path='" + path + '\'' +
                ", part='" + name + '\'' +
                ", page=" + index +
                ", title='" + title + '\'' +
                ", video='" + video + '\'' +
                ", audio='" + audio + '\'' +
                '}';
    }
}
