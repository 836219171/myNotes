package com.imooc.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 爬京东商品数据
 * @Title JDUtil
 * @author LinkedBear
 */
public class JDUtil {
    //主站
    private static String jdLink = "https://list.jd.com";
    //商品列表页（可以是分类列表页，也可以是搜索结果）
    private static String goodsPage = "/list.html?cat=670,671,672";
    //商品的ID匹配模式（纯数字，最少7位）
    private static Pattern goodsIdPattern = Pattern.compile("[\\d]{7,}");
    
    //页码计数器
    private static int pageCount = 1;
    private static int maxPageCount = 50;
    
    //爬取的单个商品map中，允许出现的最大空串数（空串太多，数据也就没意义了）
    private static int maxBlankCount = 6;
    
    //一页60个，防止犯抽抽，多给2个
    private static List<Map<String, String>> dataList = new ArrayList<>(maxPageCount * 62);
    //连接过程中可能出现连接失败的问题，会将链接暂存在这个集合中
    private static List<String> unconnenctUrlList = new ArrayList<>();
    
    
    public static void main(String[] args) throws Exception {
        //笔记本电脑的商品列表（递归调用爬取）
        catchData(jdLink + goodsPage);
        
        //连接过程中会出现一些问题，都保存在未连接成功的url列表里，需要再次连接
        saveUnconnectData();
        
        //剔除没有查到价格的商品
        filterNoPrice();
        
        //保存到Excel中
        exportData();
    }
    
    /**
     * 爬单个商品列表页
     * @author LinkedBear
     * @param url
     * @throws Exception
     */
    private static void catchData(String url) throws Exception {
        Document document = Jsoup.connect(url).get();
        //DOM取所有商品的链接
        Elements commodityLinks = document.getElementsByClass("p-img");
        for (Element element : commodityLinks) {
            Elements commodityLink = element.getElementsByTag("a");
            String href = commodityLink.attr("href");
            //爬取过程可能出现网络连接异常，做暂存处理
            try {
                saveData(href);
            } catch (Exception e) {
                System.out.println(href + "出现连接问题！");
                unconnenctUrlList.add(href);
            }
        }
        //计算页码
        if (pageCount++ >= maxPageCount) {
            return;
        }
        //取下一页的链接，递归调用
        Element nextPageA = document.getElementsByClass("pn-next").get(0);
        catchData(jdLink + nextPageA.attr("href"));
    }
    
    /**
     * 爬单个商品
     * @author LinkedBear
     * @param url
     * @throws Exception
     */
    private static void saveData(String url) throws Exception {
        String urlLocation = "http:" + url;
        //匹配商品ID
        Matcher goodsIdMatcher = goodsIdPattern.matcher(urlLocation);
        //匹配器需要先find才能group出来
        String goodsId = null;
        if (goodsIdMatcher.find()) {
            goodsId = goodsIdMatcher.group();
        }
        
        //加载商品详情页
        Document document = Jsoup.connect(urlLocation).get();
        //初始化Map，将单个商品的所有信息都存在这个map中
        Map<String, String> map = new TreeMap<>();
        //取商品全名
        Elements goodsNameElement = document.getElementsByClass("sku-name");
        for (Element element : goodsNameElement) {
            map.put("商品全名", element.text());
        }
        
        //取商品详情
        Elements parameterListElements = document.getElementsByClass("p-parameter-list");
        for (Element parameterList : parameterListElements) {
            Elements parameters = parameterList.children();
            for (Element parameter : parameters) {
                map.put(parameter.text().split("：")[0], parameter.text().split("：")[1].trim());
            }
        }
        //ajax查价格
        queryPrice(goodsId, map);
        
        //ajax查评价标签
        queryComment(goodsId, map);
        
        //存到List里
        dataList.add(map);
        
        System.out.println(dataList.size() + "\t" + map.get("价格") + "\t\t" + map.get("商品全名"));
    }
    
