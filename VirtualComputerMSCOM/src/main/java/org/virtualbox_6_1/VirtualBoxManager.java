package org.virtualbox_6_1;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VirtualBoxManager {
	private static VirtualBoxManager instance;
	private final String path;
	private final IVirtualBox vbox = new VirtualBoxClass();
	private final ISession session = new SessionClass();

	public static VirtualBoxManager createInstance(String path) {
		return instance = new VirtualBoxManager(path);
	}

	public IVirtualBox getVBox() {
		return vbox;
	}

	public ISession getSessionObject() {
		return session;
	}
}
