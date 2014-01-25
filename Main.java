import java.io.IOException;

/**
 * 
 */
/**
 * @author sHivA
 *
 */
public class Main implements Constant{

	/**
	 * @param args
	 * @throws IOException 
	 */
		
	public static void main(String[] args) throws IOException {
		ParseProgramAndGenerateIntermediateRepresentation parseProgram = new ParseProgramAndGenerateIntermediateRepresentation();
		StatementNode program = parseProgram.parse_program_and_generate_intermediate_representation();
		execute_program(program);
	}

	private static void execute_program(StatementNode program) {
		StatementNode pc = program;
		int op1, op2, result = 0;
		while (pc != null)
		{
			switch (pc.stmt_type)
			{
				case NOOPSTMT:
					pc = pc.next;
					break;
					
				case PRINTSTMT:
					if (pc.print_stmt == null)
					{
						print_debug("Error: pc points to a print statement but pc.print_stmt is null.\n");
						System.exit(1);
					}
					if (pc.print_stmt.id == null)
					{
						print_debug("Error: print_stmt.id is null.\n");
						System.exit(1);
					}
					//printf("%d\n", pc.print_stmt.id.value);
					System.out.printf("%d\n",pc.print_stmt.id.value);
					pc = pc.next;
					break;	
					
				case ASSIGNSTMT:
					if (pc.assign_stmt == null)
					{
						print_debug("Error: pc points to an assignment statement but pc.assign_stmt is null.\n");
						System.exit(1);
					}
					if (pc.assign_stmt.op1 == null)
					{
						print_debug("Error: assign_stmt.op1 is null.\n");
						System.exit(1);
					}
					if (pc.assign_stmt.op == PLUS || pc.assign_stmt.op == MINUS
						|| pc.assign_stmt.op == MULT || pc.assign_stmt.op == DIV)
					{
						if (pc.assign_stmt.op2 == null)
						{
							print_debug("Error: right-hand-side of assignment is an expression but assign_stmt.op2 is null.\n");
							System.exit(1);
						}
					}
					if (pc.assign_stmt.lhs == null)
					{
						print_debug("Error: assign_stmt.lhs is null.\n");
						System.exit(1);
					}
					switch (pc.assign_stmt.op)
					{
						case PLUS:
							op1 = pc.assign_stmt.op1.value;
							op2 = pc.assign_stmt.op2.value;
							result = op1 + op2;
							break;
						case MINUS:
							op1 = pc.assign_stmt.op1.value;
							op2 = pc.assign_stmt.op2.value;
							result = op1 - op2;
							break;
						case MULT:
							op1 = pc.assign_stmt.op1.value;
							op2 = pc.assign_stmt.op2.value;
							result = op1 * op2;
							break;
						case DIV:
							op1 = pc.assign_stmt.op1.value;
							op2 = pc.assign_stmt.op2.value;
							result = op1 / op2;
							break;
						case 0:
							op1 = pc.assign_stmt.op1.value;
							result = op1;
							break;
						default:
							print_debug("Error: invalid value for assign_stmt.op (%d).\n", pc.assign_stmt.op);
							System.exit(1);
							break;
					}
					pc.assign_stmt.lhs.value = result;
					pc = pc.next;
					break;
					
				case IFSTMT:
					if (pc.if_stmt == null)
					{
						print_debug("Error: pc points to an if statement but pc.if_stmt is null.\n");
						System.exit(1);
					}
					if (pc.if_stmt.true_branch == null)
					{
						print_debug("Error: if_stmt.true_branch is null.\n");
						System.exit(1);
					}
					if (pc.if_stmt.false_branch == null)
					{
						print_debug("Error: if_stmt.false_branch is null.\n");
						System.exit(1);
					}
					if (pc.if_stmt.op1 == null)
					{
						print_debug("Error: if_stmt.op1 is null.\n");
						System.exit(1);
					}
					if (pc.if_stmt.op2 == null)
					{
						print_debug("Error: if_stmt.op2 is null.\n");
						System.exit(1);
					}
					op1 = pc.if_stmt.op1.value;
					op2 = pc.if_stmt.op2.value;
					switch (pc.if_stmt.relop)
					{
						case GREATER:
							if (op1 > op2)
								pc = pc.if_stmt.true_branch;
							else
								pc = pc.if_stmt.false_branch;
							break;
						case LESS:
							if (op1 < op2)
								pc = pc.if_stmt.true_branch;
							else
								pc = pc.if_stmt.false_branch;
							break;
						case NOTEQUAL:
							if (op1 != op2)
								pc = pc.if_stmt.true_branch;
							else
								pc = pc.if_stmt.false_branch;
							break;
						case EQUALEQUAL:
							if (op1 == op2)
								pc = pc.if_stmt.true_branch;
							else
								pc = pc.if_stmt.false_branch;
							break;
						default:
							print_debug("Error: invalid value for if_stmt.relop (%d).\n", pc.if_stmt.relop);
							System.exit(1);
							break;
					}
					break;
					
				case GOTOSTMT:
					if (pc.goto_stmt == null)
					{
						print_debug("Error: pc points to a goto statement but pc.goto_stmt is null.\n");
						System.exit(1);
					}
					if (pc.goto_stmt.target == null)
					{
						print_debug("Error: goto_stmt.target is null.\n");
						System.exit(1);
					}
					pc = pc.goto_stmt.target;
					break;
					
				default:
					print_debug("Error: invalid value for stmt_type (%d).\n", pc.stmt_type);
					System.exit(1);
					break;
			}
		}
	}

	private static void print_debug(String string, int stmt) {
		System.out.printf(string , stmt);
		System.exit(0);
	}

	private static void print_debug(String string) {
		System.out.print(string);
		System.exit(0);
	}

}
