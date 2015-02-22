/**
 * Licensed under the CC-GNU Lesser General Public License, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/LGPL/2.1/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// LongLexTo: Tokenizing Thai texts using Longest Matching Approach
//   Note: Types: 0=unknown  1=known  2=ambiguous  3=English/digits  4=special characters
//
// Public methods: 
//   1) public LongLexTo(File dictFile);	//Constructor with a dictionary file
//   2) public void addDict(File dictFile);     //Add dictionary (e.g., unknown-word file)
//   3) public void wordInstance(String text);  //Word tokenization
//   4) public void lineInstance(String text);  //Line-break tokenization 
//   4) public Vector getIndexList();
//   5) Iterator's public methods: hasNext, first, next
//
// Author: Choochart Haruechaiyasak
// Last update: 28 March 2006

import java.io.*;
import java.util.*;

public class LongLexTo {

	//Private variables
	private Trie dict;               //For storing words from dictionary
	private LongParseTree ptree;     //Parsing tree (for Thai words)

	//Returned variables
	private Vector indexList;  //List of word index positions
	private Vector lineList;   //List of line index positions
	private Vector typeList;   //List of word types (for word only)
	private Vector typeListPOS;   //List of word POS types (for word only)
	private Vector typeListStockPOS; //List of word StockPOS types (for word only)
	private Iterator iter;     //Iterator for indexList OR lineList (depends on the call)
	

	/*******************************************************************/
	/*********************** Return index list *************************/
	/*******************************************************************/
	public Vector getIndexList() {
		return indexList; }

	/*******************************************************************/
	/*********************** Return type list *************************/
	/*******************************************************************/
	public Vector getTypeList() {
		return typeList; }
	public Vector getTypeListPOS() {
		return typeListPOS; }
	public Vector getTypeListStockPOS() {
		return typeListStockPOS; }
	/*******************************************************************/
	/******************** Iterator for index list **********************/
	/*******************************************************************/
	//Return iterator's hasNext for index list 
	public boolean hasNext() {
		if(!iter.hasNext())
			return false;
		return true;
	}

	//Return iterator's first index
	public int first() {
		return 0;
	}

	//Return iterator's next index
	public int next() {
		return((Integer)iter.next()).intValue();
	}

	/*******************************************************************/
	/********************** Constructor (default) **********************/
	/*******************************************************************/
	public LongLexTo() throws IOException {

		dict=new Trie();
		File dictFile=new File("lexitron.txt");
		if(dictFile.exists())
			addDict(dictFile);
		else
			System.out.println(" !!! Error: Missing default dictionary file, lexitron.txt");
		indexList=new Vector();
		lineList=new Vector();
		typeList=new Vector();
		typeListPOS=new Vector();
		ptree=new LongParseTree(dict, indexList, typeList);
	} //Constructor

	/*******************************************************************/
	/************** Constructor (passing dictionary file ) *************/
	/*******************************************************************/
	public LongLexTo(File dictFile) throws IOException {

		dict=new Trie();
		if(dictFile.exists())
			addDict(dictFile);
		else
			System.out.println(" !!! Error: The dictionary file is not found, " + dictFile.getName());
		indexList=new Vector();
		lineList=new Vector();
		typeList=new Vector();
		typeListPOS=new Vector();
		typeListStockPOS=new Vector();
		//		ptree=new LongParseTree(dict, indexList, typeList);
		ptree=new LongParseTree(dict, indexList, typeList,typeListPOS, typeListStockPOS);
	} //Constructor

	/*******************************************************************/
	/**************************** addDict ******************************/
	/*******************************************************************/
	public void addDict(File dictFile) throws IOException {

		//Read words from dictionary
		String[] wordlist = new String[3];
		String line;
		int index;

		//		FileReader fr = new FileReader(dictFile);
		//		BufferedReader br = new BufferedReader(fr);
		FileInputStream fr = new FileInputStream(dictFile);
		InputStreamReader isr = new InputStreamReader(fr,"Utf-8");
		BufferedReader br=new BufferedReader(isr);

		while((line=br.readLine())!=null) {
			line=line.trim();
			
			
			//			System.out.println(word2);
			if(line.length()>0)
				wordlist=line.split(" ");
				try{
					dict.add(wordlist[0],POSToInt(wordlist[1]),StockPOSToInt(wordlist[2]));
				}catch(Exception e){
					dict.add(wordlist[0],POSToInt(wordlist[1]),100);
				}
				//				System.out.println(line);
				
			//				dict.add(word,word2);
			//				dict.add(line);
		}
	} //addDict

	/****************************************************************/
	/************************** wordInstance ************************/
	/****************************************************************/
	public void wordInstance(String text) {

		indexList.clear();
		typeList.clear();  
		typeListPOS.clear();
		typeListStockPOS.clear();   
		int pos, index;
		String word;
		boolean found;
		char ch;

		pos=0;
		while(pos<text.length()) {

			//Check for special characters and English words/numbers
			ch=text.charAt(pos);

			//English
			if(((ch>='A')&&(ch<='Z'))||((ch>='a')&&(ch<='z'))) {
				while((pos<text.length())&&(((ch>='A')&&(ch<='Z'))||((ch>='a')&&(ch<='z'))))
					ch=text.charAt(pos++);
				if(pos<text.length())
					pos--;
				indexList.addElement(new Integer(pos));
				typeList.addElement(new Integer(3));
				typeListPOS.addElement(new Integer(10));
				typeListStockPOS.addElement(new Integer(100));
			}
			//Digits
			else if(((ch>='0')&&(ch<='9'))||((ch>='�')&&(ch<='�'))) {
				while((pos<text.length())&&(((ch>='0')&&(ch<='9'))||((ch>='�')&&(ch<='�'))||(ch==',')||(ch=='.')))
					ch=text.charAt(pos++);
				if(pos<text.length())
					pos--;
				indexList.addElement(new Integer(pos));
				typeList.addElement(new Integer(3));
				typeListPOS.addElement(new Integer(10));
				typeListStockPOS.addElement(new Integer(100));
			}
			//Special characters
			else if((ch<='~')||(ch=='�')||(ch=='�')||(ch=='�')||(ch=='�')||(ch==',')) {
				pos++;
				indexList.addElement(new Integer(pos));
				typeList.addElement(new Integer(4));
				typeListPOS.addElement(new Integer(10));
				typeListStockPOS.addElement(new Integer(100));
			}
			//Thai word (known/unknown/ambiguous)
			else
				pos=ptree.parseWordInstance(pos, text);
		} //While all text length
		iter=indexList.iterator();
	} //wordInstance

	/****************************************************************/
	/************************** lineInstance ************************/
	/****************************************************************/
	public void lineInstance(String text) {

		int windowSize=10; //for detecting parentheses, quotes
		int curType, nextType, tempType, curIndex, nextIndex, tempIndex;
		lineList.clear(); 
		wordInstance(text); //?? why replete
		int i;
		for(i=0; i<typeList.size()-1; i++) {
			curType=((Integer)typeList.elementAt(i)).intValue();
			curIndex=((Integer)indexList.elementAt(i)).intValue();

			if((curType==3)||(curType==4)) {
				//Parenthesese
				if((curType==4)&&(text.charAt(curIndex-1)=='(')) {
					int pos=i+1;
					while((pos<typeList.size())&&(pos<i+windowSize)) {
						tempType=((Integer)typeList.elementAt(pos)).intValue();
						tempIndex=((Integer)indexList.elementAt(pos++)).intValue();  
						if((tempType==4)&&(text.charAt(tempIndex-1)==')')) {
							lineList.addElement(new Integer(tempIndex));
							i=pos-1;
							break;
						}
					}
				}    	  
				//Single quote
				else if((curType==4)&&(text.charAt(curIndex-1)=='\'')) {
					int pos=i+1;
					while((pos<typeList.size())&&(pos<i+windowSize)) {
						tempType=((Integer)typeList.elementAt(pos)).intValue();
						tempIndex=((Integer)indexList.elementAt(pos++)).intValue();  
						if((tempType==4)&&(text.charAt(tempIndex-1)=='\'')) {
							lineList.addElement(new Integer(tempIndex));
							i=pos-1;
							break;
						}
					} 	    
				}
				//Double quote
				else if((curType==4)&&(text.charAt(curIndex-1)=='\"')) {
					int pos=i+1;
					while((pos<typeList.size())&&(pos<i+windowSize)) {
						tempType=((Integer)typeList.elementAt(pos)).intValue();
						tempIndex=((Integer)indexList.elementAt(pos++)).intValue();  
						if((tempType==4)&&(text.charAt(tempIndex-1)=='\"')) {
							lineList.addElement(new Integer(tempIndex));
							i=pos-1;
							break;
						}
					} 	    
				}    	  
				else
					lineList.addElement(new Integer(curIndex));
			}
			else {
				nextType=((Integer)typeList.elementAt(i+1)).intValue();
				nextIndex=((Integer)indexList.elementAt(i+1)).intValue();
				if((nextType==3)||
						((nextType==4)&&((text.charAt(nextIndex-1)==' ')||(text.charAt(nextIndex-1)=='\"')||
								(text.charAt(nextIndex-1)=='(')||(text.charAt(nextIndex-1)=='\''))))
					lineList.addElement(new Integer(((Integer)indexList.elementAt(i)).intValue()));
				else if((curType==1)&&(nextType!=0)&&(nextType!=4))
					lineList.addElement(new Integer(((Integer)indexList.elementAt(i)).intValue()));
			}
		}
		if(i<typeList.size())
			lineList.addElement(new Integer(((Integer)indexList.elementAt(indexList.size()-1)).intValue()));
		iter=lineList.iterator(); 
	} //lineInstance
	// turn pos word to int
	// POSToInt
	public int POSToInt(String s){
		if(s.compareTo("UNK")==0){
			return 10;
		}
		if(s.compareTo("NCMN")==0){
			return 11;
		}
		if(s.compareTo("NPRP")==0){
			return 12;
		}
		if(s.compareTo("VACT")==0){
			return 13;
		}
		return 10;
	}//POSToInt
	// StockPOSToInt
	public int StockPOSToInt(String s){
		if(s.compareTo("S")==0){//sell
			return 101;
		}
		if(s.compareTo("B")==0){//buy
			return 102;
		}
		if(s.compareTo("H")==0){//hold
			return 103;
		}
		if(s.compareTo("P")==0){//positive
			return 104;
		}
		if(s.compareTo("N")==0){//negative
			return 105;
		}
		if(s.compareTo("NP")==0){//not pos not neg
			return 106;
		}
		if(s.compareTo("AM")==0){//ambiguous
			return 107;
		}
		if(s.compareTo("FT")==0){//future's word
			return 108;
		}
		if(s.compareTo("PT")==0){//past's word
			return 109;
		}
		if(s.compareTo("CT")==0){//curent time's word
			return 110;
		}
		if(s.compareTo("INV")==0){//invert ex. but
			return 111;
		}
		if(s.compareTo("CNT")==0){//connecting (need words following)
			return 112;
		}
		return 100;
	}//StockPOSToInt
	/****************************************************************/
	/*************************** Demo *******************************/
	/****************************************************************/
	public static void main(String[] args) throws IOException {
		LongLexTo tokenizer=new LongLexTo(new File(".\\dict\\lexitron-tagged-utf2.txt"));
//		File unknownFile=new File("unknown.txt");
//		if(unknownFile.exists())
//			tokenizer.addDict(unknownFile);
		tokenizer.addDict(new File(".\\dict\\englishdict.txt"));
		tokenizer.addDict(new File(".\\dict\\thaidict.txt"));
		Vector typeList;
		Vector typeListPOS;
		Vector typeListStockPOS;
		String text="", line, inFileName, outFileName;
		char ch;
		int begin, end, type,typePOS,typeStockPOS; 

		File inFile, outFile;
		//		FileReader fr;
		FileInputStream fr;
		BufferedReader br;
		FileWriter fw;


		BufferedReader streamReader = new BufferedReader(new InputStreamReader(System.in)); 

		System.out.println("\n\n*******************************");
		System.out.println("*** LexTo: Lexeme Tokenizer ***");
		System.out.println("*******************************");
		//    do {      
		//Get input file name
		//      do {
		//      	System.out.print("\n >>> Enter input file ('q' to quit): ");
		//        inFileName=(streamReader.readLine()).trim();
		//        if(inFileName.equals("q"))
		//          System.exit(1);
		//        inFile=new File(System.getProperty("user.dir") + "//" + inFileName);
		//      } while(!inFile.exists());
		//      
		//Get output file name
		System.out.print(" >>> Enter output file (.html only): ");
		outFileName=(streamReader.readLine()).trim();
		outFile=new File(System.getProperty("user.dir") + "//" + outFileName);

		//		fr=new FileReader("D:\\LUNAworkspace2\\LexTo_works\\1xpdf.txt");
		//		br=new BufferedReader(fr);
		fr = new FileInputStream(new File("D:\\stockanalysis\\sample\\19_utf.txt"));
		InputStreamReader isr = new InputStreamReader(fr,"Utf-8");
		br=new BufferedReader(isr);
		fw=new FileWriter(outFile); 
		while((line=br.readLine())!=null) {
			line=line.trim();
//			while(true){
//				int charp= line.indexOf("#");
//				if(charp<0){
//					break;
//				}
//				line=line.substring(0, charp)+"้"+line.substring(charp+1);
//			}
			if(line.length()>0) {

				fw.write("<b>Text:</b> " + line);
				fw.write("<br>\n");

				fw.write("<b>Word instance:</b> ");
				tokenizer.wordInstance(line);
				typeList=tokenizer.getTypeList();
				typeListPOS=tokenizer.getTypeListPOS();
				typeListStockPOS=tokenizer.getTypeListStockPOS();
				//				System.out.println(typeList.size());
				//				System.out.println(typeListPOS.size());
				begin=tokenizer.first();
				int i=0;
				while(tokenizer.hasNext()) {
					end=tokenizer.next();
					type=((Integer)typeList.elementAt(i)).intValue();
					typePOS=((Integer)typeListPOS.elementAt(i)).intValue();
					typeStockPOS=((Integer)typeListStockPOS.elementAt(i)).intValue();
					i+=1;
					if(type==0)
						fw.write("<font color=#ff0000>(" +typePOS+")("+typeStockPOS+")"+ line.substring(begin, end) + "</font>");
					else if(type==1)
						fw.write("<font color=#00bb00>(" +typePOS+")("+typeStockPOS+")"+ line.substring(begin, end) + "</font>");
					else if(type==2)
						fw.write("<font color=#0000bb>(" +typePOS+")("+typeStockPOS+")"+ line.substring(begin, end) + "</font>");
					else if(type==3)
						fw.write("<font color=#aa00aa>(" +typePOS+")("+typeStockPOS+")"+ line.substring(begin, end) + "</font>");
					else if(type==4)
						fw.write("<font color=#00aaaa>(" +typePOS+")("+typeStockPOS+")"+ line.substring(begin, end) + "</font>");
					fw.write("<font color=#000000>|</font>");
					begin=end;
				}
				fw.write("<br>\n");

				fw.write("<b>Line instance:</b> ");
				tokenizer.lineInstance(line);    
				begin=tokenizer.first();
				while(tokenizer.hasNext()) {
					end=tokenizer.next();
					fw.write(line.substring(begin, end) + "<font color=#ff0000>|</font>");
					begin=end;
				}
				fw.write("<br><br>\n");        
			}
		} //while all line
		fw.write("<hr>");
		fw.write("<font color=#ff0000>unknown</font> | ");
		fw.write("<font color=#00bb00>known</font> | ");
		fw.write("<font color=#0000bb>ambiguous</font> | ");
		fw.write("<font color=#a00aa>English/Digits</font> | ");
		fw.write("<font color=#00aaaa>special</font>\n");
		fr.close();
		fw.close();
		System.out.println("\n *** Status: Use Web browser to view result: " + outFileName);
		//    } while(true);
	} //main
}
