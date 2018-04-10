package march_24;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Tfidf {
	public static List<String> fileList = new ArrayList<String>();
	public static List<String> alllist = new ArrayList<String>();
	//
	public static Map<String, Integer> dictMap = new HashMap<String, Integer>();
	public static int top = 10;
	/**
	 * 
	 * @param splitDir
	 * @return
	 */
	public Map<String, Map<String, Double>> CalTfIdf(String splitDir) {
		File cleanFile = new File(splitDir);
//		clean_data文件夹里的文件列表
		File[] cleanList = cleanFile.listFiles();
		System.out.println(cleanList.length);
//		clean_data里文件的个数：6个
		int totalFile = cleanList.length;
		
		Map<String,Map<String, Double>> tfIdfMap = new HashMap<String, Map<String,Double>>(); 
		for (int i = 0; i < totalFile; i++) {
			String tString = cleanList[i].getName();
			int calcu = 0;
			boolean flag = true;
			if(flag == true){
				try{
					BufferedReader br = new BufferedReader(new FileReader(cleanFile));//构造一个BufferedReader类来读取文件
		            String temp =br.readLine();
		            while(temp!=null){
		            	
		            }
				}catch(Exception e){
					e.getMessage();
				}
			}
		}
		
		
		return null;
	}
}
