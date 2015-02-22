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

// Author: Choochart Haruechaiyasak
// Last update: 28 March 2006

import java.io.*;
import java.util.*;

public class LongParseTree {

	//Private variables
	private Trie dict;               //For storing words from dictionary
	private Vector indexList;        //List of index positions
	private Vector typeList;         //List of word types
	private Vector typeListPOS;         //List of word POS types
	private Vector typeListStockPOS;         //List of word POS types
	private Vector frontDepChar;     //Front dependent characters: must have front characters
	private Vector rearDepChar;      //Rear dependent characters: must have rear characters
	private Vector tonalChar;        //Tonal characters
	private Vector endingChar;       //Ending characters

	/*******************************************************************/
	/************************ Constructor ******************************/
	/*******************************************************************/
	public LongParseTree(Trie dict, Vector indexList, Vector typeList) throws IOException {

		this.dict=dict;
		this.indexList=indexList;
		this.typeList=typeList;

		frontDepChar=new Vector();
		rearDepChar=new Vector();
		tonalChar=new Vector();
		endingChar=new Vector();

		//Adding front-dependent characters
		//	    frontDepChar.addElement("�"); frontDepChar.addElement("�"); frontDepChar.addElement("�"); 
		//    frontDepChar.addElement("�"); frontDepChar.addElement("�"); frontDepChar.addElement("�"); 
		//    frontDepChar.addElement("�"); frontDepChar.addElement("�"); frontDepChar.addElement("�"); 
		//    frontDepChar.addElement("�"); frontDepChar.addElement("�"); frontDepChar.addElement("�"); 
		//    frontDepChar.addElement("�"); frontDepChar.addElement("�");
		frontDepChar.addElement("ะ"); frontDepChar.addElement("ั"); frontDepChar.addElement("า"); 
		frontDepChar.addElement("ำ"); frontDepChar.addElement("ิ"); frontDepChar.addElement("ี"); 
		frontDepChar.addElement("ึ"); frontDepChar.addElement("ื"); frontDepChar.addElement("ุ"); 
		frontDepChar.addElement("ู"); frontDepChar.addElement("ๅ"); frontDepChar.addElement("็"); 
		frontDepChar.addElement("์"); frontDepChar.addElement("ํ");


		//Adding rear-dependent characters
		//    rearDepChar.addElement("�"); rearDepChar.addElement("�"); rearDepChar.addElement("�"); 
		//    rearDepChar.addElement("�"); rearDepChar.addElement("�"); rearDepChar.addElement("�"); 
		//    rearDepChar.addElement("�"); rearDepChar.addElement("�");
		rearDepChar.addElement("ั"); rearDepChar.addElement("ื"); rearDepChar.addElement("เ"); 
		rearDepChar.addElement("แ"); rearDepChar.addElement("โ"); rearDepChar.addElement("ใ"); 
		rearDepChar.addElement("ไ"); rearDepChar.addElement("ํ");

		//Adding tonal characters
		//    tonalChar.addElement("�"); tonalChar.addElement("�"); tonalChar.addElement("�"); 
		//    tonalChar.addElement("�"); 
		tonalChar.addElement("่"); tonalChar.addElement("้"); tonalChar.addElement("๊"); 
		tonalChar.addElement("๋"); 

		//Adding ending characters
		//    endingChar.addElement("�"); endingChar.addElement("�");    
		endingChar.addElement("ๆ"); endingChar.addElement("ฯ"); 
	}
	public LongParseTree(Trie dict, Vector indexList, Vector typeList, Vector typeListPOS, Vector typeListStockPOS) throws IOException {

		this.dict=dict;
		this.indexList=indexList;
		this.typeList=typeList;
		this.typeListPOS=typeListPOS;
		this.typeListStockPOS=typeListStockPOS;

		frontDepChar=new Vector();
		rearDepChar=new Vector();
		tonalChar=new Vector();
		endingChar=new Vector();

		//Adding front-dependent characters
		//	    frontDepChar.addElement("�"); frontDepChar.addElement("�"); frontDepChar.addElement("�"); 
		//    frontDepChar.addElement("�"); frontDepChar.addElement("�"); frontDepChar.addElement("�"); 
		//    frontDepChar.addElement("�"); frontDepChar.addElement("�"); frontDepChar.addElement("�"); 
		//    frontDepChar.addElement("�"); frontDepChar.addElement("�"); frontDepChar.addElement("�"); 
		//    frontDepChar.addElement("�"); frontDepChar.addElement("�");
		frontDepChar.addElement("ะ"); frontDepChar.addElement("ั"); frontDepChar.addElement("า"); 
		frontDepChar.addElement("ำ"); frontDepChar.addElement("ิ"); frontDepChar.addElement("ี"); 
		frontDepChar.addElement("ึ"); frontDepChar.addElement("ื"); frontDepChar.addElement("ุ"); 
		frontDepChar.addElement("ู"); frontDepChar.addElement("ๅ"); frontDepChar.addElement("็"); 
		frontDepChar.addElement("์"); frontDepChar.addElement("ํ");


		//Adding rear-dependent characters
		//    rearDepChar.addElement("�"); rearDepChar.addElement("�"); rearDepChar.addElement("�"); 
		//    rearDepChar.addElement("�"); rearDepChar.addElement("�"); rearDepChar.addElement("�"); 
		//    rearDepChar.addElement("�"); rearDepChar.addElement("�");
		rearDepChar.addElement("ั"); rearDepChar.addElement("ื"); rearDepChar.addElement("เ"); 
		rearDepChar.addElement("แ"); rearDepChar.addElement("โ"); rearDepChar.addElement("ใ"); 
		rearDepChar.addElement("ไ"); rearDepChar.addElement("ํ");

		//Adding tonal characters
		//    tonalChar.addElement("�"); tonalChar.addElement("�"); tonalChar.addElement("�"); 
		//    tonalChar.addElement("�"); 
		tonalChar.addElement("่"); tonalChar.addElement("้"); tonalChar.addElement("๊"); 
		tonalChar.addElement("๋"); 

		//Adding ending characters
		//    endingChar.addElement("�"); endingChar.addElement("�");    
		endingChar.addElement("ๆ"); endingChar.addElement("ฯ");     
	}

