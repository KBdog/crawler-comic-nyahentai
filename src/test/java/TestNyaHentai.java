import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.NyaHentaiParser;
import parser.impl.NyaHentaiParserImpl;
import properties.RunProperties;

import java.io.IOException;

//测试
public class TestNyaHentai {
    public static void main(String[] args) throws IOException {
//        getComicList();
//        getPicList();
//        getPicUrl();
        testParser();
    }

    public static void getComicList() throws IOException {
        String keyword="fate chinese";
        String urlAddress="https://nyahentai.com/search/q_"+keyword+"/page/2";
        //构建请求
        Request request=new Request.Builder().url(urlAddress).build();
        //请求执行返回
        Response response = RunProperties.client.newCall(request).execute();
        //搜索结果页面
        String searchPageHtml=response.body().string();
        //jsoup解析
        Document parse = Jsoup.parse(searchPageHtml);
        Elements comics = parse.getElementsByClass("gallery");
        for(Element comic:comics){
            System.out.println(comic.text());
            System.out.println(RunProperties.host+comic.child(0).attr("href"));
            System.out.println();
        }
    }

    public static void getPicList() throws IOException {
        String url="https://nyahentai.com/g/359321";
        //请求
        Request request=new Request.Builder().url(url).build();
        //响应
        Response response = RunProperties.client.newCall(request).execute();
        String searchPageHtml=response.body().string();
        Document parse = Jsoup.parse(searchPageHtml);
        Elements picListDiv = parse.getElementsByClass("thumb-container");
        System.out.println("共:"+picListDiv.size()+"页");
        for(Element pic:picListDiv){
            System.out.println(RunProperties.host+pic.child(0).attr("href"));
        }
    }

    public static void getPicUrl() throws IOException {
        String picUrl="https://nyahentai.com/g/359321/list/8/#pagination-page-top";
        //请求
        Request request=new Request.Builder().url(picUrl).build();
        //响应
        Response response = RunProperties.client.newCall(request).execute();
        String searchPageHtml=response.body().string();
        Document parse = Jsoup.parse(searchPageHtml);
        Element select = parse.select("#page-container #image-container a img[src]").first();
        System.out.println(select.attr("src"));

    }


    public static void testParser(){
        NyaHentaiParser parser=new NyaHentaiParserImpl();
        String picList = parser.getPicUrl("https://nyahentai.com/g/359523/list/19/#pagination-page-top");
        System.out.println(picList);
    }
}
