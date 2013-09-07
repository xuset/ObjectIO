package objectIO.objectCreate;

import objectIO.markupMsg.MarkupMsg;

public abstract class ObjectCreator<T> {
	public abstract T create(MarkupMsg data);
}
