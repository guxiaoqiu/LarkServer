package com.workhub.z.servicechat.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.workhub.z.servicechat.entity.ZzDictionaryWords;
import com.workhub.z.servicechat.model.ContactsMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
*@Description: 通用方法
*@Author: 忠
*@date: 2019/5/14
*/
public class common {
    private static Logger log = LoggerFactory.getLogger(common.class);
//  默认图片路径
    public static final String imgUrl = "";
    /**
     * 加密解密算法 执行一次加密，两次解密
     * @param inStr 加密字符
     * @return
     */
    public static String convert(String inStr) {

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        return new String(a);
    }

    /**
     * 校验文本包含非法字符，返回所包含的非法字符以,分割没有返回空串
     * @param str 文本
     * @param index 非法字符
     * @return
     */
    public static String stringSearch(String str,String... index){
        if(null == str) throw new NullPointerException("str is null");
        if(null == index) throw new NullPointerException("index is null");
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(index).filter(indexStr -> str.contains(indexStr)).forEach(indexResult ->{
                stringBuilder.append(indexResult).append(",");
        });
        String resultStr = stringBuilder.toString();
        if (resultStr.length() == 0) return resultStr;
        return resultStr.substring(0,resultStr.length()-1);
    }

    /**
     * 校验文本包含非法字符，返回所包含的非法字符以,分割没有返回空串
     * @param str 文本
     * @return
     */
    public static String stringSearch(String str){
        if(null == str) throw new NullPointerException("str is null");
        Set<String> setIndex = new HashSet<String>();//TODO 来源
        setIndex.add("机密");
        setIndex.add("非密");
        setIndex.add("密级");
        setIndex.add("秘密");
        StringBuilder stringBuilder = new StringBuilder();
        setIndex.stream().filter(setIndexFilter -> str.contains(setIndexFilter)).forEach(setIndexResult ->{
            stringBuilder.append(setIndexResult).append(",");
        });
        String resultStr = stringBuilder.toString();
        if (resultStr.length() == 0) return resultStr;
        return resultStr.substring(0,resultStr.length()-1);
    }

    /**
     * 校验文本包含非法字符，返回所包含的非法字符以,分割没有返回空串
     * @param str 文本
     * @param setStr 非法字符
     * @return
     */
    public static String stringSearch(String str, Set<String> setStr){
        if(null == str) throw new NullPointerException("str is null");
        if(null == setStr) throw new NullPointerException("setStr is null");
        StringBuilder stringBuilder = new StringBuilder();
        setStr.stream().filter(setIndexFilter -> str.contains(setIndexFilter)).forEach(setIndexResult ->{
            stringBuilder.append(setIndexResult).append(",");
        });
        String resultStr = stringBuilder.toString();
        if (resultStr.length() == 0) return resultStr;
        return resultStr.substring(0,resultStr.length()-1);
    }

