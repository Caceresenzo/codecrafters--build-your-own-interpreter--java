package interpreter.evaluating;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;

import interpreter.Lox;
import interpreter.evaluating.value.LoxInstance;
import interpreter.evaluating.value.LoxClass;
import interpreter.evaluating.value.LoxBoolean;
import interpreter.evaluating.value.LoxCallable;
import interpreter.evaluating.value.LoxFunction;
import interpreter.evaluating.value.LoxNil;
import interpreter.evaluating.value.LoxNumber;
import interpreter.evaluating.value.LoxString;
import interpreter.evaluating.value.LoxValue;
import interpreter.grammar.Token;
import interpreter.grammar.TokenType;
import interpreter.parser.Expression;
import interpreter.parser.Expression.Call;
import interpreter.parser.Expression.Logical;
import interpreter.parser.Statement;
import interpreter.util.DoubleOperators;
import interpreter.util.function.DoubleComparisonOperator;
import lombok.NonNull;

public class Interpreter implements Expression.Visitor<LoxValue>, Statement.Visitor<Void> {

	private final Lox lox;
	private final Environment globals = new Environment();
	private final Map<Expression, Integer> locals = new IdentityHashMap<>();
	private Environment environment = globals;

	public Interpreter(
		@NonNull Lox lox
	) {
		this.lox = lox;

		this.globals.defineFunction(new SimpleNativeFunction("clock", 0, (__) -> new LoxNumber(System.currentTimeMillis() / 1000)));
	}

	public void interpret(List<Statement> statements) {
		try {
			for (final var statement : statements) {
				execute(statement);
			}
		} catch (RuntimeError error) {
			lox.reportRuntime(error.token().line(), error.getMessage());
		}
	}

	public void interpret(Expression expression) {
		try {
			final var value = evaluate(expression);
			System.out.println(value.format());
		} catch (RuntimeError error) {
			lox.reportRuntime(error.token().line(), error.getMessage());
		}
	}

	public void executeBlock(@NonNull List<Statement> statements, Environment environment) {
		final var previous = this.environment;

		try {
			this.environment = environment;

			for (final var statement : statements) {
				execute(statement);
			}
		} finally {
			this.environment = previous;
		}
	}

	public void execute(Statement statement) {
		visit(statement);
	}

	public LoxValue evaluate(Expression expression) {
		return visit(expression);
	}

	@Override
	public Void visitExpression(Statement.Expression expression) {
		evaluate(expression.expression());

		return null;
	}

	@Override
	public Void visitPrint(Statement.Print print) {
		final var value = evaluate(print.expression());
		System.out.println(value.format());

		return null;
	}

	@Override
	public Void visitVariable(Statement.Variable variable) {
		final var value = variable.initializer()
			.map(this::evaluate)
			.orElse(LoxNil.INSTANCE);

		environment.define(variable.name().lexeme(), value);

		return null;
	}

	@Override
	public Void visitBlock(Statement.Block block) {
		executeBlock(block.statements(), environment.inner());

		return null;
	}

	@Override
	public Void visitIf(Statement.If if_) {
		if (isTruthy(evaluate(if_.condition()))) {
			execute(if_.thenBranch());
		} else {
			if_.elseBranch().ifPresent(this::execute);
		}

		return null;
	}

	@Override
	public Void visitWhile(Statement.While while_) {
		while (isTruthy(evaluate(while_.condition()))) {
			execute(while_.body());
		}

		return null;
	}

	@Override
	public Void visitFunction(Statement.Function function) {
		final var callable = new LoxFunction(function, environment);

		environment.defineFunction(callable);

		return null;
	}

	@Override
	public LoxValue visitLiteral(Expression.Literal literal) {
		return literal.value().toValue();
	}

	@Override
	public LoxValue visitGrouping(Expression.Grouping grouping) {
		return evaluate(grouping.expression());
	}

	@Override
	public LoxValue visitUnary(Expression.Unary unary) {
		final var right = evaluate(unary.right());

		return switch (unary.operator().type()) {
			case BANG -> LoxBoolean.valueOf(!isTruthy(right));
			case MINUS -> {
				if (right instanceof LoxNumber(final var value)) {
					yield new LoxNumber(-value);
				}

				throw new RuntimeError("Operand must be a number.", unary.operator());
			}
			default -> throw new UnsupportedOperationException();
		};
	}

