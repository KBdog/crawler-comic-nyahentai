package properties;

import entity.Comic;
import okhttp3.OkHttpClient;
import parser.NyaHentaiParser;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class RunProperties {
    //nyahentai域名
//    public static String host="https://nyahentai.com";
    //nayhentai中文站域名
    public static String host="https://zha.qqhentai.com/";
    //存储目录
    public static String crawlerDirectory="D:/Comic/nyahentai/";
    //代理
    public static Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1",1080));
    //okhttp客户端
    public static OkHttpClient client=new OkHttpClient.Builder()
            .proxy(RunProperties.proxy)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5,TimeUnit.SECONDS)
            .build();
    //parser
    public static NyaHentaiParser parser=null;
    //comic
    public static Comic comic=null;
}
