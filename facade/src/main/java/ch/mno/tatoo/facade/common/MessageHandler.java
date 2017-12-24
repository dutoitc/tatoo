package ch.mno.tatoo.facade.common;

/**
 * Created by dutoitc on 22/01/16.
 */
public interface MessageHandler {

	/** Implementations should listen to known message type; 'content' should be human-readable and complete message. */
	void handleMessage(String type, String content);

}
