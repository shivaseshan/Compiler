/**
 * 
 */

/**
 * @author sHivA
 *
 */
public class StatementNode {
	int stmt_type;						// NOOPSTMT, PRINTSTMT, ASSIGNSTMT, IFSTMT, GOTOSTMT
	AssignmentStatement assign_stmt;	// NOT NULL iff stmt_type == ASSIGNSTMT
	PrintStatement      print_stmt;		// NOT NULL iff stmt_type == PRINTSTMT
	IfStatement         if_stmt;		// NOT NULL iff stmt_type == IFSTMT
	GotoStatement       goto_stmt;		// NOT NULL iff stmt_type == GOTOSTMT
	StatementNode       next;			// next statement in the list or NULL 
}
