import data.DownloadFileUrl;
import data.FetchUrl;
import data.OutgoingUrl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class CrawlStat {

    List<FetchUrl> fetchedUrls;
    List<OutgoingUrl> outgoingUrls;

    List<DownloadFileUrl> downloadedUrls;

    private int totalFetch;
    private int totalSuccessFetch;
    private int totalFailedFetch;

    private int totalUrl;
    private int uniqueUrl;
    private int uniqueUrlWithin;
    private int uniqueUrlOutside;


    CrawlStat(){
        this.fetchedUrls = new ArrayList<>();
        this.outgoingUrls = new ArrayList<>();
        this.downloadedUrls = new ArrayList<>();

        this.totalFetch = 0;
        this.totalSuccessFetch = 0;
        this.totalFailedFetch = 0;

        this.totalUrl = 0;
        this.uniqueUrl = 0;
        this.uniqueUrlWithin = 0;
        this.uniqueUrlOutside = 0;
    }

    public CrawlStat(List<FetchUrl> fetchedUrls, List<OutgoingUrl> outgoingUrls, List<DownloadFileUrl> downloadedUrls, int totalFetch, int totalSuccessFetch, int totalFailedFetch,
            int totalUrl, int uniqueUrl, int uniqueUrlWithin, int uniqueUrlOutside){
        this.fetchedUrls = fetchedUrls;
        this.outgoingUrls = outgoingUrls;
        this.downloadedUrls = downloadedUrls;
        this.totalFetch = totalFetch;
        this.totalSuccessFetch = totalSuccessFetch;
        this.totalFailedFetch = totalFailedFetch;
        this.totalUrl = totalUrl;
        this.uniqueUrl = uniqueUrl;
        this.uniqueUrlWithin = uniqueUrlWithin;
        this.uniqueUrlOutside = uniqueUrlOutside;
    }



    public int getTotalFetch() {
        return totalFetch;
    }

    public void increaseTotalFetch() {
        this.totalFetch++;
    }

    public int getTotalSuccessFetch() {
        return totalSuccessFetch;
    }

    public void increaseTotalSuccessFetch() {
        this.totalSuccessFetch++;
    }

    public int getTotalFailedFetch() {
        return totalFailedFetch;
    }
    public void increaseTotalFailedFetch() {
        this.totalFailedFetch++;
    }

    public int getTotalUrl() {
        return totalUrl;
    }

    public void increaseTotalUrl() {
        this.totalUrl++;
    }


    public int getUniqueUrl() {
        return uniqueUrl;
    }
    public void increaseUniqueUrl() {
        this.uniqueUrl++;
    }

    public int getUniqueUrlWithin() {
        return uniqueUrlWithin;
    }
    public void increaseUniqueUrlWithin() {
        this.uniqueUrlWithin++;
    }

    public int getUniqueOutside() {
        return uniqueUrlOutside;
    }
    public void increaseUniqueUrlOutside() {
        this.uniqueUrlOutside++;
    }


    public List<FetchUrl> getFetchedUrls(){
        return this.fetchedUrls;
    }
    public List<DownloadFileUrl> getDownloadedUrls(){
        return this.downloadedUrls;
    }

    public List<OutgoingUrl> getOutgoingUrls(){
        return this.outgoingUrls;
    }

    public void addFetchedUrls(String url, int statusCode)
    {
        this.fetchedUrls.add(new FetchUrl(url, statusCode));
        this.totalFetch ++;


    }

    public void addOutgoingUrl(String url, boolean residenceIndicator)
    {
        this.outgoingUrls.add(new OutgoingUrl(url, residenceIndicator));
    }

    public void addDownloadUrls(String url, int size, int outlinkCount, String contentType)
    {
        this.downloadedUrls.add(new DownloadFileUrl(url, size, outlinkCount, contentType));

    }




}