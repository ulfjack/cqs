package net.cqs.main.resource;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {

  long lastModified();
  InputStream getInputStream() throws IOException;
}
