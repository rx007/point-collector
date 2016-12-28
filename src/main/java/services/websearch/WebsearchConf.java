package services.websearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebsearchConf {

    static String[] keys = new String[] {
           "山手線"
        ,  "東京"
        ,  "有楽町"
        ,  "新橋"
        ,  "浜松町"
        ,  "田町"
        ,  "品川"
        ,  "大崎"
        ,  "五反田"
        ,  "目黒"
        ,  "恵比寿"
        ,  "渋谷"
        ,  "原宿"
        ,  "代々木"
        ,  "新宿"
        ,  "新大久保"
        ,  "高田馬場"
        ,  "目白"
        ,  "池袋"
        ,  "大塚"
        ,  "巣鴨"
        ,  "駒込"
        ,  "田端"
        ,  "西日暮里"
        ,  "日暮里"
        ,  "鶯谷"
        ,  "上野"
        ,  "御徒町"
        ,  "秋葉原"
        ,  "神田"
        ,  "JR線"
        ,  "関東"
    };

    private WebsearchConf() {

    }

    public static List<String> getKeywords() {

        return new ArrayList<String>(Arrays.asList(keys));
    }
}
