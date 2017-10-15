using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using VirtualBox;

namespace VirtualComputerWindows
{
    public static class Exports
    {
        [DllExport]
        public static VirtualBoxClass Init()
        {
            var vbox = new VirtualBoxClass();
            return vbox;
            var machine = vbox.Machines.GetValue(0) as IMachine;
        }
    }
}