    /**
     * ajax获取商品的评价标签
     * @author LinkedBear
     * @param goodsId
     * @param map
     * @throws Exception
     */
    private static void queryComment(String goodsId, Map<String, String> map) throws Exception {
        URL commentUrl = new URL("https://sclub.jd.com/comment/productPageComments.action?productId=" + goodsId + "&score=0&sortType=5&page=0&pageSize=10&isShadowSku=0&fold=1");
        URLConnection commentConnection = commentUrl.openConnection();
        try {
            commentConnection.connect();
        } catch (Exception e) {
            System.out.println(map.get("商品全名") + " -- 查询评论标签失败！已放弃获取\n");
            return;
        }
        //json串是GBK编码格式
        BufferedReader br = new BufferedReader(new InputStreamReader(commentConnection.getInputStream(), "GBK"));
        String readline = null;
        StringBuilder sb = new StringBuilder();
        while ((readline = br.readLine()) != null) {
            //解析json串
            JSONObject json = JSON.parseObject(readline);
            JSONArray comments = json.getJSONArray("hotCommentTagStatistics");
            for (int i = 0; i < comments.size(); i++) {
                JSONObject commentJson = comments.getJSONObject(i);
                //取数据，字符串拼接
                String tagName = commentJson.getString("name");
                String tagCount = commentJson.getString("count");
                sb.append(tagName + "：" + tagCount + "，");
            }
        }
        //拿掉最后一个逗号
        if (sb.lastIndexOf("，") != -1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        map.put("评价标签", sb.toString());
    }

    /**
     * ajax获取价格
     * @author LinkedBear
     * @param goodsId
     * @param map
     * @throws Exception
     */
    private static void queryPrice(String goodsId, Map<String, String> map) throws Exception {
//        URL priceUrl = new URL("https://p.3.cn/prices/mgets?&skuIds=J_" + goodsId);//（可能不稳定）
        URL priceUrl = new URL("http://p.3.cn/prices/get?type=1&area=1_2805_2855&pdtk=&pduid=836516317&pdpin=&pdbp=0&skuid=J_" + goodsId);
        URLConnection priceConnection = priceUrl.openConnection();
        try {
            //伪装浏览器？！
            priceConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            priceConnection.connect();
        } catch (Exception e) {
            System.out.println(map.get("商品全名") + " -- 链接价格失败！已放弃该条数据\n");
            map.put("价格", "-1");
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(priceConnection.getInputStream()));
        String readline = null;
        while ((readline = br.readLine()) != null) {
            JSONObject priceJson = JSON.parseArray(readline).getJSONObject(0);
            map.put("价格", priceJson.get("p").toString());
            //有的商品获取的价格是-1.00，但m的价格是网页的实际显示价格，需要一个简单逻辑
            if (priceJson.getString("p").equals("-1.00") || priceJson.getString("p").equals("-1")) {
                map.put("价格", priceJson.get("m").toString());
            }
        }
    }

    /**
     * 再访问一次失败的链接
     * @author LinkedBear
     * @Time 2018年6月2日 上午10:09:29
     * @throws Exception
     */
    private static void saveUnconnectData() throws Exception {
        for (String url : unconnenctUrlList) {
            saveData(url);
        }
    }
    
    /**
     * 剔除掉没有价格的，或者空串太多的商品
     * @author LinkedBear
     */
    private static void filterNoPrice() {
        ListIterator<Map<String, String>> iterator = dataList.listIterator();
        while (iterator.hasNext()) {
            Map<String, String> map = iterator.next();
            boolean removeFlag = false;
            //检查价格是否为空/为0
            removeFlag = StringUtil.isBlank(map.getOrDefault("价格", "")) 
                    || Double.parseDouble(map.get("价格")) < 0;
            //检查数据中是否有好多空串
            int blankCount = 0;
            for (Entry<String, String> entry : map.entrySet()) {
                if (StringUtil.isBlank(entry.getValue())) {
                    blankCount++;
                }
                //空白过多，这条数据也就没意义了
                if (blankCount >= maxBlankCount) {
                    removeFlag = true;
                    break;
                }
            }
            if (removeFlag) {
                System.out.println("移除：" + map);
                iterator.remove();
            }
        }
    }

    /**
     * poi导出数据
     * @author LinkedBear
     * @Time 2018年6月2日 上午10:05:10
     * @throws Exception
     */
    private static void exportData() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("数据");
        //由于要先造表头，需要先拿一个数据出来
        XSSFRow headRow = sheet.createRow(0);
        Map<String, String> headerMap = dataList.get(0);
        int headColIndex = 0;
        for (String key : headerMap.keySet()) {
            headRow.createCell(headColIndex++).setCellValue(key);
        }
        //写正文数据
        for (int i = 0; i < dataList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            Map<String, String> map = dataList.get(i);
            //表头有多少，这里就放多少
            int size = headerMap.size();
            for (int j = 0; j < size; j++) {
                row.createCell(j).setCellValue(map.get(headRow.getCell(j).getStringCellValue()));
            }
        }
        
        workbook.write(new FileOutputStream("F:/京东笔记本数据.xlsx"));
        workbook.close();
    }
    
    
    
    @Test
    public void test() throws Exception {
        String goodsId = "7275689";
        Map<String, String> map = new HashMap<>();
        queryComment(goodsId, map);
        System.out.println(map);
    }
}
