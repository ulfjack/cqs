package net.cqs.engine.messages;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.cqs.config.InfoEnum;
import net.cqs.config.QueueEnum;
import net.cqs.engine.Position;
import net.cqs.i18n.Translation;
import net.cqs.i18n.Translator;

public interface InfoTranslator extends Translator
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface OldMeta
	{
		InfoEnum value();
	}

@Translation("Colony {0}: {1} is empty.")
@OldMeta(InfoEnum.QUEUE_EMPTY)
void queueEmpty(Position position, QueueEnum queueEnum);

}
