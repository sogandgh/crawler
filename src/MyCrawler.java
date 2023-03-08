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

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|feed|rss|svg|json|vcf|xml|js|gif|"
            + "|mp3|zip|gz|json|x-crossword|pgp-signature|icon|x-icon|charset=utf-8|charset=UTF-8))$");

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */


    public static String normalizeUrl(String url)
    {
        if (url.endsWith("/"))
        {
            url = url.substring(0, url.length() - 1);
        }

        return url.toLowerCase().replace(",","_").replaceFirst("^(https?://)?(www.)?", "");
    }

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

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        int size = page.getContentData().length;
        String contentType = page.getContentType().toLowerCase().split(";")[0];

            if (contentType.equals("text/html"))
            {
                if (page.getParseData() instanceof HtmlParseData)
                {
                    HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                    Set<WebURL> links = htmlParseData.getOutgoingUrls();
                    crawlStat.addDownloadUrls(url,size, links.size(), contentType);
                }

            }else if (contentType.equals("application/pdf") || contentType.equals("application/document") || contentType.startsWith("image")){
                    crawlStat.addDownloadUrls(url, size, 0, contentType);
            }



      /*  if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        }*/
    }

    @Override
    public void handlePageStatusCode(WebURL url, int statusCode, String statusDescription)
    {
        crawlStat.addFetchedUrls(url.getURL(), statusCode);
    }

    /** This function is called by controller to get the local data of this crawler when job is
     * finished
     */
    @Override
    public Object getMyLocalData() {
        return crawlStat;
    }

    /**
     * This function is called by controller before finishing the job.
     * You can put whatever stuff you need here.
     */
    @Override
    public void onBeforeExit() {
        dumpMyData();
    }


    public void dumpMyData() {
        int id = getMyId();
        // You can configure the log to output to file
        logger.info("Crawler {} > Total Pages: {}", id, crawlStat.getTotalFetch());

    }

}
