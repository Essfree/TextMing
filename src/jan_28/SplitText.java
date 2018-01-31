package jan_28;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

public class SplitText {

	public static String stopWordTable = "E:\\MUC\\textmining\\stopwords.txt";
	/**
	 * 
	 * @param srcPath
	 * @throws IOException 
	 */
	public void split(String srcPath) throws IOException {
		File fileDir = new File(srcPath);
		//查找路径，如果路径出错的话输出文件不存在
		if(!fileDir.exists()){  
            System.out.println("File not exist:" + srcPath);  
            return;  
        }
		String subStrPath = srcPath.substring(srcPath.lastIndexOf('\\'));
//		System.out.println(subStrPath);
		//新建目标文件Sample,与测试文件夹test在同一目录下
		String dirTarget = srcPath + "\\..\\Sample"+subStrPath;
		File dirFile = new File(dirTarget);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		File[] srcFiles = fileDir.listFiles();
//		略迷
//		System.out.println(srcPath.length());
		//母文件夹中一共有多少个子文件和第一层子文件夹
		String[] stemFileNames = new String[srcFiles.length]; 
		for(int i = 0;i<srcFiles.length;i++){
			//得到标准的绝对路径
			String fileFullName = srcFiles[i].getCanonicalPath();
			//得到文件名
			String fileShortName = srcFiles[i].getName();
			StringBuffer sb = new StringBuffer();
			sb.append(dirTarget+"\\"+fileShortName);
			if(!new File(fileFullName).isDirectory()){
				splitToFile(fileFullName,sb.toString());
				stemFileNames[i] = sb.toString();
			}else{
				System.out.println("this is a file "+fileFullName);
				split(fileFullName);
			}
		}
//		if(stemFileNames.length > 0 && stemFileNames[0] != null){  
//            Stemmer.main(stemFileNames);  
//        }  
	}
	
	/**
	 * 分词并按原本的文件夹存储在Sample文件中
	 * @param dirTarget
	 * @param string
	 * @throws IOException 
	 */
	private void splitToFile(String srcDir, String targetDir) throws IOException {
		BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(stopWordTable ))));       
		//用来存放停用词的集合 
		Set stopWordSet = new HashSet<String>(); 
		String stopWord;
		//初始化停用词集   
	    for(;(stopWord = StopWordFileBr.readLine()) != null;){     
			stopWordSet.add(stopWord);   
		}
	    int count = 1;
	    
	    try{
            BufferedReader br = new BufferedReader(new FileReader(new File(srcDir)));//构造一个BufferedReader类来读取文件
            String temp =br.readLine();
			StringBuffer sb = null;
			
			while(temp!=null){
				sb = new StringBuffer("");
            	String target = targetDir.substring(0, targetDir.lastIndexOf("."))+(count++);
            	while(temp.equals("")){
            		temp = br.readLine();
            	}
            	while(!temp.equals("\t")&&!temp.equals(""))
				{
		        	String res[] = temp.toLowerCase().split("[^a-zA-Z]");
					String list="";
					for(int i = 0; i < res.length; i++){  
			            if(!res[i].isEmpty()&&!stopWordSet.contains(res[i])){  
			                list +=res[i]+ " ";  
			            }  
			        }
					sb.append(list);
					temp = br.readLine();
					if (temp==null) {
						break;
					}
				}
	            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(target+".txt"),"UTF-8");
				BufferedWriter bw= new BufferedWriter(out);
				bw.write(sb+"");
				bw.close();
				out.close();
				temp = br.readLine();
            }
            br.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
}
