package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

//io字符流转字符缓冲流
public class IOStringBufferUtil {
    public static StringBuffer getStringBuffer(Reader reader){
        BufferedReader br=null;
        StringBuffer sb=null;
        try {
            br=new BufferedReader(reader);
            sb=new StringBuffer();
            int length=0;
            char[]cache=new char[1024];
            while((length=br.read(cache,0,cache.length))!=-1){
                sb.append(cache,0,length);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(br!=null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb;
        }
    }
}
