package run;

import entity.Comic;
import parser.impl.NyaHentaiParserImpl;
import properties.RunProperties;
import utils.DownloadUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Starter {
    public static void main(String[] args) {
        //监控程序结束线程
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("程序结束！");
            }
        }));
        //程序开始
        RunProperties.parser=new NyaHentaiParserImpl();
        Scanner in=new Scanner(System.in);
        System.out.print("请输入漫画存放目录(若不设置默认为"+RunProperties.crawlerDirectory+"):");
        String directory=in.nextLine();
        if(directory!=null&&!directory.equals("")){
            RunProperties.crawlerDirectory=directory;
        }
        System.out.println("存放目录:"+RunProperties.crawlerDirectory);
        System.out.println("---------------------------------------");
        System.out.print("请输入漫画关键字进行搜索:");
        String keyword=in.nextLine()
                .replaceAll("/"," ")
                .replaceAll("\"","")
                .replaceAll("\\(","")
                .replaceAll("\\)","")
                .replaceAll("\\[","")
                .replaceAll("\\]","");
        List<Comic> comicList=null;
        if(keyword!=null&&!keyword.equals("")){
            System.out.println("关键字:"+keyword);
            System.out.println("关键字长度:"+keyword.length());
            comicList=RunProperties.parser.getComicList(keyword, 1);
        }
        //如果为空则重试十次
        if(comicList==null){
            for(int i=0;i<10;i++){
                comicList=RunProperties.parser.getComicList(keyword,1);
                if(comicList!=null){
                    break;
                }
            }
        }
        System.out.println("---------------------------------------");
        if(comicList!=null) {
            int i = 1;
            for (Comic comic : comicList) {
                if (comic != null) {
                    System.out.println(i + "-" + comic.getComicName());
                    i++;
                }
            }
            System.out.println("---------------------------------------");
            System.out.print("请输入您要爬的漫画序号:");
            RunProperties.comic=comicList.get(Integer.parseInt(in.nextLine())-1);
            System.out.println("漫画名:"+RunProperties.comic.getComicName());
            List<String> picFormList = RunProperties.parser.getPicFormList(RunProperties.comic.getComicUrl());
            List<String> picList=null;
            if(picFormList!=null){
                System.out.println("共:"+picFormList.size()+"页");
                System.out.println("---------------------------------------");
                picList=new ArrayList<>();
                picList=Collections.synchronizedList(picList);
                //建立线程池
                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
                CompletionService<String> cs = new ExecutorCompletionService<String>(fixedThreadPool);
                //提交任务
                for(int k=0;k<picFormList.size();k++){
                    final String picForm=picFormList.get(k);
                    final int z=k+1;
                    cs.submit(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return RunProperties.parser.getPicUrl(picForm)+"___"+z;
                        }
                    });
                }
                int count=0;
                //监控线程同时添加页码
                for(int s=0;s<picFormList.size();s++){
                    try {
                        //结果
                        String result=cs.take().get();
                        if(result!=null){
                            //图片url
                            String picUrl=result.split("___")[0];
                            //漫画名
                            String comicName=RunProperties.comic.getComicName()
                                    .replaceAll("\\?","？")
                                    .replaceAll("/"," ")
                                    .replaceAll("\\|"," ")
                                    .replaceAll("\"","");
                            //页码
                            String picPage=result.split("___")[1];
                            //缝合
                            String requestPic=comicName+"___"+picUrl+"___"+picPage;
                            picList.add(requestPic);
                            count++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(count==picFormList.size()){
                            System.out.println("--排队线程池已关闭!");
                            fixedThreadPool.shutdown();
                        }
                    }
                }
                System.out.println("---------------------------------------");
                //下载
                new DownloadUtil().downloadImg(picList,5,1000);
            }else {
                System.out.println("漫画图片列表为空！");
            }
        }else {
            System.out.println("漫画不存在!");
        }
        in.close();
    }
}
