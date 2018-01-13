package tfidf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tfidf.Wordsplit.CLibrary;


public class Tfidf {

/**
 * 计算tf
 * 前10个关键词，并且把关键词和TF存到map里
 * @param String[] test
 * @return List<Entry<关键词, TF>>
 */
	public List<Entry<String, Double>> Tf(String[] test){		
		HashMap<String, Integer> hmp = new HashMap<String, Integer>();
		for(String s : test){
			if(!s.equals("")){		
				if(hmp.get(s)!=null){
					hmp.put(s, hmp.get(s)+1);
				}else{
					hmp.put(s,1);
				}
			}
		}
		// 对HashMap中的 value 进行倒序排序  
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(hmp.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> obj1 , Map.Entry<String, Integer> obj2) {
				return obj2.getValue() - obj1.getValue();
			} 
		});
		//得到文章单词总数
		int count = 0;
		for(int i = 0;i<infoIds.size();i++){
			count = count+infoIds.get(i).getValue();
		}
		//求得词频并将该词及其词频存到hashmap中
		HashMap<String, Double> map = new HashMap<String, Double>();
		int m = 20;
		String[] s = new String[m];
		int[]k = new int[m];
		double tf[] = new double[m];
		
		for (int i = 0; i < m; i++) {  
		    s[i] = infoIds.get(i).getKey();  
		    k[i] = infoIds.get(i).getValue();
		    tf[i] = (double)k[i]/count;
	    	map.put(s[i], tf[i]);
//	    	System.out.print(infoIds.get(i));
		} 
//		System.out.println();
		List<Map.Entry<String, Double>> tflist = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
//		System.out.println("tflist - "+tflist);
		return(tflist);
	}
	
	public static List<String> fileList = new ArrayList<String>();
	public static List<String> alllist = new ArrayList<String>();

/**
 * 递归获得指定文件夹中的所有文件
 * @return fileList
 * @param  filepath
 */
	public List<String> readDirs(String filepath){
		File file = new File(filepath);
		if(!file.isDirectory()){
			System.out.println("这不是文件夹名 : "+file.getAbsolutePath());
		}else if(file.isDirectory()){
			String[] filelist = file.list();
			for(int i = 0;i<filelist.length;i++){
				File readFile = new File(filepath + File.separator + filelist[i]);
				if(!readFile.isDirectory()){
					fileList.add(readFile.getAbsolutePath());
				}else if(readFile.isDirectory()){
					readDirs(filepath+File.separator+filelist[i]);
				}
			
			}
		}
		return fileList;
	}

/**
 * 把每篇文章当做一个元素放进list里
 * @param fileList
 * @return List
 * 文档总篇数就是list.size
 */
	public List<String> readFile(List<String> fileList){
		String snppi=null;
		
		for(String file : fileList)
		{
			snppi= MyFileReader.read(file);
			snppi = snppi.replace("　　","");
			alllist.add(snppi);
		}
//		System.out.println(slist.get(1));
//		System.out.println(slist.size());
		return alllist;
	}
	
/***
 * 计算idf
 * 
 * @param 有关键词和TF的list
 * @param 放文章的list
 * @return dlist,存有相对应的关键词和idf
 * 
 */
	public List<Entry<String, Double>> Idf(List<Map.Entry<String, Double>> tflist,List<String> alist){
		HashMap<String, Integer> cmp = new HashMap<String, Integer>();
		HashMap<String, Double> cnt = new HashMap<String, Double>();
		
		for(String s: alist){
			for(int i=0;i<tflist.size();i++){
				if(s.contains(tflist.get(i).getKey())){
					if(cmp.get(tflist.get(i).getKey())!=null){
						cmp.put(tflist.get(i).getKey(), cmp.get(tflist.get(i).getKey())+1);
					}else{
						cmp.put(tflist.get(i).getKey(),1);
					}
				}
			}
		}
		List<Map.Entry<String, Integer>> cnlist = new ArrayList<Map.Entry<String, Integer>>(cmp.entrySet());
		for(int i=0;i<cnlist.size();i++){
			cnt.put(cnlist.get(i).getKey(), (double)(Math.log((alist.size()/(cnlist.get(i).getValue()+1)))));
		}
		List<Map.Entry<String, Double>> dlist = new ArrayList<Map.Entry<String, Double>>(cnt.entrySet());
//		System.out.println("idflist - "+dlist);
		return dlist;
	}

	public HashMap<String, Double> tf_idf(List<Entry<String, Double>> tflist,List<Map.Entry<String, Double>> idflist){
		HashMap<String, Double> hp = new HashMap<String, Double>();
		for (int i = 0; i < tflist.size(); i++) {
			hp.put(tflist.get(i).getKey(),(double)((tflist.get(i).getValue())*(idflist.get(i).getValue())));	
		}
//		List<Entry<String, Double>> tfidfList = new ArrayList<Map.Entry<String,Double>>(hp.entrySet());
//		System.out.println("tfidfList - "+tfidfList);
		return hp;
	}
	

}