    /**
     * 涉密检索
     * @param txt
     * @param zzDictionaryWordsList
     * @return
     */
    public static String stringSearch(String txt,List<ZzDictionaryWords> zzDictionaryWordsList) {
        // TODO: 2019/5/31 获取涉密词汇列表
        if(null == txt) throw new NullPointerException("txt is null");
        if(null == zzDictionaryWordsList||zzDictionaryWordsList.isEmpty()) throw new NullPointerException("zzDictionaryWordsList is null");
        Set<String> strSet = new HashSet<String>();
        Optional<ZzDictionaryWords> max = zzDictionaryWordsList.stream()
                    .filter(setIndexFilter -> txt.contains(setIndexFilter.getWordName()))
                    .max(Comparator.comparing(zz ->Integer.parseInt(zz.getWordCode())));
        try {
            return max.get().getWordCode();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 敏感词替换
     * @param txt
     * @param zzDictionaryWordsList
     * @return
     */
    public static String sensitiveSearch(String txt, List<ZzDictionaryWords> zzDictionaryWordsList) {
        if (null == txt) throw new NullPointerException();
        zzDictionaryWordsList.stream().forEach(list ->{
            txt.replace(list.getWordName(),list.getReplaceWord());
        });
        return txt;
    }
    /**
    *@Description: 判断用户是否在线
    *@Param: 上下文ChannelContext
    *@return: boolean
    *@Author: 忠
    *@date: 2019/5/30
    */
    public static void checkUserOnline(ChannelContext channelContext,String userId){
        ChannelContext previousChannelContext = Tio.getChannelContextByBsId(channelContext.groupContext, userId);
        if (!channelContext.equals(previousChannelContext) && previousChannelContext != null) {
            previousChannelContext.setAttribute("kickOut", true); // 踢掉的标志
            Tio.unbindBsId(previousChannelContext);
            Tio.remove(previousChannelContext, "服务器断开客户端连接");
            System.out.println("踢掉 {} 已经登录的连接 {}"+ userId + previousChannelContext.getClientNode());
        }

    }
    /**
     *@Description: 判断msg数据体是否正确
     *@Param: string 接收的消息
     *@return: boolean
     *@Author: 忠
     *@date: 2019/5/30
     */
    public static boolean isJson(String text){
        return false;
    }
    /**
     *@Description: 把entity null 转换成空字符串,时间转换成当前时间，long转换成0L
     *@Param: string 接收的实体
     *@return: boolean
     *@Author: zhuqz
     *@date: 2019/06/10
     */
    public static<T> void putEntityNullToEmptyString (T enity) throws Exception{
        if(enity==null){
            return;
        }
        //遍历enity类 成员为String类型 属性为空的全部替换为“”
        Field[] fields = enity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            // 获取属性的名字
            String name = fields[i].getName();
            // 将属性的首字符大写，方便构造get，set方法
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            // 获取属性的类型
            String type = fields[i].getGenericType().toString();
            // 如果type是类类型，则前面包含"class "，后面跟类名
            if (type.equals("class java.lang.String")) {
                Method m = enity.getClass().getMethod("get" + name);
                // 调用getter方法获取属性值
                String value = (String) m.invoke(enity);
                //System.out.println("数据类型为：String");
                if (value == null) {
                    //set值
                    Class[] parameterTypes = new Class[1];
                    parameterTypes[0] = fields[i].getType();
                    m = enity.getClass().getMethod("set" + name, parameterTypes);
                    String string = new String("");
                    Object[] objects = new Object[1];
                    objects[0] = string;
                    m.invoke(enity, objects);
                }
            }
            // 如果type是类类型，则前面包含"class "，后面跟类名
            if (type.equals("class java.util.Date")) {
                Method m = enity.getClass().getMethod("get" + name);
                // 调用getter方法获取属性值
                Date value = (Date) m.invoke(enity);
                //System.out.println("数据类型为：String");
                if (value == null) {
                    //set值
                    Class[] parameterTypes = new Class[1];
                    parameterTypes[0] = fields[i].getType();
                    m = enity.getClass().getMethod("set" + name, parameterTypes);
                    Date date = new Date();
                    Object[] objects = new Object[1];
                    objects[0] = date;
                    m.invoke(enity, objects);
                }
            }
            // 如果type是类类型，则前面包含"class "，后面跟类名
            if (type.equals("class java.lang.Double")) {
                Method m = enity.getClass().getMethod("get" + name);
                // 调用getter方法获取属性值
                Double value = (Double) m.invoke(enity);
                //System.out.println("数据类型为：String");
                if (value == null) {
                    //set值
                    Class[] parameterTypes = new Class[1];
                    parameterTypes[0] = fields[i].getType();
                    m = enity.getClass().getMethod("set" + name, parameterTypes);
                    double data = 0.0;
                    Object[] objects = new Object[1];
                    objects[0] = data;
                    m.invoke(enity, objects);
                }
            }
            // 如果type是类类型，则前面包含"class "，后面跟类名
            if (type.equals("long")) {
                boolean hasFun = true;
                Method m = null;
                try{
                    m = enity.getClass().getMethod("get" + name);
                }catch (NoSuchMethodException e){//可能是序列化的long
                    hasFun=false;
                }
                if(!hasFun){
                    continue;
                }
                // 调用getter方法获取属性值
                Long value = (Long) m.invoke(enity);
                //System.out.println("数据类型为：String");
                if (value == null) {
                    //set值
                    Class[] parameterTypes = new Class[1];
                    parameterTypes[0] = fields[i].getType();
                    m = enity.getClass().getMethod("set" + name, parameterTypes);
                    long data = 0L;
                    Object[] objects = new Object[1];
                    objects[0] = data;
                    m.invoke(enity, objects);
                }
            }
        }
    }
    //获取当前日期
    public static String getCurrentDate(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar=Calendar.getInstance();
        return  format.format(calendar.getTime());
    }
    //获取上个月第一天
    public static String getBeforeMonthFirstDay(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return  format.format(calendar.getTime());
    }
    //异常信息获取
    public static String getExceptionMessage(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
    //格式化double
    public static String formatDouble2(double d) {
        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return String.valueOf(bg.doubleValue());
    }

    /**
     * 分组聚合
     */
    public static <T> List<List<T>> aggregation(List<T> list,Comparator<? super T> comparator) {
        List<List<T>> lists = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            boolean isContain = false;
            for (int j = 0; j < lists.size(); j++) {
                if (lists.get(j).size() == 0||comparator.compare(lists.get(j).get(0),list.get(i)) == 0) {
                    lists.get(j).add(list.get(i));
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                List<T> newList = new ArrayList<>();
                newList.add(list.get(i));
                lists.add(newList);
            }
        }
        return lists;
    }

    public static void aggregation1(List<ContactsMessageDto> list, Map<String, List<ContactsMessageDto>> map) {//map是用来接收分好的组的
        if (null == list) {
            return;
        }


        String key;
        List<ContactsMessageDto> listTmp;
        for (ContactsMessageDto val : list) {
            key = val.getContactsId();//按这个属性分组，map的Key
            listTmp = map.get(key);
            if (null == listTmp) {
                listTmp = new ArrayList<ContactsMessageDto>();
                map.put(key, listTmp);
            }
            listTmp.add(val);
        }
    }
    /**
     *@Description: 把返回给前端的VO 属性是字符串的且值是null的 转换成空字符串，其它类型在代码自行处理
     *@Param: vo
     *@return: void
     *@Author: zhuqz
     *@date: 2019/06/26
     */
    public static<T> void putVoNullStringToEmptyString (T vo) throws Exception{
        if(vo==null){
            return;
        }
        //遍历enity类 成员为String类型 属性为空的全部替换为“”
        Field[] fields = vo.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            // 获取属性的名字
            String name = fields[i].getName();
            // 将属性的首字符大写，方便构造get，set方法
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            // 获取属性的类型
            String type = fields[i].getGenericType().toString();
            // 如果type是类类型，则前面包含"class "，后面跟类名
            if (type.equals("class java.lang.String")) {
                Method m = vo.getClass().getMethod("get" + name);
                // 调用getter方法获取属性值
                String value = (String) m.invoke(vo);
                //System.out.println("数据类型为：String");
                if (value == null) {
                    //set值
                    Class[] parameterTypes = new Class[1];
                    parameterTypes[0] = fields[i].getType();
                    m = vo.getClass().getMethod("set" + name, parameterTypes);
                    String string = new String("");
                    Object[] objects = new Object[1];
                    objects[0] = string;
                    m.invoke(vo, objects);
                }
            }
        }
    }
    /**
     *@Description: 把返回给前端的List<VO> 属性是字符串的且值是null的 转换成空字符串，其它类型在代码自行处理
     *@Param: vo
     *@return: void
     *@Author: zhuqz
     *@date: 2019/06/26
     */
    public static<T> void putVoNullStringToEmptyString (List<T> list) throws Exception{
        if(list==null){
            return;
        }
        for(T vo:list){
            //遍历enity类 成员为String类型 属性为空的全部替换为“”
            Field[] fields = vo.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                // 获取属性的名字
                String name = fields[i].getName();
                // 将属性的首字符大写，方便构造get，set方法
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                // 获取属性的类型
                String type = fields[i].getGenericType().toString();
                // 如果type是类类型，则前面包含"class "，后面跟类名
                if (type.equals("class java.lang.String")) {
                    Method m = vo.getClass().getMethod("get" + name);
                    // 调用getter方法获取属性值
                    String value = (String) m.invoke(vo);
                    //System.out.println("数据类型为：String");
                    if (value == null) {
                        //set值
                        Class[] parameterTypes = new Class[1];
                        parameterTypes[0] = fields[i].getType();
                        m = vo.getClass().getMethod("set" + name, parameterTypes);
                        String string = new String("");
                        Object[] objects = new Object[1];
                        objects[0] = string;
                        m.invoke(vo, objects);
                    }
                }
            }
        }
    }
    /**
     *@Description: 修改json字符串的某个属性值
     *@Param: json字符串；key,如果是多级用"."拼接，key只支持String；value 修改后新的值.只支持到叶子节点，非叶子节点，没试过，有需要自行测试。
     * 例如：某个json字符串是{"toId":"GMt5H6xf","contactInfo":{"name":"8756","id":"GMt5H6xf"},"username":"myname"}，传入key: contactInfo.id, value: newValue,返回值是：{"toId":"GMt5H6xf","contactInfo":{"name":"8756","id":"newValue"},"username":"myname"}
     *@return: 修改后的json字符串
     *@Author: zhuqz
     *@date: 2019/06/28
     */
    public static String   setJsonStringKeyValue (String jsonStr,String key, Object value) throws Exception{
        if(jsonStr==null){
            return null;
        }
        Map<String,Object> jsonMap= JSON.parseObject(jsonStr, new TypeReference<Map<String, Object>>() {});
        //System.out.println("before:"+JSONObject.toJSONString(jsonMap));
        jsonMap=setMapKeyValue(jsonMap,key,value);
       // System.out.println("after:"+JSONObject.toJSONString(jsonMap));
        return JSONObject.toJSONString(jsonMap);
    }
    //递归赋值给map的key
    public static Map<String,Object> setMapKeyValue(Map<String,Object> map,String key, Object value) throws  Exception {
        String[] keyArr=key.split("\\.");

        if(keyArr.length!=1){
            String currentKey=keyArr[0];
            Map<String,Object> temp=JSONObject.parseObject(map.get(currentKey).toString());
            map.put(currentKey,setMapKeyValue(temp,key.substring(key.indexOf(".")+1),value));

        }else {
            map.put(keyArr[0],value);
        }
        JSONObject jsonObj=new JSONObject(map);
        return  map;
    }
    //测试setJsonStringKeyValue
/*
    public static void main(String[] args) {
        try {
            setJsonStringKeyValue("{\"toId\":\"GMt5H6xf\",\"atId\":[],\"contactInfo\":{\"name\":\"8756\",\"id\":\"GMt5H6xf\",\"avatar\":\"\",\"memberNum\":4,\"isGroup\":true,\"secretLevel\":30},\"id\":\"69be81ca-f633-45c0-be0c-262d14aa68e1\",\"avatar\":\"http://10.12.97.34:80/undefined\",\"time\":\"2019-06-24T08:36:10.137Z\",\"isGroup\":true,\"fromId\":\"duyukun\",\"content\":{\"extension\":\"jpg\",\"id\":\"398b8662f7694ea28264330e63e79d97\",\"type\":3,\"title\":\"jpg\",\"secretLevel\":30,\"url\":\"/api/chat/zzFileManage/GetFile?fileId=398b8662f7694ea28264330e63e79d97&t=1561193135178\"},\"username\":\"杜宇坤\"}",
            "contactInfo.memberNum",
                    12
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    /**
     *@Description: 获取json字符串的某个属性值
     *@Param: json字符串；key,如果是多级用"."拼接，key只支持String；
     * 例如：某个json字符串是{"toId":"GMt5H6xf","contactInfo":{"name":"8756","id":"GMt5H6xf"},"username":"myname"}，传入key: contactInfo.id 返回值是：GMt5H6xf
     *@return:
     *@Author: zhuqz
     *@date: 2019/07/03
     */
    public static Object   getJsonStringKeyValue (String jsonStr,String key) throws Exception{
        if(jsonStr==null){
            return null;
        }
        Map<String,Object> jsonMap= JSON.parseObject(jsonStr, new TypeReference<Map<String, Object>>() {});
        return getMapKeyValue(jsonMap,key);
    }
    //递归json的key
    public static Object getMapKeyValue(Map<String,Object> map,String key) throws  Exception {
        String[] keyArr=key.split("\\.");

        if(keyArr.length!=1){
            String currentKey=keyArr[0];
            if(map.get(currentKey)==null){
                return null;
            }
            Map<String,Object> temp=JSONObject.parseObject(map.get(currentKey).toString());
            return getMapKeyValue(temp,key.substring(key.indexOf(".")+1));

        }else {
            return map.get(keyArr[0]);
        }
        
    }
/*    public static void main(String[] args) {
        try {
            Object obj = getJsonStringKeyValue("{\"toId\":\"GMt5H6xf\",\"atId\":[],\"contactInfo\":{\"name\":\"8756\",\"id\":\"GMt5H6xf\",\"avatar\":\"\",\"memberNum\":4,\"isGroup\":true,\"secretLevel\":30,\"family\":{\"address\":\"China Shandong\"}},\"id\":\"69be81ca-f633-45c0-be0c-262d14aa68e1\",\"avatar\":\"http://10.12.97.34:80/undefined\",\"time\":\"2019-06-24T08:36:10.137Z\",\"isGroup\":true,\"fromId\":\"duyukun\",\"content\":{\"extension\":\"jpg\",\"id\":\"398b8662f7694ea28264330e63e79d97\",\"type\":3,\"title\":\"jpg\",\"secretLevel\":30,\"url\":\"/api/chat/zzFileManage/GetFile?fileId=398b8662f7694ea28264330e63e79d97&t=1561193135178\"},\"username\":\"杜宇坤\"}",
                    "contactInfo.family.2222"
            );
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    //objec转字符串 null-> ""
    public static String nulToEmptyString(Object object){
        try{
            if(object==null){
                return "";
            }
            return  object.toString();
        }catch (Exception e){
            e.printStackTrace();
            log.error(getExceptionMessage(e));
        }
        return  null;
    }
}