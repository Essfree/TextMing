package march;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class MyFileReader{
	public static String read(String filePath) {
		String result = null;
		try {
			String encoding="gbk";
			File file=new File(filePath);
			if(file.isFile() && file.exists()){ //
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				
				result = "";
				while((lineTxt = bufferedReader.readLine()) != null){
                  //System.out.println(lineTxt);
					result += lineTxt;
				}
				
				read.close();
			}else{
				System.out.println("error");
			}
		} catch (Exception e) {
			System.out.println("error1");
			e.printStackTrace();
		}
//		result = result.replaceAll("\\d+", "");
		result = result.replaceAll("\\p{P}","");
		result = result.replaceAll(" +"," ");
//		result = result+"\t";
		return result;
	}
}