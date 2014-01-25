/**
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author sHivA
 * 
 */

public class Tokenize implements Constant{

     String reserved[] = 
   	 {	"",
   		"VAR",
   		"IF",
   		"WHILE",
   		"REPEAT",
   		"UNTIL",
   		"print",
   		"ARRAY",
   		"+",
   		"-",
   		"/",
   		"*",
   		"=",
   		":",
   		",",
   		";",
   		"[",
   		"]",
   		"(",
   		")",
   		"{",
   		"}",
   		"<>",
   		">",
   		"<",
   		"ID",
   		"NUM",
   		"ERROR"
   	 };
    
	// Defining variables
	static int ttype;
	int lineNumber = 1;
	int tokenLength = 0;
	int readAheadLimit = 1;
	char character;
	//static char[] token = new char[MAX_TOKEN_LENGTH];
	static StringBuilder token = new StringBuilder(MAX_TOKEN_LENGTH);
	static boolean activeToken = false;
	BufferedReader buffer;

	int isKeyword(String s)
	{
		int i;

		for (i = 1; i <= KEYWORDS; i++)
			if (reserved[i].equals(s))
				return i;
		return FALSE;
	}
	
	int scan_number(BufferedReader buffer) throws IOException
	{
		char tempChar;
		token.setLength(0); // Resetting the string
		//token.delete(0, token.length());
		
		tempChar = getChar(buffer);
		if (Character.isDigit(tempChar))
		{
			if (tempChar == '0')
			{
				/*token[tokenLength] = tempChar;
				tokenLength++;*/
				token.append(tempChar);
			}
			else
			{
				while (Character.isDigit(tempChar))
				{
					/*token[tokenLength] = tempChar;
					tokenLength++;*/
					token.append(tempChar);
					buffer.mark(readAheadLimit);
					tempChar = getChar(buffer);
				}
				buffer.reset();
			}
			return NUM;
		}
		else
			return ERROR;
	}

	int scan_id_or_keyword(BufferedReader buffer) throws IOException 
	{
		int ttype = 0;
		char tempChar;
		token.setLength(0); // Resetting the string
		//token.delete(0, token.length());
		
		tempChar = getChar(buffer);
		if (Character.isLetter(tempChar))
		{
			while (Character.isLetterOrDigit(tempChar))
			{
				/*token[tokenLength] = tempChar;
				tokenLength++;*/
				token.append(tempChar);
				buffer.mark(readAheadLimit); 
				tempChar = getChar(buffer);
			}
			
			buffer.reset();
				 
			ttype = isKeyword(token.toString());
			if (ttype == 0) 
				ttype = ID;
			return ttype;
		} else
			return ERROR;
	}

	void ungetToken() 
	{
		activeToken = true;
	}

	char getChar(BufferedReader buffer) throws IOException
	{
		int c = 0;
		char tempChar = 0;
		
		if ((c = buffer.read()) != -1)
			tempChar = (char)c;
		
		return tempChar;
	}
	
	BufferedReader getInput()
	{
		buffer = new BufferedReader(new InputStreamReader(System.in));
		return buffer;
	}
	
	void skipSpace(BufferedReader buffer) throws IOException
	{
		int c = 0;
		char tempChar;
		
		buffer.mark(readAheadLimit);
		tempChar = getChar(buffer);
		
		if (tempChar == '\n')
			lineNumber += 1;
		
		while (Character.isWhitespace(tempChar))
		{
			buffer.mark(readAheadLimit);
			tempChar = getChar(buffer);
			if (tempChar == '\n')
				lineNumber += 1;
		}
		
		// return character to input buffer if eof is not reached
		if (c != -1) 
			buffer.reset();
	}
	
	int getToken(BufferedReader buffer) throws IOException 
	{
		if (activeToken) 
		{
			activeToken = false;
			return ttype;
		}
		
		skipSpace(buffer);
		buffer.mark(readAheadLimit);
		character = getChar(buffer);
		
		switch(character)
		{
			case '+': 
				ttype = PLUS; 
				break;
			case '-': 
				ttype = MINUS; 
				break;	
			case '/': 
				ttype = DIV; 
				break;
			case '*': 
				ttype = MULT; 
				break;
			case '=': 
				ttype = EQUAL; 
				break;
			case ':': 
				ttype = COLON; 
				break;
			case ',': 
				ttype = COMMA; 
				break;
			case ';': 
				ttype = SEMICOLON; 
				break;
			case '[': 
				ttype = LBRAC; 
				break;
			case ']': 
				ttype = RBRAC; 
				break;
			case '(': 
				ttype = LPAREN; 
				break;
			case ')': 
				ttype = RPAREN; 
				break;
			case '{': 
				ttype = LBRACE; 
				break;
			case '}': 
				ttype = RBRACE; 
				break;
			case '<':
				buffer.mark(readAheadLimit);
				character = getChar(buffer);
				if (character == '>')
					ttype = NOTEQUAL;
				else
				{
					buffer.reset();
					ttype = LESS;
				}
				break;
			case '>':
				ttype = GREATER;
				break;
			default:
				if (Character.isDigit(character))
				{
					buffer.reset();
					ttype = scan_number(buffer);
				}
				else if (Character.isLetter(character)) 
				{
					// token is either a keyword or ID
					buffer.reset();
					ttype = scan_id_or_keyword(buffer);
				}
				break;
		}
		return ttype;
	}	
}
