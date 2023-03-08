import data.DownloadFileUrl;
import data.FetchUrl;
import data.OutgoingUrl;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger logger =
            LoggerFactory.getLogger(Controller.class);

    public static void main(String[] args) throws Exception {

        List<Object> allCrawlData = performCrawling();
        logger.info("Crawling finished");
        CrawlStat crawlStat = fetchStat(allCrawlData);
        writeCsv(crawlStat);
        writeReport(crawlStat);

    }

    private static CrawlStat fetchStat(List<Object> allCrawlData) throws Exception {
        logger.info("fetching stats");

        ArrayList<FetchUrl> fetchedUrls = new ArrayList<>();
        ArrayList<OutgoingUrl> outgoingUrls = new ArrayList<>();
        ArrayList<DownloadFileUrl> downloadedUrls = new ArrayList<>();
        int totalFetch = 0;
        int totalSuccessFetch = 0;
        int totalFailedFetch = 0;
        int totalUrl = 0;
        int uniqueUrl = 0;
        int uniqueUrlWithin = 0;
        int uniqueUrlOutside = 0;

        for (Object iter : allCrawlData) {
            CrawlStat data = (CrawlStat) iter;
            fetchedUrls.addAll(data.fetchedUrls);
            outgoingUrls.addAll(data.outgoingUrls);
            downloadedUrls.addAll(data.downloadedUrls);
            totalFetch += data.getTotalFetch();
            totalSuccessFetch += data.getTotalSuccessFetch();
            totalFailedFetch += data.getTotalFailedFetch();
            totalUrl += data.getTotalUrl();
            uniqueUrl += data.getUniqueUrl();
            uniqueUrlWithin += data.getUniqueUrlWithin();
            uniqueUrlOutside += data.getUniqueOutside();
        }

        CrawlStat crawlStat = new CrawlStat(fetchedUrls, outgoingUrls, downloadedUrls, totalFetch, totalSuccessFetch, totalFailedFetch, totalUrl, uniqueUrl,
                uniqueUrlWithin, uniqueUrlOutside);
        return crawlStat;

    }

    private static List<Object> performCrawling() throws Exception {
        String crawlStorageFolder = "data";
        int numberOfCrawlers = 7;
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(16);
        config.setMaxPagesToFetch(20000);
        config.setPolitenessDelay(200);
        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("https://www.foxnews.com");
        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(MyCrawler.class, numberOfCrawlers);
        return controller.getCrawlersLocalData();
    }

    private static void writeCsv(CrawlStat crawlStat) throws Exception {
        logger.info("Generating CSV files");

        File newFile = new File("fetch_" + "FoxNews" + ".csv");
        newFile.delete();
        newFile.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, true));
        bw.append("URL,Status\n");

        for (FetchUrl fetchUrl : crawlStat.getFetchedUrls()) {
            bw.append(fetchUrl.url + "," + fetchUrl.statusCode + "\n");
        }
        bw.close();

        newFile = new File("visit_" + "FoxNews" + ".csv");
        newFile.delete();
        newFile.createNewFile();
        bw = new BufferedWriter(new FileWriter(newFile, true));
        bw.write("URL, Size(Bytes), # of Outlinks, Content-Type\n");

        for (DownloadFileUrl downloadFileUrl : crawlStat.getDownloadedUrls()) {
            bw.append(downloadFileUrl.url + "," + downloadFileUrl.size + "," + downloadFileUrl.outlinkCount + "," + downloadFileUrl.contentType + "\n");
        }
        bw.close();

        newFile = new File("urls_" + "FoxNews" + ".csv");
        newFile.delete();
        newFile.createNewFile();
        bw = new BufferedWriter(new FileWriter(newFile, true));
        bw.write("URL,Residence Indicator\n");

        for (OutgoingUrl outgoingUrl : crawlStat.getOutgoingUrls()) {
            String residenceIndicator = outgoingUrl.residence ? "OK" : "N_OK";
            bw.append(outgoingUrl.url + "," + residenceIndicator + "\n");
        }
        bw.close();
    }

    private static void writeReport(CrawlStat crawlStat) throws Exception {
        logger.info("Generating Reports");

        HashMap<Integer, Integer> statusCodes = new HashMap<Integer, Integer>();

        for(FetchUrl fetchUrl : crawlStat.getFetchedUrls())
        {
            if (statusCodes.containsKey(fetchUrl.statusCode))
            {
                statusCodes.put(fetchUrl.statusCode, statusCodes.get(fetchUrl.statusCode) + 1);
            }
            else
            {
                statusCodes.put(fetchUrl.statusCode, 1);
            }
        }

        HashMap<String, Integer> contentTypes = new HashMap<String, Integer>();
        int oneK = 0, tenK = 0, hundredK = 0, oneM = 0, other = 0;

        for(DownloadFileUrl downloadFileUrl : crawlStat.getDownloadedUrls())
        {
            if (contentTypes.containsKey(downloadFileUrl.contentType))
            {
                contentTypes.put(downloadFileUrl.contentType, contentTypes.get(downloadFileUrl.contentType) + 1);
            }
            else
            {
                contentTypes.put(downloadFileUrl.contentType, 1);
            }

            if (downloadFileUrl.size < 1024)
            {
                oneK ++;
            }
            else if (downloadFileUrl.size < 10240)
            {
                tenK ++;
            }
            else if (downloadFileUrl.size < 102400)
            {
                hundredK ++;
            }
            else if (downloadFileUrl.size < 1024 * 1024)
            {
                oneM ++;
            }
            else
            {
                other ++;
            }

        }

        File newFile = new File("CrawlReport_" + "FoxNews" + ".txt");
        newFile.delete();
        newFile.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, true));
        bw.write("Name: Name\nUSC ID: ID\n\n");
        bw.write("News site crawled: FoxNews.com" + "\nNumber of threads: 7" + "\n\n");

        bw.write("Fetch Statistics: \n================\n");
        bw.write("# fetches attempted: " + crawlStat.getTotalFetch() + "\n# fetches succeeded: " + crawlStat.getTotalSuccessFetch() +
                "\n# fetches failed or aborted: " + crawlStat.getTotalFailedFetch() + "\n\n");

        bw.write("Outgoing URLs: \n================\n");
        bw.write("Total URLs extracted: " + crawlStat.getTotalUrl() + "\n# unique URLs extracted " + crawlStat.getUniqueUrl() +
                "\n# unique URLs within News Site " + crawlStat.getUniqueUrlWithin() + "\n# unique URLs outside News Site " + crawlStat.getUniqueOutside()
                + "\n\n");

        bw.write("Status Codes: \n================\n");
        statusCodes.keySet().forEach(key -> {
            try {
                bw.write(key + ": " + statusCodes.get(key) + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        bw.write("\n\n");

        bw.write("File Sizes:\n================\n");
        bw.write("< 1KB: "+ oneK + "\n");
        bw.write("1KB ~ <10KB: "+ tenK + "\n");
        bw.write("10KB ~ <100KB: "+ hundredK + "\n");
        bw.write("100KB ~ <1MB: "+ oneM + "\n");
        bw.write(">= 1MB: "+ other + "\n\n");

        bw.write("Content Types:\n================\n");

        contentTypes.keySet().forEach(key -> {
            try {
                bw.write(key + ": " + contentTypes.get(key) + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        bw.close();


        for(int key: statusCodes.keySet())
        {
            System.out.println(key + " " + statusCodes.get(key));
        }
    }
}