	/****************************************************************/
	/********************** nextWordValid ***************************/
	/****************************************************************/
	private boolean nextWordValid(int beginPos, String text) {

		int pos=beginPos+1;
		int status;

		if(beginPos==text.length())
			return true;
		else if(text.charAt(beginPos)<='~')  //English alphabets/digits/special characters
			return true;
		else {
			while(pos<=text.length()) {
				status=dict.contains(text.substring(beginPos,pos));
				if(status==1)
					return true;
				else if(status==0)
					pos++;
				else
					break;
			}
		}
		return false;
	} //nextWordValid

	/****************************************************************/
	/********************** parseWordInstance ***********************/
	/****************************************************************/
	public int parseWordInstance(int beginPos, String text) {

		char prevChar='\0';      //Previous character
		int longestPos=-1;       //Longest position
		int longestStatusPOS =10;       //POS of the Longest
		int longestStatusStockPOS =100;       //StockPOS of the Longest
		int longestValidPOS =10;       //POS of the Longest valid
		int longestValidStockPOS =100;       //StockPOS of the Longest valid
		int longestValidPos=-1;  //Longest valid position
		int numValidPos=0;       //Number of longest value pos (for determining ambiguity)
		int returnPos=-1;        //Returned text position
		int pos, status;
		int[] statusarray = new int[3];

		status=1;
		statusarray[0]=0;
		statusarray[1]=10;
		statusarray[2]=100;
		numValidPos=0;
		pos=beginPos+1;
		while((pos<=text.length())&&(statusarray[0]!=-1)) {
			//			status=dict.contains(text.substring(beginPos, pos));
			statusarray=dict.containsStockPOS(text.substring(beginPos, pos));

			//Record longest so far
			if(statusarray[0]==1) {
				longestPos=pos;
				longestStatusPOS=statusarray[1];
				longestStatusStockPOS=statusarray[2];
				if(nextWordValid(pos, text)) {
					longestValidPos=pos;
					longestValidPOS=statusarray[1];
					longestValidStockPOS=statusarray[2];
					numValidPos++;
				}
			}
			pos++;
		} //while

		//--------------------------------------------------
		//For checking rear dependent character
		if(beginPos>=1)
			prevChar=text.charAt(beginPos-1);    	

		//Unknown word
		if(longestPos==-1) {
			returnPos=beginPos+1;      
			//Combine unknown segments	
			if((indexList.size()>0)&&
					(frontDepChar.contains("" + text.charAt(beginPos))||
							tonalChar.contains("" + text.charAt(beginPos))||
							rearDepChar.contains("" + prevChar)||
							(((Integer)typeList.elementAt(typeList.size()-1)).intValue()==0))) {
				indexList.setElementAt(new Integer(returnPos), indexList.size()-1);
				typeList.setElementAt(new Integer(0), typeList.size()-1);
				typeListPOS.setElementAt(new Integer(10), typeListPOS.size()-1);
				typeListStockPOS.setElementAt(new Integer(100), typeListPOS.size()-1);
			}
			else { 
				indexList.addElement(new Integer(returnPos));
				typeList.addElement(new Integer(0));
				typeListPOS.addElement(new Integer(10));
				typeListStockPOS.addElement(new Integer(100));
			}
			return returnPos;
		}
		//--------------------------------------------------
		//Known or ambiguous word
		else {
			//If there is no merging point
			if(longestValidPos==-1) {
				//Check whether front char requires rear segment
				if(rearDepChar.contains("" + prevChar)) {
					indexList.setElementAt(new Integer(longestPos), indexList.size()-1);
					typeList.setElementAt(new Integer(0), typeList.size()-1);
					typeListPOS.setElementAt(new Integer(10), typeListPOS.size()-1);
					typeListStockPOS.setElementAt(new Integer(100), typeListPOS.size()-1);
				}
				else {
					typeList.addElement(new Integer(1));
					typeListPOS.addElement(new Integer(longestStatusPOS));
					typeListStockPOS.addElement(new Integer(longestStatusStockPOS));
					indexList.addElement(new Integer(longestPos));  
				}
				return(longestPos);  //known followed by unknown: consider longestPos
			}
			else {
				//Check whether front char requires rear segment
				if(rearDepChar.contains("" + prevChar)) {
					indexList.setElementAt(new Integer(longestValidPos), indexList.size()-1);
					typeList.setElementAt(new Integer(0), typeList.size()-1);
					typeListPOS.setElementAt(new Integer(10), typeListPOS.size()-1);
					typeListStockPOS.setElementAt(new Integer(100), typeListPOS.size()-1);
				}
				else if(numValidPos==1) {
					typeList.addElement(new Integer(1)); //known
					typeListPOS.addElement(new Integer(longestValidPOS));
					typeListStockPOS.addElement(new Integer(longestValidStockPOS));
					indexList.addElement(new Integer(longestValidPos)); 
				}
				else {
					typeList.addElement(new Integer(2)); //ambiguous
					typeListPOS.addElement(new Integer(longestValidPOS));
					typeListStockPOS.addElement(new Integer(longestValidStockPOS));
					indexList.addElement(new Integer(longestValidPos));
				}
				return(longestValidPos);  
			}
		}
	} //parseWordInstance
}