package net.cqs.web.action;

import net.cqs.config.ErrorCode;
import net.cqs.storage.GalaxyTask;

public interface TaskHandler
{

void execute(GalaxyTask task);
void log(ErrorCode code);

}
