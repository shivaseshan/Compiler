import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author sHivA
 *
 */
public class ParseProgramAndGenerateIntermediateRepresentation implements Constant{
	
	Tokenize t = new Tokenize();
	public ArrayList<VarNode> variables = new ArrayList<VarNode>();
	
	StatementNode ParseProgram(BufferedReader buffer) throws IOException
	{
		int ttype;
		StatementNode stmtNode = null;
		
		ttype = t.getToken(buffer);
		if (ttype == ID)
		{
			t.ungetToken();
			variables = VariableSection(buffer);
			ttype = t.getToken(buffer);
		}
		
		if (ttype == LBRACE)
		{
			t.ungetToken();
			stmtNode = Body(buffer);
		}
		
		return stmtNode;
	}
	
	ArrayList<VarNode> VariableSection(BufferedReader buffer) throws IOException
	{
		int ttype;
		
		ttype = t.getToken(buffer);
		while (ttype == ID)
		{
			t.ungetToken();
			variables = idList(buffer);	
			ttype = t.getToken(buffer);
		}
		t.ungetToken();
		
		return variables;
	}
	
	ArrayList<VarNode> idList(BufferedReader buffer) throws IOException
	{
		int ttype;
		VarNode var = new VarNode();

		ttype = t.getToken(buffer);
		if (ttype == ID)
		{
			var.name = new StringBuilder(Tokenize.token);
			variables.add(var);
			
			ttype = t.getToken(buffer);
			if (ttype == COMMA)
				idList(buffer);
			else if (ttype != SEMICOLON)
			{
				System.out.print("idList. SEMICOLON expected");
				System.exit(0);
			}
		}
		
		return variables;
	}
	
	StatementNode Body(BufferedReader buffer) throws IOException
	{
		int ttype;
		StatementNode stmtNode = null;

		ttype = t.getToken(buffer);
		if (ttype == LBRACE)
		{
			stmtNode = stmtList(buffer);
			ttype = t.getToken(buffer);
			if (ttype != RBRACE)
			{
				System.out.print("body. RBRACE expected");
				System.exit(0);
			}
		}
		
		return stmtNode;
	}
	
	StatementNode stmtList(BufferedReader buffer) throws IOException
	{
		int ttype;
		StatementNode stmtNode = null;
		
		ttype = t.getToken(buffer);
		if (ttype == ID || ttype == WHILE || ttype == IF || ttype == REPEAT || ttype == PRINT)
		{
			t.ungetToken();
			stmtNode = stmt(buffer);
			
			ttype = t.getToken(buffer);
			if (ttype == ID || ttype == WHILE || ttype == IF || ttype == REPEAT || ttype == PRINT)
			{
				t.ungetToken();
				if (stmtNode.next == null) 
					stmtNode.next = stmtList(buffer);
				else 
					stmtNode.next.next = stmtList(buffer);
			}
			else if (ttype == RBRACE)
			{
				t.ungetToken();
			}
			else 
			{
				System.out.print("stmtList. ID, WHILE, IF, REPEAT expected");
				System.exit(0);
			}
		}
		return stmtNode;
	}
	
	StatementNode stmt(BufferedReader buffer) throws IOException
	{
		int ttype;
		StatementNode stmtNode = new StatementNode();
		
		ttype = t.getToken(buffer);
		switch (ttype)
		{
			case ID:
				stmtNode = assignmentStmt(buffer);
				break;
				
			case WHILE:
				stmtNode = whileStmt(buffer);
				break;
				
			case IF:
				stmtNode = ifStmt(buffer);
				break;
				
			case REPEAT:
				stmtNode = repeatStmt(buffer);
				break;
				
			case PRINT:
				stmtNode = printStmt(buffer);
				break;
				
			default:
				System.out.print("stmt. ID, WHILE, IF, REPEAT, print expected.");
				System.exit(0);
		}
		return stmtNode;
	}
	
	StatementNode assignmentStmt(BufferedReader buffer) throws IOException
	{
		int ttype = 0, index = 0;
		StringBuilder token = null;
		VarNode var = new VarNode();
		StatementNode  stmt = new StatementNode();		
		AssignmentStatement assignmentStmt = new AssignmentStatement();
		stmt.assign_stmt = assignmentStmt;
		
		stmt.stmt_type = ASSIGNSTMT; // Setting stmt_type
		token = Tokenize.token;
		index = findInArrayList(variables, token);
		
		if (index == -1) // variable not declared earlier
		{
			var.name = new StringBuilder(token);
			assignmentStmt.lhs = var;
		}
		else // found declared variable at index
			assignmentStmt.lhs = variables.get(index);
		
		ttype = t.getToken(buffer);
	
		if (ttype == EQUAL)
		{
			assignmentStmt = expr(buffer, assignmentStmt);
			ttype = t.getToken(buffer);
			if (ttype != SEMICOLON)
			{
				System.out.print("SEMICOLON expected.");
				System.exit(0);
			}
		}
		return stmt;
	}
	
