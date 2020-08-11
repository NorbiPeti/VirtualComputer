package sznp.virtualcomputer.util;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public abstract class COMObjectBase extends Dispatch {
    public COMObjectBase() {
        super("clsid:{67099191-32E7-4F6C-85EE-422304C71B90}");
    }
}
