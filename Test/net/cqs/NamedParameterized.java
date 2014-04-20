package net.cqs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * A slight variation of the Parameterized Runner, with better name display.
 */
@SuppressWarnings("boxing")
public class NamedParameterized extends Suite {
	private class TestClassRunnerForParameters extends BlockJUnit4ClassRunner {
		private final String fName;

		private final List<Object[]> fParameterList;

		private final int fParameterSetNumber;

		TestClassRunnerForParameters(Class<?> testClass, List<Object[]> parameterList, int i) throws InitializationError {
			super(testClass);
			fName = testClass.getSimpleName();
			fParameterList= parameterList;
			fParameterSetNumber= i;
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(
					computeParams());
		}

		private Object[] computeParams() throws Exception {
			try {
				return fParameterList.get(fParameterSetNumber);
			} catch (ClassCastException e) {
				throw new Exception(String.format(
						"%s.%s() must return a Collection of arrays.",
						getTestClass().getName(), getParametersMethod(
								getTestClass()).getName()));
			}
		}

		@Override
		protected String getName()
		{
			StringBuilder result = new StringBuilder();
			result.append(fName);
			result.append('[');
			result.append(fParameterSetNumber);
			result.append("] - ");
			result.append(fParameterList.get(fParameterSetNumber)[0]);
			return result.toString();
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s] - %s", method.getName(), fParameterSetNumber,
					fParameterList.get(fParameterSetNumber)[0]);
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}
		
		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface Parameters
	{/* Annotation */}

	private final ArrayList<Runner> runners = new ArrayList<Runner>();

	public NamedParameterized(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
		List<Object[]> parametersList= getParametersList(getTestClass());
		for (int i= 0; i < parametersList.size(); i++)
			runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(),
					parametersList, i));
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList(TestClass klass)
			throws Throwable {
		return (List<Object[]>) getParametersMethod(klass).invokeExplosively(
				null);
	}

	private FrameworkMethod getParametersMethod(TestClass testClass)
			throws Exception {
		List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);
		for (FrameworkMethod each : methods) {
			int modifiers = each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class " + testClass.getName());
	}
}
