package com.workhub.z.servicechat.config;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.workhub.z.servicechat.VO.GroupInfoVO;
import com.workhub.z.servicechat.entity.ZzDictionaryWords;

import java.util.*;
import java.util.stream.Collectors;

/**
*@Description: 通用方法
*@Author: 忠
*@date: 2019/5/14
*/
public class common {

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
     * 涉密检索(字符串对比，请满足字符串对比规则如1和11应写成01和11)
     * @param txt
     * @param zzDictionaryWordsList
     * @return
     */
    public static String stringSearch(String txt, List<ZzDictionaryWords> zzDictionaryWordsList) {
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

    public static void main(String[] args){
        List<GroupInfoVO> list = new ArrayList<GroupInfoVO>();
        GroupInfoVO groupInfoVO = new GroupInfoVO();
        groupInfoVO.setId("1");
        list.add(groupInfoVO);
        groupInfoVO = new GroupInfoVO();
        groupInfoVO.setId("2");
        list.add(groupInfoVO);
        groupInfoVO = new GroupInfoVO();
        groupInfoVO.setId("4");
        list.add(groupInfoVO);
        groupInfoVO = new GroupInfoVO();
        groupInfoVO.setId("3");
        list.add(groupInfoVO);
        System.out.println(JSONObject.toJSON(list));
        groupInfoVO = new GroupInfoVO();
        groupInfoVO.setId("0");
        list.add(1,groupInfoVO);
        List<GroupInfoVO> list1 = new ArrayList<GroupInfoVO>();
        list1.add(new GroupInfoVO());
        list1.add(new GroupInfoVO());
        list1.add(new GroupInfoVO());
        list1.add(new GroupInfoVO());
        list1.add(new GroupInfoVO());
        System.out.println(JSONObject.toJSON(list));
        List<GroupInfoVO> result = list.stream().filter(listf -> listf.getId()!=null).sorted((b, a) -> Integer.parseInt(a.getId()) - Integer.parseInt(b.getId())).collect(Collectors.toList());
        result.addAll(list1);
        result.stream().forEach(as ->System.out.println(as.getId()));

        List<GroupInfoVO> collect = list.stream().filter(li -> li.getId().equals("1")).collect(Collectors.toList());
        System.out.println(collect);
//        List<ZzDictionaryWords> zzDictionaryWordsList = new ArrayList<ZzDictionaryWords>();
//        ZzDictionaryWords zzDictionaryWords = new ZzDictionaryWords();
//        zzDictionaryWords.setWordName("机密");
//        zzDictionaryWords.setWordCode("1");
//        zzDictionaryWordsList.add(zzDictionaryWords);
//        zzDictionaryWords = new ZzDictionaryWords();
//        zzDictionaryWords.setWordName("非密");
//        zzDictionaryWords.setWordCode("2");
//        zzDictionaryWordsList.add(zzDictionaryWords);
//        zzDictionaryWords = new ZzDictionaryWords();
//        zzDictionaryWords.setWordName("秘密");
//        zzDictionaryWords.setWordCode("3");
//        zzDictionaryWordsList.add(zzDictionaryWords);
//        zzDictionaryWords = new ZzDictionaryWords();
//        zzDictionaryWords.setWordName("123");
//        zzDictionaryWords.setWordCode("11");
//        zzDictionaryWordsList.add(zzDictionaryWords);
//        System.out.println(common.stringSearch("机密非密秘密12345揭穿你2内存浓c",zzDictionaryWordsList));

//        Page<Object> pageMassage = PageHelper.startPage(1, 10);
//        pageMassage.setTotal(100);
//        System.out.println(pageMassage);
//        PageInfo<GroupInfoVO> pageInfoGroupInfo = new PageInfo<GroupInfoVO>();
//        pageInfoGroupInfo.setSize(10);
//        pageInfoGroupInfo.setPageNum(2);
//        List<GroupInfoVO> list = new ArrayList<GroupInfoVO>();
//        for (int n=0;n<100;n++)list.add(new GroupInfoVO());
////        pageInfoGroupInfo.setList(list);
//        pageInfoGroupInfo.setTotal(10);
//        System.out.println(pageInfoGroupInfo);
//        System.out.println(common.stringSearch("阿里巴巴 哥斯拉 弗兰多路 蕾米莉亚 190拿分033奥巴马金正恩机-密密级.密"));
//        System.out.println(common.convert("`0923870348934h2u20!@#$%^&*[]★()))>>PL'"));
//        System.out.println(common.convert(convert("`0923870348934h2u20!@#$%^&*[]★()))>>PL'")));
//        System.out.println(common.convert("陋醸嶀嶀T咑旛抽T彣億奮趛T蔊簇菽仮TEMD抋割DGG夑嶀騘醥欗思李Y宲宲结Z宲"));
    }

}