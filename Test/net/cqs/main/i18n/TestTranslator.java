package net.cqs.main.i18n;

import net.cqs.i18n.Translation;
import net.cqs.i18n.Translator;

public interface TestTranslator extends Translator
{

@Translation("Get some {0} for {1,number,integer}.")
void someMessage(String value, int number);

}
