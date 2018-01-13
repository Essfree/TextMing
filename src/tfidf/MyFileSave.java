package tfidf;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


public class MyFileSave {

	public void Save(String destFile,String[] a) throws IOException{
		FileOutputStream foS=null;		
		try {
				foS=new FileOutputStream(destFile,true);
				PrintWriter pWriter=new PrintWriter(foS);
				for(int i=0;i<a.length;i++){
					if(a[i].equals("")){
						continue;
					}
					pWriter.write(a[i]+"  ");
				}
				pWriter.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				foS.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}