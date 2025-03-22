package interpreter.evaluating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import interpreter.Lox;
import interpreter.grammar.Token;
import interpreter.parser.Expression;
import interpreter.parser.Statement;
import lombok.NonNull;

public class Resolver implements Statement.Visitor<Void>, Expression.Visitor<Void> {

	private final Interpreter interpreter;
	private final Lox lox;

	private @NonNull FunctionType currentFunctionType = FunctionType.NONE;
	private @NonNull ClassType currentClassType = ClassType.NONE;

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
		if (scope.containsKey(name.lexeme())) {
			lox.error(name, "Already a variable with this name in this scope.");
		}

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

	private void resolveFunction(Statement.Function function, FunctionType type) {
		final var enclosingType = currentFunctionType;
		currentFunctionType = type;

		beginScope();

		for (final var parameter : function.parameters()) {
			declare(parameter);
			define(parameter);
		}

		resolve(function.body());

		endScope();

		currentFunctionType = enclosingType;
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

		resolveFunction(function, FunctionType.FUNCTION);

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
		if (FunctionType.NONE.equals(currentFunctionType)) {
			lox.error(return_.keyword(), "Can't return from top-level code.");
		}

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
	public Void visitGrouping(Expression.Grouping grouping) {
		resolve(grouping.expression());

		return null;
	}

	@Override
	public Void visitLiteral(Expression.Literal literal) {
		return null;
	}

	@Override
	public Void visitLogical(Expression.Logical logical) {
		resolve(logical.left());
		resolve(logical.right());

		return null;
	}

	@Override
	public Void visitUnary(Expression.Unary unary) {
		resolve(unary.right());

		return null;
	}

	@Override
	public Void visitClass(Statement.Class class_) {
		final var enclosingType = currentClassType;
		currentClassType = ClassType.CLASS;

		declare(class_.name());
		define(class_.name());

		beginScope();
		scopes.peek().put("this", true);

		for (final var method : class_.methods()) {
			var declaration = FunctionType.METHOD;
			if (method.name().lexeme().equals("init")) {
				declaration = FunctionType.INITIALIZER;
			}

			resolveFunction(method, declaration);
		}

		endScope();

		currentClassType = enclosingType;

		return null;
	}

	@Override
	public Void visitGet(Expression.Get get) {
		resolve(get.object());

		return null;
	}

	@Override
	public Void visitSet(Expression.Set set) {
		resolve(set.value());
		resolve(set.object());

		return null;
	}

	@Override
	public Void visitThis(Expression.This this_) {
		if (ClassType.NONE.equals(currentClassType)) {
			lox.error(this_.keyword(), "Can't use 'this' outside of a class.");
		}

		resolveLocal(this_, this_.keyword());

		return null;
	}

	public enum FunctionType {

		NONE,
		FUNCTION,
		INITIALIZER,
		METHOD,

	}

	public enum ClassType {

		NONE,
		CLASS,

	}

}