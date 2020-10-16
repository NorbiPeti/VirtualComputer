package org.virtualbox_6_1;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MouseButtonState {
	LeftButton(1),
	RightButton(2),
	MiddleButton(4),
	WheelUp(8),
	WheelDown(16),
	XButton1(32),
	XButton2(64),
	MouseStateMask(127);

	private final int value;

	public int value() {
		return value;
	}
}
