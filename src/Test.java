import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;
public class Test {
	public static void main(String[] args) throws IOException {
		int version = 1;
		try{
			version = Integer.parseInt(args[0]);
		}catch(Exception e){
			System.out.println("input version not available: use default version");
			System.out.println("default version = "+version);
		}
		// version=0 -> show color (easy for human)
		// version=1 -> not show color (easy for next process)
		
		//create Tokenizer and add the dictionary
		LongLexTo tokenizer=new LongLexTo(new File(".\\dict\\lexitron-tagged-utf_stock.txt"));
		tokenizer.addDict(new File(".\\dict\\stockenglishdict.txt"));
		tokenizer.addDict(new File(".\\dict\\englishdict.txt"));
				
		//get input file
		String inputpath =  ".\\sample\\19_utf.txt";
		try{
			inputpath = args[1];
		}catch(Exception e){
			System.out.println("input file not available: use default input");
		}
		FileInputStream fr;
		BufferedReader br;
		fr = new FileInputStream(new File(inputpath));
		InputStreamReader isr = new InputStreamReader(fr,"Utf-8");
		br=new BufferedReader(isr);
		
		//prepare output file
		File outFile;
		FileWriter fw;
		outFile=new File(inputpath+"_word.html");
		fw=new FileWriter(outFile);
//		
//		BufferedReader streamReader = new BufferedReader(new InputStreamReader(System.in)); 
		//declair variables for output
		Vector typeList; //keep searching result 1=found -1=notfound 0=not word just prefix
		Vector typeListPOS;//keep POS form orchid
		Vector typeListStockPOS;//keep POS specialize for Stock research
		int begin, end, type,typePOS,typeStockPOS;
		String StypePOS,StypeStockPOS;
		
		String line;
		fw.write("<html>");
		fw.write("<body>");
		while((line=br.readLine())!=null) {
			line=line.trim();
			if(line.length()>0) {
				fw.write("<line>");
				fw.write("<full>"+line+"</full>");
				fw.write("<br/>\n");
				fw.write("<words>");
				tokenizer.wordInstance(line);
				typeList=tokenizer.getTypeList();
				typeListPOS=tokenizer.getTypeListPOS();
				typeListStockPOS=tokenizer.getTypeListStockPOS();
				begin=tokenizer.first();
				int i=0;
				if(version==0) fw.write("<font color=#000000>|</font>");
				while(tokenizer.hasNext()) {
					end=tokenizer.next();
					type=((Integer)typeList.elementAt(i)).intValue();
					typePOS=((Integer)typeListPOS.elementAt(i)).intValue();
					typeStockPOS=((Integer)typeListStockPOS.elementAt(i)).intValue();
					StypePOS=LongLexTo.IntToStockPOS(typePOS);
					StypeStockPOS=LongLexTo.IntToStockPOS(typeStockPOS);
					i+=1;
					if(version==0){
						if(type==0||type==-1)
							fw.write("<font color=#ff0000><" +StypePOS+"><"+StypeStockPOS+">"+ line.substring(begin, end) + "</" +StypeStockPOS+"></"+StypePOS+"></font>");
						else if(type==1)
							fw.write("<font color=#00bb00><" +StypePOS+"><"+StypeStockPOS+">"+ line.substring(begin, end) + "</" +StypeStockPOS+"></"+StypePOS+"></font>");
						else if(type==2)
							fw.write("<font color=#0000bb><" +StypePOS+"><"+StypeStockPOS+">"+ line.substring(begin, end) + "</" +StypeStockPOS+"></"+StypePOS+"></font>");
						else if(type==3)
							fw.write("<font color=#aa00aa><" +StypePOS+"><"+StypeStockPOS+">"+ line.substring(begin, end) + "</" +StypeStockPOS+"></"+StypePOS+"></font>");
						else if(type==4)
							fw.write("<font color=#00aaaa><" +StypePOS+"><"+StypeStockPOS+">"+ line.substring(begin, end) + "</" +StypeStockPOS+"></"+StypePOS+"></font>");
						fw.write("<font color=#000000>|</font>");
					}else{
						fw.write("<" +StypePOS+"><"+StypeStockPOS+">"+ line.substring(begin, end) + "</" +StypeStockPOS+"></"+StypePOS+">");
					}
					begin=end;
				}
				fw.write("</words><br/>");
				fw.write("</line>");
				fw.write("<br/>\n");   
			}
		} //while all line
		fw.write("</body>");
		fw.write("</html>");
		if(version==0){
			fw.write("<hr>");
			fw.write("<font color=#ff0000>unknown</font> | ");
			fw.write("<font color=#00bb00>known</font> | ");
			fw.write("<font color=#0000bb>ambiguous</font> | ");
			fw.write("<font color=#a00aa>Digits</font> | ");
			fw.write("<font color=#00aaaa>special</font>\n");
		}
		fr.close();
		fw.close();
		System.out.println("*** Status: Use Web browser to view result: " + outFile.getPath());
		//    } while(true);
	} //main
}
