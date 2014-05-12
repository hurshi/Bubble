package gavin.i_bubble.utils;

import gavin.i_bubble.manager.ChatManager;

public class ChatManagerUtil {
	private volatile static ChatManagerUtil uniqueInstance;
	private ChatManager c;
	private boolean isGroupOwner = false;

	private ChatManagerUtil() {
	}

	public static ChatManagerUtil getInstance() {
		if (uniqueInstance == null) {
			synchronized (ChatManagerUtil.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new ChatManagerUtil();
				}
			}
		}
		return uniqueInstance;
	}

	public ChatManager getChatManager() {
		return c;
	}

	public void setChatManager(ChatManager c) {
		this.c = c;
	}

	public void setIsGroupOwner(boolean isGroupOwner) {
		this.isGroupOwner = isGroupOwner;
	}

	public boolean getIsGourpOwner() {
		return isGroupOwner;
	}

}
