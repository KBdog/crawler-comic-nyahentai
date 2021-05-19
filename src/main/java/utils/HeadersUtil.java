package utils;

import java.util.HashMap;
import java.util.Map;

public class HeadersUtil {
    private static Map<String,String> header=new HashMap<>();
    public synchronized static Map<String,String> getInstance(){
        if(header.isEmpty()){
            header.put("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/88.0.4324.104 Safari/537.36");
        }
        return header;
    }
}
