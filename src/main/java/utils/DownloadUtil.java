package utils;

import properties.RunProperties;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

//下载工具
public class DownloadUtil {
    public void downloadImg(final List<String> imgList, final int threadsize, final long sleeptime){
        int count=0;
        int size = imgList.size();
        List<String> list = Collections.synchronizedList(imgList);
        //建立线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadsize);
        // CompletionService对线程池返回值进行监控为线程池中Task的执行结果服务的，
        // 即为Executor中Task返回Future而服务的。CompletionService的实现目标是任务先完成可优先获取到，
        // 即结果按照完成先后顺序排序。
        CompletionService<String> cs = new ExecutorCompletionService<String>(fixedThreadPool);
        for (String url : list) {
            final String url1 = url;
            cs.submit(new Callable<String>() {
                public String call() throws Exception {
                    try {
                        Thread.sleep(sleeptime);
                        //下载图片
                        return down(url1);
                    } catch (InterruptedException e) {
                        System.out.println("线程异常");
                        return "error_" + "url1";
                    }
                }
            });
        }
        //监控下载数
        for (String url : list) {
            try {
                String a = cs.take().get();
                if (a != null) {
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (count == size) {
                    System.out.println("---"+count + "/" + size);
                    System.out.println("over");
                    System.out.println("下载线程池已关闭!");
                    fixedThreadPool.shutdown();
                } else {
                    System.out.println("---"+count + "/" + size);
                }
            }
        }
    }

    protected String down(String url) {
        HttpURLConnection uc =null;
        InputStream is=null;
        BufferedInputStream bis =null;
        BufferedOutputStream bos =null;
        FileOutputStream fos =null;
        try {
            url = url.trim();
            //存放爬虫的目录
            File mangas_directory = new File(RunProperties.crawlerDirectory);
            if (!mangas_directory.exists() && !mangas_directory.isDirectory()) {
                mangas_directory.mkdir();
            }
            //存放漫画类别目录
            String manga_directory_string=RunProperties.crawlerDirectory+url.split("___")[0];
            File manga_directory = new File(manga_directory_string);
            if (!manga_directory.exists() && !manga_directory.isDirectory()) {
                manga_directory.mkdir();
            }
            //存放漫画单话每页图片
            String img_string=manga_directory_string+"/"+(Integer.parseInt(url.split("___")[2]))+".jpg";
            File img=new File(img_string);
            if(!img.exists()){
                img.createNewFile();
            }

            //字节输出流
            fos = new FileOutputStream(img);
            URL temp;
            String imgurl = url.split("___")[1];
            temp = new URL(imgurl.trim());
            uc = (HttpURLConnection) temp.openConnection(RunProperties.proxy);
            uc.setConnectTimeout(3000);
            uc.setReadTimeout(3000);
            //添加请求头
            Map<String, String> headers = HeadersUtil.getInstance();
            for(Map.Entry<String,String>header:headers.entrySet()){
                uc.setRequestProperty(header.getKey(),header.getValue());
            }
            is = uc.getInputStream();
            //为字节输入流加缓冲
            bis = new BufferedInputStream(is);
            //为字节输出流加缓冲
            bos = new BufferedOutputStream(fos);
            int length;
            byte[] bytes = new byte[1024 * 20];
            while ((length = bis.read(bytes, 0, bytes.length)) != -1) {
                bos.write(bytes, 0, length);
            }
            return "success_" + "url1";
        }catch(FileNotFoundException e){
            System.out.println("文件失效_缺页_"+url.split("___")[0]+"_"+url.split("___")[1]);
            return "error_" + "url1";
        }catch (SSLHandshakeException e){
            //出现异常通常是网络问题,此时递归下载把未成功下载的页数重新下载直到成功为止
            System.out.println("TCP连接握手失败_缺页_"+url.split("___")[0]+"_"+url.split("___")[1]);
            return down(url);
        } catch (SocketTimeoutException e){
            //出现异常通常是网络问题,此时递归下载把未成功下载的页数重新下载直到成功为止
            System.out.println("连接超时_缺页_"+url.split("___")[0]+"_"+url.split("___")[1]);
            return down(url);
        } catch (Exception e) {
            System.out.println("未知错误_缺页_"+url.split("___")[0]+"_"+url.split("___")[1]);
            return down(url);
        } finally {
            try {
                if(bos!=null){
                    bos.close();
                }
                if(fos!=null){
                    fos.close();
                }
                if(bis!=null){
                    bis.close();
                }
                if(is!=null){
                    is.close();
                }
                if(uc!=null){
                    uc.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
