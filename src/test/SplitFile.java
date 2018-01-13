package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;


public class SplitFile {
	
	public static int count = 1;
	public static String stopWordTable = "E:\\MUC\\textmining\\stopwords.txt";
	public static int Top = 5;
	public Map<String, Map<String, Double>> sortFile(String srcPath) throws IOException{
		File fileDir = new File(srcPath);
		if(!fileDir.exists()){  
            System.out.println("File not exist:" + srcPath);  
            return null;  
        }
		//新建目标文件Sample
		String dirTarget = "E:\\MUC\\textmining\\Sample";
		File dirFile = new File(dirTarget);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		File[] srcFiles = fileDir.listFiles();
		//母文件夹中一共有多少个子文件和第一层子文件夹
		for(int i = 0;i<srcFiles.length;i++){
			//得到标准的绝对路径
			String fileFullName = srcFiles[i].getCanonicalPath();
			//得到文件名
			String fileShortName = srcFiles[i].getName();
			if(!new File(fileFullName).isDirectory()){
				splitToFile(fileFullName,dirTarget,fileShortName);
			}else{
				System.out.println("this is a file "+fileFullName);
				sortFile(fileFullName);
				
			}
		}
		
		//sample文件夹里有分好词的txt文件若干个
		Tfidf tI = new Tfidf();
		List fileList = tI.readDirs(dirTarget);
		List allFile = tI.readFile(fileList);
		List<String[]> conList = new ArrayList<String[]>();
		for (int i = 0; i < allFile.size(); i++) {
			String temp = allFile.get(i).toString();
			String[] sp = temp.split(" ");
			conList.add(sp);
		}
		
//		算tf-idf     
		Map<String, Map<String, Double>> tfIdfMap = tfidf(fileList,conList,allFile);
		System.out.println("TFIDF");
		System.out.println(tfIdfMap);
		System.out.println("-----------------");
		return tfIdfMap;
	}


	/**
	 * 
	 * @param fileList  所有文档的List
	 * @param conList   文本内容的list,<[a,b,c],[d,e,f...]>
	 * @param allFile 
	 * @return 
	 */
	public Map<String, Map<String, Double>> tfidf(List fileList, List<String[]> conList, List allFile) {
		
		//TF
		Map<String, Map<String, Double>> tfMap = tf(fileList, conList);
		System.out.println("TF");
		System.out.println(tfMap);
		System.out.println("-----------------");
		//IDF
		Map<String, Map<String, Double>> idfMap= idf(fileList,tfMap,allFile);
		System.out.println("IDF");
		System.out.println(idfMap);
		System.out.println("-----------------");
		//TF-IDF
		Map<String, Map<String, Double>> tfIdfMap = new HashMap<String, Map<String,Double>>();
		for(int i = 0;i<tfMap.size();i++){
			List<Map.Entry<String, Double>> tfList = new ArrayList<Map.Entry<String, Double>>(tfMap.get(fileList.get(i)).entrySet());
			List<Map.Entry<String, Double>> idfList = new ArrayList<Map.Entry<String, Double>>(idfMap.get(fileList.get(i)).entrySet());
			tfIdfMap.put((String) fileList.get(i),calTfIdf(tfList,idfList));
		}
		return tfIdfMap;
	}

	public HashMap<String, Double> calTfIdf(List<Entry<String, Double>> tfList,List<Entry<String, Double>> idfList) {
		HashMap<String, Double> hp = new HashMap<String, Double>();
		for (int i = 0; i < tfList.size(); i++) {
			hp.put(tfList.get(i).getKey(),(double)((tfList.get(i).getValue())*(idfList.get(i).getValue())));	
		}		
		return hp;	
	}


	public Map<String, Map<String, Double>> idf(List fileList, Map<String, Map<String, Double>> tfMap, List<String> allFile) {
		Map<String, Map<String, Double>> idfMap = new HashMap<String, Map<String, Double>>();
		for(int i = 0;i<tfMap.size();i++){
			List<Map.Entry<String, Double>> mapList = new ArrayList<Map.Entry<String, Double>>(tfMap.get(fileList.get(i)).entrySet());
			idfMap.put((String) fileList.get(i),calIdf(mapList,allFile));
		}
		return idfMap;
	}


