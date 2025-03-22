package interpreter.evaluating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import interpreter.Lox;
import interpreter.grammar.Token;
import interpreter.parser.Expression;
import interpreter.parser.Expression.Grouping;
import interpreter.parser.Expression.Literal;
import interpreter.parser.Expression.Logical;
import interpreter.parser.Expression.Unary;
import interpreter.parser.Statement;

public class Resolver implements Statement.Visitor<Void>, Expression.Visitor<Void> {

	private final Interpreter interpreter;
	private final Lox lox;

	private final Stack<Map<String, Boolean>> scopes = new Stack<>();

	public Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;
		this.lox = interpreter.lox();
	}

	public void resolve(List<Statement> statements) {
		statements.forEach(this::resolve);
	}

	private void resolve(Statement statement) {
		visit(statement);
	}

	private void resolve(Expression expression) {
		visit(expression);
	}

	private void beginScope() {
		scopes.push(new HashMap<>());
	}

	private void endScope() {
		scopes.pop();
	}

	private void declare(Token name) {
		if (scopes.isEmpty()) {
			return;
		}

		final var scope = scopes.peek();
		scope.put(name.lexeme(), false);
	}

	private void define(Token name) {
		if (scopes.isEmpty()) {
			return;
		}

		final var scope = scopes.peek();
		scope.put(name.lexeme(), true);
	}

	private void resolveLocal(Expression expression, Token name) {
		for (var index = scopes.size() - 1; index >= 0; index--) {
			if (scopes.get(index).containsKey(name.lexeme())) {
				interpreter.resolve(expression, scopes.size() - 1 - index);
				return;
			}
		}
	}

	private void resolveFunction(Statement.Function function) {
		beginScope();

		for (final var parameter : function.parameters()) {
			declare(parameter);
			define(parameter);
		}

		resolve(function.body());

		endScope();
	}

	@Override
	public Void visitBlock(Statement.Block block) {
		beginScope();
		resolve(block.statements());
		endScope();

		return null;
	}

	@Override
	public Void visitVariable(Statement.Variable variable) {
		declare(variable.name());
		variable.initializer().ifPresent(this::resolve);
		define(variable.name());

		return null;
	}

	@Override
	public Void visitVariable(Expression.Variable variable) {
		if (!scopes.isEmpty() && scopes.peek().get(variable.name().lexeme()) == Boolean.FALSE) {
			lox.error(variable.name(), "Can't read local variable in its own initializer.");
		}

		resolveLocal(variable, variable.name());

		return null;
	}

	@Override
	public Void visitAssign(Expression.Assign assign) {
		resolve(assign.value());
		resolveLocal(assign, assign.name());

		return null;
	}

	@Override
	public Void visitFunction(Statement.Function function) {
		declare(function.name());
		define(function.name());

		resolveFunction(function);

		return null;
	}

	@Override
	public Void visitExpression(Statement.Expression expression) {
		resolve(expression.expression());

		return null;
	}

	@Override
	public Void visitIf(Statement.If if_) {
		resolve(if_.condition());
		resolve(if_.thenBranch());
		if_.elseBranch().ifPresent(this::resolve);

		return null;
	}

	@Override
	public Void visitPrint(Statement.Print print) {
		resolve(print.expression());

		return null;
	}

	@Override
	public Void visitReturn(Statement.Return return_) {
		return_.value().ifPresent(this::resolve);

		return null;
	}

	@Override
	public Void visitWhile(Statement.While while_) {
		resolve(while_.condition());
		resolve(while_.body());

		return null;
	}

	@Override
	public Void visitBinary(Expression.Binary binary) {
		resolve(binary.left());
		resolve(binary.right());

		return null;
	}

	@Override
	public Void visitCall(Expression.Call call) {
		resolve(call.callee());

		for (final var argument : call.arguments()) {
			resolve(argument);
		}

		return null;
	}

	@Override
	public Void visitGrouping(Grouping grouping) {
		resolve(grouping.expression());

		return null;
	}

	@Override
	public Void visitLiteral(Literal literal) {
		return null;
	}

	@Override
	public Void visitLogical(Logical logical) {
		resolve(logical.left());
		resolve(logical.right());

		return null;
	}

	@Override
	public Void visitUnary(Unary unary) {
		resolve(unary.right());

		return null;
	}

}