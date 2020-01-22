package compilerTest;
import java.util.*;
import java.lang.*;
import java.io.*;
public class cOmPiLeRbOi {	
	private String[] reservedWords=new String[8];
	private char[] reservedSymbols=new char[11];
	
	//parser support
	private List<String> list=new ArrayList<String>();
	private int index;
	private boolean error;
	private boolean endOfCode;
	private boolean insideIf;
	private boolean insideRepeat;
	//reserved words and symbols
	public void Start() 
	{
	reservedWords[0]="if";
	reservedWords[1]="then";
	reservedWords[2]="else";
	reservedWords[3]="end";
	reservedWords[4]="repeat";
	reservedWords[5]="until";
	reservedWords[6]="read";
	reservedWords[7]="write";
	
	reservedSymbols[0]='+';
	reservedSymbols[1]='-';
	reservedSymbols[2]='*';
	reservedSymbols[3]='/';
	reservedSymbols[4]='=';
	reservedSymbols[5]='<';
	reservedSymbols[6]='>';
	reservedSymbols[7]='(';
	reservedSymbols[8]=')';
	reservedSymbols[9]=';';
	reservedSymbols[10]=':'; //check after that if there is equal sign
	
	index=0;
	error=false;
	endOfCode=false;
	
	}
	
	//Scanner
	public void Tokenize() throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader("D:\\trial.txt"));	
		String word="";
		char c=(char)r.read();
		while(r.ready())
		{			
			//check if it is a comment
			if (c=='{')
			{
				word+=c;
				c=(char)r.read();
				while(c != '}')
				{
					if (c=='\n' || c=='\r')
					{
						word+= " ";
						c=(char)r.read();
					}
					else {
					word+=c;
					c=(char)r.read();
					}
				}
				if(c=='}') {
				word+=c;
				System.out.println(word + " is a Comment");
				word="";
				}
				c=(char)r.read();
				while(checkIfSeparator(c))
				{
					c=(char)r.read();
				}
			}
			if(c!= '{')
			{
				
				//checking if it is a number
				if(Character.isDigit(c))
				{
					while(Character.isDigit(c)|| c=='E' || c=='.' || c=='-' || c=='+')
					{
						word+=c;
						c=(char)r.read();						
					}
					if(word !="")
					{
						System.out.println(word + " is a Number");
						list.add("number");
						
						word="";
						while(checkIfSeparator(c))
						{
							c=(char)r.read();
						}
					}
				}
				
				//checking if it it is a keyword or an identifier 
				else if(Character.isAlphabetic(c))
				{
					while(Character.isAlphabetic(c)|| Character.isDigit(c) 
							||(!checkIfSymbol(c)&& !checkIfSeparator(c)))
					{
						word+=c;
						c=(char)r.read();
					}
					if(word !="")
					{
						if(checkIfKeyword(word))
						{
							System.out.println(word + " is a Keyword");
							list.add(word);
							word="";
							while(checkIfSeparator(c))
							{
								c=(char)r.read();
							}
						}
						else
						{
							System.out.println(word + " is an Identifier");
							list.add("identifier");
							word="";
							while(checkIfSeparator(c))
							{
								c=(char)r.read();
							}
						}
					}
				}
				
				else if (checkIfSymbol(c))
				{
					//handle the assigning
					if(c==':')
					{
						word+=c;
						c=(char)r.read();
						if (c =='=')
						{
							word+=c;
							System.out.println(word + " is an Symbol");
							list.add(word);
							c=(char)r.read();
							word="";
							while(checkIfSeparator(c))
							{
								c=(char)r.read();
							}
						}						
					}
					
					//handle if a number comes after + or -
					else if (c== '+' || c=='-')
					{
						char temp =c;
						c=(char)r.read();
						if (Character.isDigit(c))
						{
							word+=temp;
							while(Character.isDigit(c)|| c=='E' || c=='.'|| c=='-' || c=='+')
							{
								word+=c;
								c=(char)r.read();						
							}
							if(word !="")
							{
								System.out.println(word + " is a Number");
								list.add("number");
								word="";
								while(checkIfSeparator(c))
								{
									c=(char)r.read();
								}
							}
						}
						else
						{
							System.out.println(temp + " is a Symbol");
							String temp2=Character.toString(temp);
							list.add(temp2);
							while(checkIfSeparator(c))
							{
								c=(char)r.read();
							}
						}
					}
					//if other symbol
					else
					{
						System.out.println(c + " is a Symbol");
						String temp2=Character.toString(c);
						list.add(temp2);
						c=(char)r.read();
						while(checkIfSeparator(c))
						{
							c=(char)r.read();
						}
					}					
				}				
			}		
		}
		r.close();
	}
	
	//some functions to help the scanner
	public boolean checkIfSymbol(char c)
	{
		for (int i =0; i<reservedSymbols.length;i++)
		{
			if(c== reservedSymbols[i])
			{				
				return true;
			}
		}
		return false;		
	}
	
	public boolean checkIfKeyword(String s)
	{
		for (int i=0; i<reservedWords.length;i++)
		{
			if (s.equals(reservedWords[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIfSeparator(char c)
	{
		if (c==' '|| c=='\n' || c=='\r' || c == '\t' || c==Character.MIN_VALUE)
			return true;
		return false;
	}
	/*********
	
	
	
	
	
	
	********/
	
	//Parser
	//matching
	public boolean  Match(String s)
	{
		//String temp=list.get(index);
		if(list.get(index).equals(s))
		{
			if(list.size()>(index+1))
			{
				++index;
				//int tempIndex=index;
				//temp=list.get(index);			
			}
			else
			{
				endOfCode=true;
			}
			//temp=list.get(index);
			return true;			
		}
		//temp=list.get(index);
		return false;
	}
		
	//Checking for statements
	public boolean Statement()
	{
		if(ReadStatment()|| WriteStatement()||IfStatement() ||AssignStatement() || RepeatStatement())
		{
			return true;
		}
		return false;
	}
	//read statement
	public boolean ReadStatment()
	{
		if(Match("read"))
		{
			if(Match("identifier"))
			{
				if(insideIf)
				{System.out.println("A read statement was found, inside if");}
				else if(insideRepeat)
				{System.out.println("A read statement was found, inside repeat");}
				else
				{System.out.println("A read statement was found");}
				return true;
			}
			error=true;			
		}
		return false;
	}
	
	//write statement
	public boolean WriteStatement()
	{
		if (Match("write"))
		{
			if(isExpression())
			{
				if(insideIf)
				{System.out.println("A write statement was found, inside if");}
				else if(insideRepeat)
				{System.out.println("A write statement was found, inside repeat");}
				else
				{System.out.println("A write statement was found");}
				return true;
			}
			error=true;
		}
		return false;
	}
	
	//if statement
	public boolean IfStatement()
	{
		if(Match("if"))
		{
			if(Match("("))
			{
				if(isExpression())
				{
					if(Match(")"))
					{
						insideIf=true;
						if(Match("then"))
						{
							while(true) //if no statements between if and end no error will be detected
							{
								String token=list.get(index);
								if(!token.equals("else")&&!token.equals("end"))
								{
									if(!Statement())
									{
										error=true;
										break;
									}
								}
								else break;
							}
							if(!error)
							{
								String token=list.get(index);
								if(token.equals("else"))
								{
									Match(token);
									while(true)
									{
										String token2=list.get(index);
										if(!token2.equals("end"))
										{
											if(!Statement())
											{
												error=true;
												break;
											}
										}
										else break;
									}
									if(!error)
									{
										if(Match("end"))
										{
											if(insideRepeat)
											{
												System.out.println("An if statement was found, inside Repeat");
											}
											else
											{
												System.out.println("An if statement was found");
											}
											insideIf=false;
											return true;
										}
									}								
								}
								else if(Match("end"))
								{
									if(insideRepeat)
									{
										System.out.println("An if statement was found, inside Repeat");
									}
									else
									{
										System.out.println("An if statement was found");
									}
									insideIf=false;
									return true;
								}
							}
						}
					}
				}
				error=true;
			}
			error=true;
		}
		return false;
	}
	
	//Assign statement
	public boolean AssignStatement()
	{
		if(Match("identifier"))
		{
			if(Match(":=")) 
			{
				if(isExpression())
				{
					if(insideIf)
					{System.out.println("An Assign statement was found, inside if");}
					else if(insideRepeat)
					{System.out.println("An Assign statement was found, inside repeat");}
					else
					{System.out.println("An Assign statement was found");}
					return true;
				}
				error=true;
			}
			error=true;
		}
		return false;
	}
	
	//repeat statement
	public boolean RepeatStatement()
	{
		if(Match("repeat"))
		{
			insideRepeat=true;
			while(true) //if no statements between repeat and until no error will be detected
			{
				String token=list.get(index);
				if(!token.equals("until"))
				{
					if(!Statement())
					{
						error=true;
						break;
					}
				}
				else break;
			}
			if(!error)
			{
				if(Match("until"))
				{
					if(isExpression())
					{
						if(insideIf)
						{
							System.out.println("A repeat statement was found, inside if");
						}
						else
						{	
							System.out.println("A repeat statement was found");
						}
						insideRepeat=false;
						return true;
					}
					error=true;
				}
			}			
		}
		return false;
	}
	//parsing elements
	
	//factor
	public boolean isFactor()
	{
		if(Match("("))
		{
			if(isExpression())//checks if the the input is an expression
			{
				if(Match(")"))
				{
					return true;
				}
			}
		}
		else if(Match("number")|| Match("identifier"))
		{
			return true;
		}
		return false;
	}
	
	//Term
	public boolean isTerm()
	{
		if(isFactor())
		{
			String token=list.get(index);
			while(token.equals("*"))
			{
				Match(token);
				token =list.get(index);
				if(!isFactor())
				{
					return false;
				}				
			}
			return true;
		}
		return false;
	}
	
	//simple expression
	public boolean isSimpleExpression()
	{
		if(isTerm())
		{
			String token =list.get(index);
			while(token.equals("+") || token.equals("-"))
			{
				Match(token);
				token =list.get(index);
				if(!isTerm())
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	//expression
	public boolean isExpression()
	{
		if(isSimpleExpression()) 
		{
			String token =list.get(index);
			if(token.equals("<")||
			   token.equals(">")||
			   token.equals("="))
			{
				Match(token);
				if(isSimpleExpression())
				{
					return true;
				}
				error=true;
			}
			return true;
		}
		return false;
	}
	
	
	public boolean checkIfEmpty(String s)
	{
		if (s.equals(""))
			return true;
		return false;
	}
	
	public void Parse()
	{
		//list.removeAll(Arrays.asList("", null));
		while(!endOfCode)
		{
			if(ReadStatment())
			{
				continue;
				//System.out.println("A read statement was found");//remember to write this in every function
			}
			else if(!error &&WriteStatement())
			{
				continue;
				//System.out.println("A write statement was found");
			}
			else if(!error &&IfStatement())
			{
				continue;
				//System.out.println("An if statement was found");
			}
			else if(!error &&AssignStatement())
			{
				continue;
				//System.out.println("An Assign statement was found");
			}
			else if(!error &&RepeatStatement())
			{
				continue;
				//System.out.println("A repeat statement was found");
			}
			else
			{
				System.out.println("Syntax Error");
				break;
			}
		}
	}
	
	public void printList()
	{
		//Iterator<String> iterator=list.iterator();
		for(String s:list)
		{
			System.out.println(s);
		}
		
	}
	public static void main(String args[]) throws IOException 
	{
		cOmPiLeRbOi c=new cOmPiLeRbOi();
		c.Start();
		c.Tokenize();
		c.Parse();
		//c.printList();
	}
	
	
	
}
