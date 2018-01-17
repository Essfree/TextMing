package jan_16;
import java.io.File;
import java.io.FileInputStream;

/**
 * 读文件 去标点
 * @author 肖
 *
 */
public class MyFileReader_0{
	public static String read(String filePath) {
		String result = null;
		try {
			String encoding="gbk";
			File file=new File(filePath);
			if(file.isFile() && file.exists()){ 
				long filelength = file.length();
				byte[] bytes = new byte[(int) filelength];
				FileInputStream in = new FileInputStream(file);
				in.read(bytes);
			    in.close();
				result = new String(bytes, encoding);
			}else{
				System.out.println("error");
			}
		} catch (Exception e) {
			System.out.println("error1");
			e.printStackTrace();
		}
		result = result.replaceAll("\\p{P}","");
//		result = result.replaceAll(" +"," ");
		return result;
	}
}