package net.cqs.web.game;

import net.cqs.services.Service;

public interface CssCompressorService extends Service
{

String requestCssId(String... cssFiles);

}
