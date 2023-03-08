import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    CrawlStat crawlStat;
    Set<WebURL> visitedUrls;
    public MyCrawler(){
      crawlStat = new CrawlStat();
      visitedUrls = new HashSet<>();

    }

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|json"
            + "|png|mp3|mp3|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase().replace(",","_");

        crawlStat.increaseTotalUrl();

        //check if URL has not been visited before
        if(!this.visitedUrls.contains(url)){
            this.visitedUrls.add(url);
            crawlStat.increaseUniqueUrl();

            if (href.startsWith("https://www.foxnews.com"))
            {
                crawlStat.addOutgoingUrl(url.getURL(), true);
                crawlStat.increaseUniqueUrlWithin();
            }
            else
            {
                crawlStat.addOutgoingUrl(url.getURL(), false);
                crawlStat.increaseUniqueUrlOutside();
            }
        }

        return !FILTERS.matcher(href).matches()
                && href.startsWith("https://www.foxnews.com");

    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);
        String contentType = page.getContentType().toLowerCase();
        contentType = contentType.split(";")[0];

            if (page.getParseData() instanceof HtmlParseData)
            {
                    HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                    crawlStat.addDownloadUrls(url,page.getContentData().length, htmlParseData.getOutgoingUrls().size(), contentType);
            }else if (contentType.equals("application/document") || contentType.equals("application/pdf") || contentType.startsWith("image")){
                    crawlStat.addDownloadUrls(url, page.getContentData().length, 0, contentType);
            }

    }

    @Override
    public void handlePageStatusCode(WebURL url, int statusCode, String statusDescription)
    {
        crawlStat.addFetchedUrls(url.getURL(), statusCode);
    }


    @Override
    public Object getMyLocalData() {
        return crawlStat;
    }

}
