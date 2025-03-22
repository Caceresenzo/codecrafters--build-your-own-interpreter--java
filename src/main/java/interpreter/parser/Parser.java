package interpreter.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import interpreter.Lox;
import interpreter.grammar.Literal;
import interpreter.grammar.Token;
import interpreter.grammar.TokenType;
import lombok.NonNull;

public class Parser {

	private final Lox lox;
	private final List<Token> tokens;

	private int current = 0;

	public Parser(
		@NonNull Lox lox,
		@NonNull List<Token> tokens
	) {
		this.lox = lox;
		this.tokens = tokens;
	}

	public List<Statement> parse() {
		try {
			final var statements = new ArrayList<Statement>();

			while (!isAtEnd()) {
				statements.add(declarationStatement());
			}

			return statements;
		} catch (ParseError __) {
			return Collections.emptyList();
		}
	}

	public Optional<Expression> parseExpression() {
		try {
			return Optional.of(expression());
		} catch (ParseError __) {
			return Optional.empty();
		}
	}

	private Statement declarationStatement() {
		try {
			if (match(TokenType.CLASS)) {
				return classDeclarationStatement();
			}

			if (match(TokenType.FUN)) {
				return functionStatement("function");
			}

			if (match(TokenType.VAR)) {
				return variableDeclarationStatement();
			}

			return statement();
		} catch (ParseError error) {
			synchronize();
			throw error;
		}
	}

	private Statement.Class classDeclarationStatement() {
		final var name = consume(TokenType.IDENTIFIER, "Expect class name.");
		consume(TokenType.LEFT_BRACE, "Expect '{' before class body.");

		final var methods = new ArrayList<Statement.Function>();
		while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
			methods.add(functionStatement("method"));
		}

		consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.");

