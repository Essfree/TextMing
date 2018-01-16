package jan_13;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tfidf.MyFileReader;

public class Tfidf {
	public static List<String> fileList = new ArrayList<String>();
	public static List<String> alllist = new ArrayList<String>();
	/**
	 * allFileList  sample文件夹中所有文件名(不包括文件夹)
	 * allFile      把每篇文章当做一个元素放进list-->allFile里
	 * @param splitDir  sample文件夹
	 * @return 
	 */
	public Map<String, Map<String, Double>> CalTfIdf(String splitDir) {
		
		List allFileList = readDirs(splitDir);
		List allFile = readFile(allFileList);
		//得到所有文件个数
		int totalFile = allFileList.size();
		
//		Map<String, Integer> totalCount = new HashMap<String, Integer>();
		
		Map<String,Map<String, Double>> tfIdfMap = new HashMap<String, Map<String,Double>>(); 
		for(int i = 0;i<totalFile;i++){
			Map<String,Double> temp = new HashMap<String,Double>();
			String tString = allFile.get(i).toString();
			String[] sp = tString.split(" ");
			System.out.println(allFileList.get(i));
//			//计算某篇文档的单词和数目
//			temp = cal(sp);
//			//得到总文档的单词和单词数
//			totalCount.putAll(temp);
			temp = tfidf(sp, allFile);
			tfIdfMap.put((String) allFileList.get(i), temp);
		}
		//将得到的总文档的单词逆序排序整理并取前20%
//		Map<String, Integer> totalSort =sortMap(totalCount);
		System.out.println(tfIdfMap);
		return tfIdfMap;
	}
	
	/**
	 * 将所有<单词,单词数>按逆序排序并取前20
	 * @param totalCount
	 * @return
	 
	private Map<String, Integer> sortMap(Map<String, Integer> totalCount) {
		// 对HashMap中的 value 进行倒序排序  
				List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(totalCount.entrySet());
				Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> obj1 , Map.Entry<String, Integer> obj2) {
						return obj2.getValue() - obj1.getValue();
					} 
				});
				System.out.println(infoIds);
				
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				for(int i = 0;i<infoIds.size()*0.2;i++){
					map.put(infoIds.get(i).getKey(), infoIds.get(i).getValue());
				}
		return map;
	}
*/
	/**
	 * 计算文档中的单词及其数目，用哈希表存储
	 * @param sp
	 * @return
	 
	private HashMap<String, Integer> cal(String[] sp) {
		HashMap<String, Integer> tfMap = new HashMap<String, Integer>();
		for(String s : sp){
			if(!s.equals(" ")){
				if(tfMap.get(s)!=null){
					tfMap.put(s,tfMap.get(s)+1);
				}else{
					tfMap.put(s,1);
				}
			}
		}
		return tfMap;
	}

*/
	private Map<String, Double> tfidf(String[] sp, List<String> allFile) {
		Map<String, Double> tfMap = tf(sp);
//		System.out.println(tfMap);
		Map<String,Double> idfMap = idf(tfMap,allFile);
//		System.out.println(idfMap);
		Map<String, Double> tfidfMap = tfIdf(tfMap,idfMap);
//		System.out.println(tfidfMap);
		return tfidfMap;
	}

    private Map<String, Double> tfIdf(Map<String, Double> tfMap,Map<String, Double> idfMap) {
		List <Map.Entry<String, Double>> tfList = new ArrayList<Map.Entry<String,Double>>(tfMap.entrySet());
		List <Map.Entry<String, Double>> idfList = new ArrayList<Map.Entry<String,Double>>(idfMap.entrySet());
		HashMap<String, Double> tfidfMap = new HashMap<String, Double>();
		for (int i = 0; i < tfList.size(); i++) {
			tfidfMap.put(tfList.get(i).getKey(),((tfList.get(i).getValue())*(idfList.get(i).getValue())));	
		}
		return tfidfMap;
	}


	private Map<String, Double> idf(Map<String, Double> tfMap, List<String> allFile) {
		int fileCount = allFile.size();
		List <Map.Entry<String, Double>> tfList = new ArrayList<Map.Entry<String,Double>>(tfMap.entrySet());
		Map<String, Double> idfMap = new HashMap<String, Double>();
//		System.out.println(tfList);
		for(int i=0;i<tfList.size();i++){
			int count = 0;
			for(String s: allFile){
				if(s.contains(tfList.get(i).getKey())){
					count++;
				}
			}
			idfMap.put(tfList.get(i).getKey(), (Math.log((double)fileCount/(count+1))));
		}
		return idfMap;
	}


	/**
     * 取前20个单词
     * @param TextContent
     * @return HashMap<String, Double> tfMap  <单词，单词的tf值>
     */
	private HashMap<String, Double> tf(String[] TextContent) {
		HashMap<String, Integer> tfMap = new HashMap<String, Integer>();
		for(String s : TextContent){
			if(!s.equals(" ")){
				if(tfMap.get(s)!=null){
					tfMap.put(s,tfMap.get(s)+1);
				}else{
					tfMap.put(s,1);
				}
			}
		}
		// 对HashMap中的 value 进行倒序排序  
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(tfMap.entrySet());
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
		int number = (int) (infoIds.size()*0.2)+1;
		int top = 20;
//				这里number要加一
		if(infoIds.size()>=20){
			for (int i = 0; i < top; i++) { 
		    	map.put(infoIds.get(i).getKey(), (double)infoIds.get(i).getValue()/count);
//				    	System.out.print(infoIds.get(i));
			}
		}else{
			for (int i = 0; i < number; i++) { 
		    	map.put(infoIds.get(i).getKey(), (double)infoIds.get(i).getValue()/count);
//				    	System.out.print(infoIds.get(i));
			}
		}
		return map;
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
		return alllist;
	}



	/**
	 * 递归获得指定文件夹中的所有文件
	 * @return fileList
	 * @param  filepath
	 */
	public List<String> readDirs(String filepath) {
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

}
