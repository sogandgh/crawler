import data.DownloadFileUrl;
import data.FetchUrl;
import data.OutgoingUrl;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
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

        List<Object> allCrawlData = crawl();
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

        return new CrawlStat(fetchedUrls, outgoingUrls, downloadedUrls, totalFetch, totalSuccessFetch, totalFailedFetch, totalUrl, uniqueUrl,
                uniqueUrlWithin, uniqueUrlOutside);

    }

    private static List<Object> crawl() throws Exception {
        String crawlStorageFolder = "data";
        int numberOfCrawlers = 7;
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(16);
        config.setMaxPagesToFetch(20000);
        config.setPolitenessDelay(200);
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        controller.addSeed("https://www.foxnews.com");
        controller.start(MyCrawler.class, numberOfCrawlers);
        return controller.getCrawlersLocalData();
    }

    private static void writeCsv(CrawlStat crawlStat) throws Exception {
        logger.info("Generating CSV files");

        File file = new File("fetch_FoxNews.cvs");
        FileWriter writer = new FileWriter(file, true);
        writer.append("URL, Status\n");
        for (FetchUrl fetchUrl : crawlStat.getFetchedUrls()) {
            writer.append(fetchUrl.url).append(",").append(String.valueOf(fetchUrl.status)).append("\n");
        }
        writer.close();

        file = new File("visit_FoxNews.csv");
        writer = new FileWriter(file, true);
        writer.write("URL, Size(Bytes), # of Outlinks, Content-Type\n");

        for (DownloadFileUrl downloadFileUrl : crawlStat.getDownloadedUrls()) {
            writer.append(downloadFileUrl.url).append(",").append(String.valueOf(downloadFileUrl.size)).append(",")
                    .append(String.valueOf(downloadFileUrl.outlinkCount)).append(",").append(downloadFileUrl.contentType).append("\n");
        }
        writer.close();

        file = new File("urls_FoxNews.csv");
        writer = new FileWriter(file, true);
        writer.write("URL, URL Type\n");
        for (OutgoingUrl outgoingUrl : crawlStat.getOutgoingUrls()) {
            String residenceIndicator = outgoingUrl.residence ? "OK" : "N_OK";
            writer.append(outgoingUrl.url).append(",").append(residenceIndicator).append("\n");
        }
        writer.close();
    }

    private static void writeReport(CrawlStat crawlStat) throws Exception {
        logger.info("Generating Reports");

        HashMap<Integer, Integer> statusMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> fileSizeMap = new HashMap<Integer, Integer>();

                Map.of(1, 0 , 2, 0, 3, 0,4, 0, 5, 0);

        for(FetchUrl fetchUrl : crawlStat.getFetchedUrls())
        {
            statusMap.merge(fetchUrl.status, 1, Integer::sum);
        }

        HashMap<String, Integer> contentTypes = new HashMap<String, Integer>();
        for(DownloadFileUrl downloadFileUrl : crawlStat.getDownloadedUrls())
        {

            contentTypes.merge(downloadFileUrl.contentType, 1, Integer::sum);

            if (downloadFileUrl.size < 1024)
            {
                fileSizeMap.merge(1, 1, Integer::sum);
            }
            else if (downloadFileUrl.size < 10240)
            {
                fileSizeMap.merge(2, 1, Integer::sum);
            }
            else if (downloadFileUrl.size < 102400)
            {
                fileSizeMap.merge(3, 1, Integer::sum);
            }
            else if (downloadFileUrl.size < 1048576)
            {
                fileSizeMap.merge(4, 1, Integer::sum);
            }
            else
            {
                fileSizeMap.merge(5, 1, Integer::sum);
            }

        }

        File csvFile = new File("CrawlReport_FoxNews.txt");
        FileWriter writer = new FileWriter(csvFile, true);
        writer.write("Name: Sogand  Ghods\n");
        writer.write("USC ID: 2525215149\n\n");

        writer.write("News site crawled: FoxNews.com" + "\nNumber of threads: 7");
        writer.write("Number of threads: 7\n\n");

        writer.write("Fetch Statistics: \n");
        writer.write("================\n");

        writer.write("# fetches attempted: " + crawlStat.getTotalFetch() + "\n" );
        writer.write("# fetches succeeded: " + crawlStat.getTotalSuccessFetch() + "\n");
        writer.write( "# fetches failed or aborted: " + crawlStat.getTotalFailedFetch()+ "\n\n");


        writer.write("Outgoing URLs: \n");
        writer.write("================\n");

        writer.write("Total URLs extracted: " + crawlStat.getTotalUrl() + "\n");
        writer.write("# unique URLs extracted " + crawlStat.getUniqueUrl() +"\n");
        writer.write("# unique URLs extracted within News Site " + crawlStat.getUniqueUrlWithin() +"\n");
        writer.write("# unique URLs extracted outside News Site " + crawlStat.getUniqueOutside() +"\n\n");

        writer.write("Status Codes: \n");
        writer.write("================\n");

        statusMap.keySet().forEach(key -> {
            try {
                writer.write(key + ": " + statusMap.get(key) + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        writer.write("\n\nFile Sizes:\n");
        writer.write("================\n");

        writer.write("< 1KB: "+ fileSizeMap.get(0) + "\n");
        writer.write("1KB ~ <10KB: "+ fileSizeMap.get(1) + "\n");
        writer.write("10KB ~ <100KB: "+ fileSizeMap.get(2) + "\n");
        writer.write("100KB ~ <1MB: "+ fileSizeMap.get(3) + "\n");
        writer.write(">= 1MB: "+ fileSizeMap.get(4) + "\n\n");

        writer.write("Content Types:\n");
        writer.write("================\n");

        contentTypes.keySet().forEach(key -> {
            try {
                writer.write(key + ": " + contentTypes.get(key) + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        writer.close();
    }
}