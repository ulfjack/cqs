package net.cqs.main.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.io.TextSource;

public class ClasspathResourceManager implements ResourceManager {

  private final ClassLoader classLoader = ClasspathResourceManager.class.getClassLoader();

  public ClasspathResourceManager() {
  }

  @Override
  public Resource getResource(final String name) {
  	final InputStream in = classLoader.getResourceAsStream(name);
    if (in == null) {
      return null;
    }
    return new Resource() {
      @Override
      public InputStream getInputStream() throws IOException {
        return classLoader.getResourceAsStream(name);
      }
  
      @Override
      public long lastModified() {
        return 0;
      }
    };
  }

  @Override
  public String[] listResources(String path, final ResourceNameFilter filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public SourceProvider getSourceProvider(final String prefix) {
  	return new SourceProvider() {
			@Override
      public TextSource getSource(final String name) {
			  String entryName = prefix + name;
        entryName = entryName.replaceAll("/[^/]+/../", "/");
			  final Resource resource = getResource(entryName);
				if (resource == null) {
				  return null;
				}
				return new TextSource() {
					@Override
					public Reader getReader() throws IOException {
					  return new InputStreamReader(resource.getInputStream(), Charset.forName("UTF-8"));
					}

					@Override
					public String getName() {
					  return name;
					}

					@Override
					public long lastModified() {
					  return resource.lastModified();
					}
				};
			}
		};
  }
}
