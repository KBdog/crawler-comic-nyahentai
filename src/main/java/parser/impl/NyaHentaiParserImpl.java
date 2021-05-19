package parser.impl;

import entity.Comic;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.NyaHentaiParser;
import properties.RunProperties;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NyaHentaiParserImpl implements NyaHentaiParser {
    @Override
    public List<Comic> getComicList(String keyword,Integer pageNum) {
        String url= RunProperties.host+ "/search/q_"+keyword+"/page/"+pageNum;
        Request request=new Request.Builder().url(url).build();
        Response response=null;
        List<Comic> comicList=null;
        Comic comic=null;
        try {
            response=RunProperties.client.newCall(request).execute();
            String pageHtml = response.body().string();
            //jsoup解析
            Document doc = Jsoup.parse(pageHtml);
            Elements comics = doc.getElementsByClass("gallery");
            if(comics.size()>0){
                comicList=new ArrayList<>();
                for(Element element:comics){
                    String comicName=element.text();
                    String comicUrl=RunProperties.host+element.child(0).attr("href");
                    comic=new Comic();
                    comic.setComicName(comicName);
                    comic.setComicUrl(comicUrl);
                    comicList.add(comic);
                }
            }
            return comicList;
        }catch (SSLHandshakeException e){
            System.out.println("TCP连接握手失败:"+url);
            return getComicList(keyword,pageNum);
        } catch (IOException e) {
            return getComicList(keyword,pageNum);
        }
    }

    @Override
    public List<String> getPicFormList(String comicUrl) {
        Request request=new Request.Builder().url(comicUrl).build();
        Response response=null;
        List<String>picFormList=null;
        try {
            response=RunProperties.client.newCall(request).execute();
            String pageHtml = response.body().string();
            //jsoup解析
            Document doc = Jsoup.parse(pageHtml);
            Elements picListDiv = doc.getElementsByClass("thumb-container");
            if(picListDiv.size()>0){
                picFormList=new ArrayList<>();
                for(Element pic:picListDiv){
                    String picForm=RunProperties.host+pic.child(0).attr("href");
                    picFormList.add(picForm);
                }
            }
            return picFormList;
        }catch (SSLHandshakeException e){
            System.out.println("TCP连接握手失败:"+comicUrl);
            return getPicFormList(comicUrl);
        } catch (IOException e) {
            return getPicFormList(comicUrl);
        }
    }

    @Override
    public String getPicUrl(String picFormUrl) {
        //请求
        Request request=new Request.Builder().url(picFormUrl).build();
        //响应
        Response response = null;
        try {
            response = RunProperties.client.newCall(request).execute();
            String pageHtml=response.body().string();
            Document doc = Jsoup.parse(pageHtml);
            Element imageUrl = doc.select("#page-container #image-container a img[src]").first();
            if(imageUrl!=null){
                String picUrl=imageUrl.attr("src");
                System.out.println("--正在把当前页加入下载队列:"+picUrl);
                return picUrl;
            }else {
                return null;
            }
        }catch (SSLHandshakeException e){
            System.out.println("TCP连接握手失败:"+picFormUrl);
            return getPicUrl(picFormUrl);
        } catch (IOException e) {
            return getPicUrl(picFormUrl);
        }
    }
}