	public HashMap<String, Double> calIdf(List<Entry<String, Double>> mapList,
			List<String> allFile) {
		HashMap<String, Integer> cmp = new HashMap<String, Integer>();
		HashMap<String, Double> cnt = new HashMap<String, Double>();
		for(String s: allFile){
			for(int i=0;i<mapList.size();i++){
				if(s.contains(mapList.get(i).getKey())){
					if(cmp.get(mapList.get(i).getKey())!=null){
						cmp.put(mapList.get(i).getKey(), cmp.get(mapList.get(i).getKey())+1);
					}else{
						cmp.put(mapList.get(i).getKey(),1);
					}
				}
			}
		}
		List<Map.Entry<String, Integer>> cnlist = new ArrayList<Map.Entry<String, Integer>>(cmp.entrySet());
		for(int i=0;i<cnlist.size();i++){
			cnt.put(cnlist.get(i).getKey(), (double)(Math.log((allFile.size()/(cnlist.get(i).getValue()+1)))));
		}
		return cnt;
	}


	public Map<String, Map<String, Double>> tf(List fileList, List<String[]> conList){
		Map<String, Map<String, Integer>> tfTtlMap = new HashMap<String, Map<String,Integer>>();
		for(int i = 0;i<fileList.size();i++){
			HashMap<String, Integer> tfMap = new HashMap<String, Integer>();
			for(String s : conList.get(i)){
				if(!s.equals(" ")){
					if(tfMap.get(s)!=null){
						tfMap.put(s,tfMap.get(s)+1);
					}else{
						tfMap.put(s,1);
					}
				}
			}
//				得到哈希map，所有的东西，以<文档名,<单词,单词数>>的形式存储
			tfTtlMap.put((String) fileList.get(i), tfMap);
		}
		
//		单词总数
		int totalWords = 0;
		Map<String, Map<String, Double>> tfAllMap = new HashMap<String, Map<String,Double>>();
		for(int i = 0;i<tfTtlMap.size();i++){
			List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(tfTtlMap.get(fileList.get(i)).entrySet());
				totalWords += infoIds.size(); 
		}
		
		for(int i = 0;i<tfTtlMap.size();i++){
			List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(tfTtlMap.get(fileList.get(i)).entrySet());
			Map<String, Integer> map = new HashMap<String, Integer>();
			Map<String, Double> map2 = new HashMap<String, Double>();

			for(int j = 0;j<infoIds.size();j++){
				map.put(infoIds.get(j).getKey(), infoIds.get(j).getValue());
			}
			List<Entry<String, Integer>> sortTf = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
			Collections.sort(sortTf, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> obj1 , Map.Entry<String, Integer> obj2) {
					return obj2.getValue() - obj1.getValue();
				} 
			});
			for (int j = 0; j < Top; j++) {
				map2.put(sortTf.get(j).getKey(), ((double)sortTf.get(j).getValue()/totalWords));
			}
			tfAllMap.put((String) fileList.get(i),map2);
		}
		return tfAllMap;
	}
	




	/**①先词法分析，去停用词
	 * ②把所给的文档按篇分开，分成n份保存在dirTarget里面
	 * 俩个搞定
	 * 词根还原先放一放
	 * @param fileFullName
	 * @param dirTarget
	 * @throws IOException
	 */
	public void splitToFile(String fileFullName, String dirTarget,String fileShortName) throws IOException {
//		File dirFile = new File(fileFullName);
//		File targetFile = new File(dirTarget);
	    //词法分析和去停用词,保存
	    stopSplit(fileFullName, dirTarget,fileShortName); 
	}
	
	/**
	 * 词法分析和去停用词
	 * @param srcFile
	 * @param targetFile
	 * @param fileShortName 
	 * @param stopWordSet
	 * @throws IOException 
	 */
	public void stopSplit(String srcFile,String targetFile, String fileShortName) throws IOException{
		BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(stopWordTable ))));       
		//用来存放停用词的集合 
		Set stopWordSet = new HashSet<String>(); 
		fileShortName = fileShortName.substring(0,fileShortName.length()-4);
	    String stopWord;
		//初始化停用词集   
	    for(;(stopWord = StopWordFileBr.readLine()) != null;){     
			stopWordSet.add(stopWord);   
		}
	    StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(srcFile)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
            	String res[] = s.toLowerCase().split("[^a-zA-Z]");
				String list="";
				for(int i = 0; i < res.length; i++){  
		            if(!res[i].isEmpty()&&!stopWordSet.contains(res[i])){  
		                list +=res[i]+ " ";  
		            }  
		        }
            	result.append(list);
            }
            br.close();
            String[] w = result.toString().split(" ");
           
	        if(w.length>Top){  
		        //write
				OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(targetFile+"\\"+fileShortName+"_"+count+".txt"),"UTF-8");
				BufferedWriter bw= new BufferedWriter(out);
				bw.write(result+"");
				bw.close();
				out.close();
	        }
			count++;
        }catch(Exception e){
            e.printStackTrace();
        }


	}
	
	
}
