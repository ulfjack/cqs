package net.cqs.startup;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Main
{

	private static class ClassLoaderBuilder
	{
		private final List<URL> urls = new ArrayList<URL>();
		public ClassLoader build()
		{ return new URLClassLoader(urls.toArray(new URL[0])); }
		public ClassLoaderBuilder addFile(File file) throws MalformedURLException
		{
			if (file.exists())
				urls.add(file.toURI().toURL());
			return this;
		}
		public ClassLoaderBuilder addDirectory(File directory) throws MalformedURLException
		{
			if (directory.exists())
			{
				File[] fs = directory.listFiles(new FileFilter()
					{
						@Override
            public boolean accept(File pathname)
						{ return pathname.getName().endsWith(".jar"); }
					});
				for (File f : fs)
					urls.add(f.toURI().toURL());
			}
			return this;
		}
	}

public static void main(String[] args) throws Exception
{
	ClassLoaderBuilder builder = new ClassLoaderBuilder();
	
	String mainClassName;
	String[] mainArgs;
	if ((args.length >= 1) && "test".equals(args[0]))
	{
		builder.addFile(new File("Classes/"))
			.addFile(new File("Build/"))
			.addDirectory(new File("Libraries/"))
			.addDirectory(new File("Libraries/Testing/"));
		mainClassName = "org.junit.runner.JUnitCore";
		mainArgs = new String[] { "net.cqs.CqsTestSuite" };
	}
	else
	{
		builder.addFile(new File("Classes/"))
//			.addFile(new File("../catfish/Classes/"))
			.addDirectory(new File("Libraries/"))
			.addFile(new File("cqs.jar"));
		mainClassName = "net.cqs.main.Main";
		mainArgs = args;
	}
	
	ClassLoader classLoader = builder.build();
	Class<?> mainClass = classLoader.loadClass(mainClassName);
	Method m = mainClass.getDeclaredMethod("main", new Class[] { String[].class });
	try
	{
		m.invoke(null, new Object[] { mainArgs });
	}
	catch (InvocationTargetException e)
	{
		if (e.getCause() != null)
			e.getCause().printStackTrace();
		else
			e.printStackTrace();
	}
}

}
