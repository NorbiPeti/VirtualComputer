package sznp.virtualcomputer;

@Deprecated
public enum MouseButtonState
{
	LeftButton(1), MiddleButton(4), MouseStateMask(127), RightButton(2), WheelDown(
			16), WheelUp(8), XButton1(32), XButton2(64), Null(0);

	private final int val;

	MouseButtonState(int val)
	{
		this.val = val;
	}

	public int getValue()
	{
		return val;
	}
}
