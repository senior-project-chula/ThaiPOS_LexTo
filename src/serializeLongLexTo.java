//technique from http://www.mkyong.com/java/how-to-write-an-object-to-file-in-java/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class serializeLongLexTo {
	LongLexTo tokenizer = null;
	public serializeLongLexTo(String dictpath) throws IOException {
		tokenizer = new LongLexTo(new File(dictpath));
	}
	public LongLexTo getLongLexTo(){
		return tokenizer;
	}
	public void addDict(String dictpath){
		try {
			tokenizer.addDict(new File(dictpath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void save(String savepath){
		try{

			FileOutputStream fout = new FileOutputStream(savepath);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(tokenizer);
			oos.close();
			System.out.println("Done");

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static LongLexTo deserialzeLongLexTo(String savepath){

		LongLexTo intokenizer;

		try{

			FileInputStream fin = new FileInputStream(savepath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			intokenizer = (LongLexTo) ois.readObject();
			ois.close();

			return intokenizer;

		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		} 
	}
}