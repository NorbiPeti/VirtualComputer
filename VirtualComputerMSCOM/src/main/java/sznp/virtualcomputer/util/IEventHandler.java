package sznp.virtualcomputer.util;

import com.jacob.com.Dispatch;
import lombok.var;
import org.virtualbox_6_1.IEvent;

public interface IEventHandler extends virtualcomputerwindows.IEventHandler {
	void handleEvent(IEvent iEvent);

	@Override
	default void handleEvent(long l) {
		var dp = new Dispatch();
		dp.m_pDispatch = l;
		handleEvent(new IEvent(dp));
	}
}
