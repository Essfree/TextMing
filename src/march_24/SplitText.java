package march_24;

import java.awt.print.Printable;
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
	 * @param srcPath  原始数据集的存放路径
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
//		存放清洗好的数据的文件夹，新建
		String dirTarget = srcPath+"\\..\\clean_data";
		File dirFile = new File(dirTarget);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
//		得到data文件夹下的文件列表
		File[] srcFiles = fileDir.listFiles();
//		for(int i = 0;i<srcFiles.length;i++){
//			System.out.println(srcFiles[i]);
//		}
		
		//母data文件夹中一共有多少个子文件和第一层子文件夹
		String[] stemFileNames = new String[srcFiles.length]; 
		for(int i = 0;i<srcFiles.length;i++){
			//得到标准的绝对路径
			String fileFullName = srcFiles[i].getCanonicalPath();
			//得到文件名
			String fileShortName = srcFiles[i].getName();
			StringBuffer sb = new StringBuffer();
			sb.append(dirTarget+"\\"+fileShortName);
//			如果绝对路径不是一个目录，则开始分词，如果是一个目录则迭代再进入子文件夹
			if(!new File(fileFullName).isDirectory()){
				splitToFile(fileFullName,sb.toString());
				stemFileNames[i] = sb.toString();
			}else{
				System.out.println("this is a file "+fileFullName);
				split(fileFullName);
			}
		}
 
	}
	
	 /**
	  * 分词，去停用词，输出保存，下一次用
	  * @param srcDir  原始数据路径
	  * @param targetDir  清洗数据要放的路径
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
	    
	    try{
            BufferedReader br = new BufferedReader(new FileReader(new File(srcDir)));//构造一个BufferedReader类来读取文件
            String temp = br.readLine();
			StringBuffer sb = null;
			File tarFile = new File(targetDir);
			if (tarFile.exists()) {
				tarFile.delete();
			}
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(targetDir,true),"UTF-8");
			BufferedWriter bw= new BufferedWriter(out);
			while(temp!=null){
				sb = new StringBuffer("");
            	if(!temp.equals("\t")&&!temp.equals(""))
				{
//            		这里保留了连字符，但是如果连字符后面有数字的，数字没有保留了
		        	String res[] = temp.toLowerCase().split("[^a-zA-Z-]");
					String list="";
					for(int i = 0; i < res.length; i++){  
			            if(!res[i].isEmpty()&&!stopWordSet.contains(res[i])){  
			                list +=res[i]+ " ";  
			            }  
			        }
					sb.append(list);
				}
				temp = br.readLine();
				bw.write(sb+"\n");
				bw.flush();
            }
			
			
			bw.close();
			out.close();
            br.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
}