		return new Statement.Class(name, methods);
	}

	private Statement.Function functionStatement(String kind) {
		final var name = consume(TokenType.IDENTIFIER, "Expect %s name.".formatted(kind));

		consume(TokenType.LEFT_PAREN, "Expect '(' after %s name.".formatted(kind));

		final var parameters = new ArrayList<Token>();
		if (!check(TokenType.RIGHT_PAREN)) {
			do {
				if (parameters.size() >= 255) {
					throw error(peek(), "Can't have more than 255 parameters.");
				}

				parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."));
			} while (match(TokenType.COMMA));
		}

		consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
		consume(TokenType.LEFT_BRACE, "Expect '{' before %s} body.".formatted(kind));

		final var body = block();

		return new Statement.Function(name, parameters, body);
	}

	private Statement.Variable variableDeclarationStatement() {
		final var name = consume(TokenType.IDENTIFIER, "Expect variable name.");

		var initializer = Optional.<Expression>empty();
		if (match(TokenType.EQUAL)) {
			initializer = Optional.of(expression());
		}

		consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");

		return new Statement.Variable(name, initializer);
	}

	private @NonNull Statement statement() {
		if (match(TokenType.FOR)) {
			return forStatement();
		}

		if (match(TokenType.IF)) {
			return ifStatement();
		}

		if (match(TokenType.PRINT)) {
			return printStatement();
		}

		if (match(TokenType.RETURN)) {
			return returnStatement();
		}

		if (match(TokenType.WHILE)) {
			return whileStatement();
		}

		if (match(TokenType.LEFT_BRACE)) {
			return new Statement.Block(block());
		}

		return expressionStatement();
	}

	private Statement forStatement() {
		consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");

		Optional<Statement> initializer;
		if (match(TokenType.SEMICOLON)) {
			initializer = Optional.empty();
		} else if (match(TokenType.VAR)) {
			initializer = Optional.of(variableDeclarationStatement());
		} else {
			initializer = Optional.of(expressionStatement());
		}

		final var condition = !check(TokenType.SEMICOLON)
			? expression()
			: new Expression.Literal(new Literal.Boolean(true));

		consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

		final var increment = !check(TokenType.RIGHT_PAREN)
			? Optional.of(expression())
			: Optional.<Expression>empty();

		consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");

		var body = statement();
		if (increment.isPresent()) {
			body = new Statement.Block(List.of(
				body,
				new Statement.Expression(increment.get())
			));
		}

		body = new Statement.While(condition, body);
		if (initializer.isPresent()) {
			body = new Statement.Block(List.of(
				initializer.get(),
				body
			));
		}

		return body;
	}

	private Statement.If ifStatement() {
		consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
		final var condition = expression();
		consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

		final var thenBranch = statement();
		final var elseBranch = match(TokenType.ELSE)
			? Optional.of(statement())
			: Optional.<Statement>empty();

		return new Statement.If(condition, thenBranch, elseBranch);
	}

	private Statement.Print printStatement() {
		final var value = expression();

		consume(TokenType.SEMICOLON, "Expect ';' after value.");

		return new Statement.Print(value);
	}

	private Statement.Return returnStatement() {
		final var keyword = previous();

		final var value = !check(TokenType.SEMICOLON)
			? Optional.of(expression())
			: Optional.<Expression>empty();

		consume(TokenType.SEMICOLON, "Expect ';' after return value.");

		return new Statement.Return(keyword, value);
	}

	private Statement.While whileStatement() {
		consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
		final var condition = expression();
		consume(TokenType.RIGHT_PAREN, "Expect ')' after while condition.");

		final var body = statement();

		return new Statement.While(condition, body);
	}

	private Statement.Expression expressionStatement() {
		final var expression = expression();

		consume(TokenType.SEMICOLON, "Expect ';' after expression.");

		return new Statement.Expression(expression);
	}

	private List<Statement> block() {
		final var statements = new ArrayList<Statement>();

		while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
			statements.add(declarationStatement());
		}

		consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");

		return statements;
	}

	private @NonNull Expression expression() {
		return assignmentExpression();
	}

	private Expression assignmentExpression() {
		final var expression = orExpression();

		if (match(TokenType.EQUAL)) {
			final var equals = previous();
			final var value = assignmentExpression();

			if (expression instanceof Expression.Variable(final var name)) {
				return new Expression.Assign(name, value);
			} else if (expression instanceof Expression.Get(final var object, final var name)) {
				return new Expression.Set(object, name, value);
			}

			throw error(equals, "Invalid assignment target.");
		}

		return expression;
	}

	private Expression orExpression() {
		var expression = andExpression();

		while (match(TokenType.OR)) {
			final var operator = previous();
			final var right = andExpression();

			expression = new Expression.Logical(expression, operator, right);
		}

		return expression;
	}

	private Expression andExpression() {
		var expression = equalityExpression();

		while (match(TokenType.AND)) {
			final var operator = previous();
			final var right = equalityExpression();

			expression = new Expression.Logical(expression, operator, right);
		}

		return expression;
	}

	private Expression equalityExpression() {
		var expression = comparisonExpression();

		while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
			final var operator = previous();
			final var right = comparisonExpression();

			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression comparisonExpression() {
		var expression = termExpression();

		while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
			final var operator = previous();
			final var right = termExpression();

			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression termExpression() {
		var expression = factorExpression();

		while (match(TokenType.MINUS, TokenType.PLUS)) {
			final var operator = previous();
			final var right = factorExpression();

			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression factorExpression() {
		var expression = unaryExpression();

		while (match(TokenType.SLASH, TokenType.STAR)) {
			final var operator = previous();
			final var right = unaryExpression();

			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression unaryExpression() {
		if (match(TokenType.BANG, TokenType.MINUS)) {
			final var operator = previous();
			final var right = unaryExpression();

			return new Expression.Unary(operator, right);
		}

		return callExpression();
	}

	private Expression callExpression() {
		var expression = primaryExpression();

		while (true) {
			if (match(TokenType.LEFT_PAREN)) {
				expression = finishCallExpression(expression);
			} else if (match(TokenType.DOT)) {
				final var name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");

				expression = new Expression.Get(expression, name);
			} else {
				break;
			}
		}

		return expression;
	}

	private Expression.Call finishCallExpression(Expression callee) {
		final var arguments = new ArrayList<Expression>();

		if (!check(TokenType.RIGHT_PAREN)) {
			do {
				if (arguments.size() >= 255) {
					throw error(peek(), "Can't have more than 255 arguments.");
				}

				arguments.add(expression());
			} while (match(TokenType.COMMA));
		}

		final var parenthesis = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");

		return new Expression.Call(callee, parenthesis, arguments);
	}

	private Expression primaryExpression() {
		if (match(TokenType.FALSE)) {
			return new Expression.Literal(new Literal.Boolean(false));
		}

		if (match(TokenType.TRUE)) {
			return new Expression.Literal(new Literal.Boolean(true));
		}

		if (match(TokenType.NIL)) {
			return new Expression.Literal(new Literal.Nil());
		}

		if (match(TokenType.IDENTIFIER)) {
			return new Expression.Variable(previous());
		}

		if (match(TokenType.STRING, TokenType.NUMBER)) {
			return new Expression.Literal(previous().literal());
		}

		if (match(TokenType.LEFT_PAREN)) {
			final var expression = expression();

			consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");

			return new Expression.Grouping(expression);
		}

		throw error(peek(), "Expect expression.");
	}

	private boolean match(TokenType type) {
		if (check(type)) {
			advance();
			return true;
		}

		return false;
	}

	private boolean match(TokenType... types) {
		for (final var type : types) {
			if (match(type)) {
				return true;
			}
		}

		return false;
	}

	private Token consume(TokenType type, String message) {
		if (check(type)) {
			return advance();
		}

		throw error(peek(), message);
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) {
			return false;
		}

		return type.equals(peek().type());
	}

	private Token advance() {
		if (!isAtEnd()) {
			++current;
		}

		return previous();
	}

	private boolean isAtEnd() {
		return TokenType.EOF.equals(peek().type());
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}

	private ParseError error(Token token, String message) {
		lox.error(token, message);

		throw new ParseError(token, message);
	}

	private void synchronize() {
		advance();

		while (!isAtEnd()) {
			if (previous().type() == TokenType.SEMICOLON) {
				return;
			}

			switch (peek().type()) {
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN: {
					return;
				}

				default: {}
			}

			advance();
		}
	}

}