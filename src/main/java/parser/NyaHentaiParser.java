package parser;

import entity.Comic;

import java.util.List;

public interface NyaHentaiParser {
    //获取漫画列表
    List<Comic>getComicList(String keyword,Integer pageNum);
    //获取图片div列表
    List<String>getPicFormList(String comicUrl);
    //获取真实图片url
    String getPicUrl(String picFormUrl);
}