	int findInArrayList(ArrayList<VarNode> variables, StringBuilder token)
	{
		int index = 0;
		StringBuilder varName = null;
		
		for (int i = 0; i < variables.size(); i++)
		{
			varName = variables.get(i).name;
			if (varName.toString().equals(token.toString()))
			{
				return i;
			}
			else 
				index = -1;
		}
		return index;
	}
	
	AssignmentStatement expr(BufferedReader buffer, AssignmentStatement assignmentStmt) throws IOException
	{
		int ttype;
		VarNode op1 = null;
		
		op1 = primary(buffer);
		assignmentStmt.op1 = op1;
		assignmentStmt.op = 0;
		
		ttype = t.getToken(buffer);
		if (ttype == PLUS || ttype == MINUS || ttype == MULT || ttype == DIV)
		{
			VarNode op2 = null;
			assignmentStmt.op = ttype;
			op2 = primary(buffer);
			assignmentStmt.op2 = op2;
		}
		else if (ttype == SEMICOLON)
		{
			t.ungetToken();
		}
		else
		{
			System.out.print("PLUS, MINUS, MULT, DIV expected.");
			System.exit(0);
		}
		return assignmentStmt;
		
	}
	
	VarNode primary(BufferedReader buffer) throws IOException
	{
		int ttype, index;
		String number = null;
		StringBuilder token = null;		
		VarNode var = new VarNode();
		
		ttype = t.getToken(buffer);
		if (ttype == ID || ttype == NUM)
		{
			if (ttype == ID)
			{
				token = Tokenize.token;
				index = findInArrayList(variables, token);
				if (index == -1)
				{
					var.name = new StringBuilder(token);
				}
				else
				{
					var = variables.get(index);
				}
			}
			
			else if (ttype == NUM)
			{ 
				token = Tokenize.token;
				number = token.toString();
				var.value = Integer.parseInt(String.valueOf(number));
			}
		}
		else
		{
			System.out.print("primary. ID, NUM expected.");
			System.exit(0);
		}
		
		return var;	
	}
	
	StatementNode printStmt(BufferedReader buffer) throws IOException
	{
		int ttype, index = 0;
		StringBuilder token = null;
		StatementNode stmt = new StatementNode();
		PrintStatement printStmt = new PrintStatement();
		stmt.print_stmt = printStmt;
		
		stmt.stmt_type = PRINTSTMT; // Setting stmt_type
		ttype = t.getToken(buffer);
		token = Tokenize.token;
		index = findInArrayList(variables, token);
		
		if (index == -1) // variable not declared earlier
		{
			VarNode var = new VarNode();
			var.name = token;
			printStmt.id = var;
		}
		else // found declared variable at index
			printStmt.id = variables.get(index);
		
		ttype = t.getToken(buffer);
		if (ttype != SEMICOLON)
		{
			System.out.print("SEMICOLON expected.");
			System.exit(0);
		}
		
		return stmt;
	}
	
	StatementNode whileStmt(BufferedReader buffer) throws IOException
	{
		StatementNode stmt = new StatementNode();
		StatementNode updateIfStmt;
		StatementNode gotoStmt = new StatementNode();
		StatementNode stmtNode = new StatementNode();
		GotoStatement gotoNode = new GotoStatement();
		stmtNode.stmt_type = NOOPSTMT;
		
		stmt.stmt_type = IFSTMT; // Setting stmt_type
		stmt.if_stmt = condition(buffer);
		stmt.if_stmt.true_branch = Body(buffer);
		gotoStmt.stmt_type = GOTOSTMT;
		gotoStmt.goto_stmt = gotoNode;
		gotoNode.target = stmt;
		
		// Traversing the linked list to add gotoStmt node
		updateIfStmt = stmt.if_stmt.true_branch;
		while (updateIfStmt.next != null)
		{
			updateIfStmt = updateIfStmt.next;
		}
		updateIfStmt.next = gotoStmt;
		// Updating the false branch to point to NOOPSTMT node
		stmt.if_stmt.false_branch =  stmtNode;
		stmt.next = stmtNode;
		
		return stmt;
	}
	