	@Override
	public LoxValue visitBinary(Expression.Binary binary) {
		final var left = evaluate(binary.left());
		final var right = evaluate(binary.right());

		final var operatorToken = binary.operator();
		return switch (operatorToken.type()) {
			case MINUS -> applyNumberOperator(left, operatorToken, right, DoubleOperators::substract);
			case PLUS -> {
				if (left instanceof LoxNumber(final var leftValue) && right instanceof LoxNumber(final var rightValue)) {
					yield new LoxNumber(leftValue + rightValue);
				}

				if (left instanceof LoxString(final var leftValue) && right instanceof LoxString(final var rightValue)) {
					yield new LoxString(leftValue + rightValue);
				}

				throw new RuntimeError("Operands must be two numbers or two strings.", operatorToken);
			}
			case SLASH -> applyNumberOperator(left, operatorToken, right, DoubleOperators::divide);
			case STAR -> applyNumberOperator(left, operatorToken, right, DoubleOperators::multiply);
			case GREATER -> applyNumberOperator(left, operatorToken, right, DoubleOperators::greaterThan);
			case GREATER_EQUAL -> applyNumberOperator(left, operatorToken, right, DoubleOperators::greaterThanOrEqual);
			case LESS -> applyNumberOperator(left, operatorToken, right, DoubleOperators::lessThan);
			case LESS_EQUAL -> applyNumberOperator(left, operatorToken, right, DoubleOperators::lessThanOrEqual);
			case BANG_EQUAL -> LoxBoolean.valueOf(!left.equals(right));
			case EQUAL_EQUAL -> LoxBoolean.valueOf(left.equals(right));
			default -> throw new UnsupportedOperationException();
		};
	}

	@Override
	public LoxValue visitAssign(Expression.Assign assign) {
		final var value = evaluate(assign.value());

		final var distance = locals.get(assign);
		if (distance != null) {
			environment.assignAt(distance, assign.name(), value);
		} else {
			globals.assign(assign.name(), value);
		}

		return value;
	}

	@Override
	public LoxValue visitVariable(Expression.Variable variable) {
		return lookUpVariable(variable.name(), variable);
	}

	@Override
	public LoxValue visitLogical(Logical logical) {
		final var left = evaluate(logical.left());

		if (TokenType.OR.equals(logical.operator().type())) {
			if (isTruthy(left)) {
				return left;
			}
		} else {
			if (!isTruthy(left)) {
				return left;
			}
		}

		return evaluate(logical.right());
	}

	@Override
	public LoxValue visitCall(Call call) {
		final var callee = evaluate(call.callee());

		final var arguments = call.arguments()
			.stream()
			.map(this::evaluate)
			.toList();

		if (!(callee instanceof LoxCallable callable)) {
			throw new RuntimeError("Can only call functions and classes.", call.parenthesis());
		}

		if (callable.arity() != arguments.size()) {
			throw new RuntimeError("Expected %d arguments but got %s.".formatted(callable.arity(), arguments.size()), call.parenthesis());
		}

		return callable.call(this, arguments);
	}

	@Override
	public Void visitReturn(Statement.Return return_) {
		final var value = return_.value()
			.map(this::evaluate)
			.orElse(LoxNil.INSTANCE);

		throw new Return(value);
	}

	@Override
	public Void visitClass(Statement.Class class_) {
		environment.define(class_.name().lexeme(), null);

		final var methods = new HashMap<String, LoxFunction>();
		for (final var method : class_.methods()) {
			final var function = new LoxFunction(method, environment);
			methods.put(method.name().lexeme(), function);
		}

		final var klass = new LoxClass(class_.name().lexeme(), methods);
		environment.assign(class_.name(), klass);

		return null;
	}

	@Override
	public LoxValue visitGet(Expression.Get get) {
		final var object = evaluate(get.object());

		if (object instanceof LoxInstance instance) {
			return instance.get(get.name());
		}

		throw new RuntimeError("Only instances have properties.", get.name());
	}

	@Override
	public LoxValue visitSet(Expression.Set set) {
		final var object = evaluate(set.object());
		if (!(object instanceof LoxInstance instance)) {
			throw new RuntimeError("Only instances have fields.", set.name());
		}

		final var value = evaluate(set.value());
		instance.set(set.name(), value);

		return value;
	}

	@Override
	public LoxValue visitThis(Expression.This this_) {
		return lookUpVariable(this_.keyword(), this_);
	}

	public void resolve(Expression expression, int depth) {
		locals.put(expression, depth);
	}

	public LoxValue lookUpVariable(Token name, Expression expression) {
		final var distance = locals.get(expression);

		if (distance != null) {
			return environment.getAt(distance, name.lexeme());
		}

		return globals.get(name);
	}

	public boolean isTruthy(LoxValue value) {
		return switch (value) {
			case LoxNil __ -> false;
			case LoxBoolean boolean_ -> boolean_.value();
			default -> true;
		};
	}

	private LoxNumber applyNumberOperator(LoxValue left, Token token, LoxValue right, DoubleBinaryOperator operator) {
		if (left instanceof LoxNumber(final var leftValue) && right instanceof LoxNumber(final var rightValue)) {
			return new LoxNumber(operator.applyAsDouble(leftValue, rightValue));
		}

		throw new RuntimeError("Operands must be numbers.", token);
	}

	private LoxBoolean applyNumberOperator(LoxValue left, Token token, LoxValue right, DoubleComparisonOperator operator) {
		if (left instanceof LoxNumber(final var leftValue) && right instanceof LoxNumber(final var rightValue)) {
			return LoxBoolean.valueOf(operator.applyAsDouble(leftValue, rightValue));
		}

		throw new RuntimeError("Operands must be numbers.", token);
	}

	public Lox lox() {
		return lox;
	}

	public Environment environment() {
		return environment;
	}

}