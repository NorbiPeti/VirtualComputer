package sznp.virtualcomputer.util;

import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;

public abstract class COMObjectBase implements nsISupports {
    public nsISupports queryInterface(String id) {
        return Mozilla.queryInterface(this, id);
    }

}
