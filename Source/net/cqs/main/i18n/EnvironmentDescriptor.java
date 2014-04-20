package net.cqs.main.i18n;

import net.cqs.main.resource.ResourceManager;
import de.ofahrt.ulfscript.Environment;
import de.ofahrt.ulfscript.SourceProvider;

public interface EnvironmentDescriptor<T extends Environment>
{

String getName();
String getPrefix();
String getFilePattern();
String bundleName();
T newInstance(SourceProvider provider);
T newInstance(ResourceManager resourceManager);

}