	IfStatement condition(BufferedReader buffer) throws IOException
	{
		int ttype;
		IfStatement ifStmt = new IfStatement();
		
		ttype = t.getToken(buffer);
		if (ttype == ID || ttype == NUM)
		{
			t.ungetToken();
			ifStmt.op1 = primary(buffer);
			
			ttype = t.getToken(buffer);
			if (ttype == GREATER || ttype == LESS || ttype == NOTEQUAL)
			{
				ifStmt.relop = ttype;
				ifStmt.op2 = primary(buffer);
			}
		}
		else
		{
			System.out.print("condition. ID, NUM expected.");
			System.exit(0);
		}
		
		return ifStmt;
	}
	
	StatementNode ifStmt(BufferedReader buffer) throws IOException
	{
		StatementNode updateIfStmt;
		StatementNode stmt = new StatementNode();
		StatementNode stmtNode = new StatementNode();
		stmtNode.stmt_type = NOOPSTMT;
		
		stmt.stmt_type = IFSTMT; // Setting stmt_type
		stmt.if_stmt = condition(buffer);
		stmt.if_stmt.true_branch = Body(buffer);
		
		// Traversing the linked list to add NOOPSTMT node
		updateIfStmt = stmt.if_stmt.true_branch;
		while (updateIfStmt.next != null)
		{
			updateIfStmt = updateIfStmt.next;
		}
		updateIfStmt.next = stmtNode;
		// Updating the false branch to point to NOOPSTMT node
		stmt.if_stmt.false_branch =  stmtNode;
		stmt.next = stmtNode;
		
		return stmt;
	}
	
	StatementNode repeatStmt(BufferedReader buffer) throws IOException
	{
		int ttype, relop;
		StatementNode updateStmt, updateIfStmt;
		StatementNode stmt = new StatementNode();
		StatementNode repeatStmt = new StatementNode();
		StatementNode gotoStmt = new StatementNode();
		StatementNode stmtNode = new StatementNode();
		GotoStatement gotoNode = new GotoStatement();
		stmtNode.stmt_type = NOOPSTMT;
		
		stmt = Body(buffer);
		ttype = t.getToken(buffer);
		if (ttype == UNTIL)
		{
			// Traversing till end of linked list
			updateStmt = stmt;
			while (updateStmt.next != null)
			{
				updateStmt = updateStmt.next;
			}
			updateStmt.next = repeatStmt;

			repeatStmt.stmt_type = IFSTMT;
			repeatStmt.if_stmt = condition(buffer);
			relop = repeatStmt.if_stmt.relop;
			if (relop == GREATER)
				repeatStmt.if_stmt.relop = LESS;
			else if (relop == LESS)
				repeatStmt.if_stmt.relop = GREATER;
			else if (relop == NOTEQUAL)
				repeatStmt.if_stmt.relop = EQUALEQUAL;
			repeatStmt.if_stmt.true_branch = stmt;
			gotoStmt.stmt_type = GOTOSTMT;
			gotoStmt.goto_stmt = gotoNode;
			gotoNode.target = repeatStmt;
			
			// Traversing the linked list to add gotoStmt node
			updateIfStmt = repeatStmt.if_stmt.true_branch;
			while (updateIfStmt.next != null)
			{
				updateIfStmt = updateIfStmt.next;
			}
			updateIfStmt.next = gotoStmt;
			// Updating the false branch to point to NOOPSTMT node
			repeatStmt.if_stmt.false_branch =  stmtNode;
			repeatStmt.next = stmtNode;
		}
		else 
		{
			System.out.print("Repeat. Until Expected");
			System.exit(0);
		}
		return stmt;
		
	}	
	
	public StatementNode parse_program_and_generate_intermediate_representation() throws IOException 
	{
		int ttype;
		StatementNode stmttNode = null;
		
		BufferedReader buffer = t.getInput();
		ttype = t.getToken(buffer);
		
		if (ttype == ID || ttype == LBRACE)
		{	
			t.ungetToken();
			ParseProgramAndGenerateIntermediateRepresentation parseProgram = new ParseProgramAndGenerateIntermediateRepresentation();
			stmttNode = parseProgram.ParseProgram(buffer);
		}
		else
		{
			System.out.print("ID or LBRACE Expected");
			System.exit(0);
		}
		return stmttNode;
	}
}
