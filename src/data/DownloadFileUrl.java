package data;

public class DownloadFileUrl {

    public String url;
    public int size;
    public int outlinkCount;
    public  String contentType;

    public DownloadFileUrl(String url, int size, int outlinkCount, String contentType){
        this.url = url;
        this.size = size;
        this.outlinkCount = outlinkCount;
        this.contentType = contentType;
    }
}
