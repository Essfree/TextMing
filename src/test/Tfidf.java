package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tfidf.MyFileReader;

public class Tfidf {
	
	public static List<String> fileList = new ArrayList<String>();
	public static List<String> alllist = new ArrayList<String>();
	
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
