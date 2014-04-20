package net.cqs.web.game;

import net.cqs.services.Service;

public interface JsCompressorService extends Service
{

String requestJsId(String... jsFiles);

}
