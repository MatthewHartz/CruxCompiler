package crux;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Scanner implements Iterable<Token> {
	public static String studentName = "Matthew Hartz";
	public static String studentID = "87401675";
	public static String uciNetID = "hartzm";
	
	private int lineNum;  // current line count
	private int charPos;  // character offset for current line
	private int nextChar; // contains the next char (-1 == EOF)
	private Reader input;
	
	Scanner(Reader reader) throws IOException
	{
		input = reader;
		lineNum = 1;
		charPos = 1;
		nextChar = input.read();
	}	
	
	// OPTIONAL: helper function for reading a single char from input
	//           can be used to catch and handle any IOExceptions,
	//           advance the charPos or lineNum, etc.	
	private int readChar() {
		try {
			nextChar = input.read();
			charPos++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nextChar;
	}
	
	private void readNewLine() {
		// to handle comments
		while (!IsNewLine(nextChar)) {
			if (nextChar == -1) {
				return;
			}
			nextChar = readChar();
		}
		
		while (IsNewLine(nextChar)) {
			nextChar = readChar();
		}
		
		charPos = 1;
		lineNum++;
	}
	
	private Integer ClearWhitespace() {	
		while (Character.isWhitespace((char)nextChar)) {
			// Newline coming up
			if (IsNewLine(nextChar)) {
				readNewLine();
			} else {
				nextChar = readChar();
			}
		}
		
		return charPos;
	}
	
	private Boolean IsNewLine(int character) {
		if (character == 10) {
			return true;
		}
		else {
			return false;
		}
	}
	
		

	/* Invariants:
	 *  1. call assumes that nextChar is already holding an unread character
	 *  2. return leaves nextChar containing an untokenized character
	 */
	public Token next()
	{
		Integer lNum;
		Integer cPos;
		String s = "";
		
		// Ignore whitespace
		ClearWhitespace();
		
		// Case for special characters except !=		
		if (Kind.matches(s + (char)nextChar)) {
			lNum = lineNum;
			cPos = charPos;
			
			s += (char)nextChar;
			nextChar = readChar();
			
			// Odd case for comments since "//" starts with the division sign "/"
			if ((char)nextChar == '/' && s.equals("/")) {
				while (IsNewLine(nextChar) && nextChar != -1) {
					nextChar = readChar();
				}
				
				// if comment is on the last line of the file, return the EOF token
				if (nextChar == -1) {
					return Token.GetToken("EOF", "", lineNum, charPos);
				}
				
				// else move on to next line
				readNewLine();
				return next();
			}
			
			// special case for 2 character tokens
			if (Kind.matches(s + (char)nextChar)) {
				s += (char)nextChar;
				nextChar = readChar();
				return Token.GetToken("", s, lNum, cPos);
			} else {
				return Token.GetToken("", s, lNum, cPos);
			}
		}
		
		
		// Special case for !=
		if ((char)nextChar == '!') {
			lNum = lineNum;
			cPos = charPos;
			
			s += (char)nextChar;
			nextChar = readChar();
			if ((char)nextChar == '=') {
				s += (char)nextChar;
				nextChar = readChar();
			}
			
			return new Token(s, lNum, cPos);			
		}
			
		// Case for INTEGER/FLOAT
		if (Character.isDigit((char)nextChar)) {
			lNum = lineNum;
			cPos = charPos;
			
			s += (char)nextChar;
			nextChar = readChar();
			
			while (Character.isDigit((char)nextChar)) {
				s += (char)nextChar;
				nextChar = readChar();
			}
			
			if ((char)nextChar == '.') {
				s += (char)nextChar;
				nextChar = readChar();
				
				while (Character.isDigit((char)nextChar)) {
					s += (char)nextChar;
					nextChar = readChar();
				}
				
				return Token.GetToken("FLOAT", s, lNum, cPos);
			} else {
				return Token.GetToken("INTEGER", s, lNum, cPos);
			}	
		}
		
		// Case for IDENTIFIERS or KEYWORDS
		if (Character.isLetter((char)nextChar) || (char)nextChar == '_') {
			lNum = lineNum;
			cPos = charPos;
			
			s += (char)nextChar;
			nextChar = readChar();
			
			while (Character.isLetter((char)nextChar) || (char)nextChar == '_' || Character.isDigit((char)nextChar)) {
				s += (char)nextChar;
				nextChar = readChar();
			}
			
			if (Kind.matches(s)) {
				return new Token(s, lNum, cPos);
			} else {
				return Token.GetToken("IDENTIFIER", s, lNum, cPos);
			}
		}
		
		// END OF FILE
		if (nextChar == -1) {
			return Token.GetToken("EOF", "", lineNum, charPos);
		}
		
		// ERROR ITEM FOUND
		lNum = lineNum;
		cPos = charPos;
		s += (char)nextChar;
		nextChar = readChar();

		return new Token(s, lNum, cPos);
	}

	@Override
	public Iterator<Token> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	// OPTIONAL: any other methods that you find convenient for implementation or testing
	
}
