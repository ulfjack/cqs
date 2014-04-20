package net.cqs.main.resource;

import net.cqs.services.Service;
import de.ofahrt.ulfscript.SourceProvider;

public interface ResourceManager extends Service
{

Resource getResource(String name);
String[] listResources(String path, ResourceNameFilter filter);
SourceProvider getSourceProvider(String prefix);

}
