﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using VirtualBox;

namespace VirtualComputerWindows
{
    public static class Exports
    {
        [DllImport(@"C:\Program Files\Oracle\VirtualBox\VBoxRT.dll", CallingConvention = CallingConvention.StdCall)]
        private static extern void RTR3InitExe(int argc, string argv, int zeroflags);
        /*[DllImport(@"C:\Program Files\Oracle\VirtualBox\VBoxVMM.dll", CallingConvention = CallingConvention.StdCall)]
        private static extern int VMMDoHmTest(IntPtr vmstruct);*/

        private static VirtualBoxClient vbc;

        public static async Task<IVirtualBox> Init() => await Task.Run(() =>
        {
            try
            {
                //Environment.SetEnvironmentVariable("VBOX_HOME", @"C:\Program Files\Oracle\VirtualBox\");
                //TODO: Only finds the VBoxVMM.dll when placed in the VirtualBox dir (regardless of working dir)
                //Even then there are hardening issues: VERR_SUPDRV_NOT_BUDDING_VM_PROCESS_1
                //https://www.virtualbox.org/svn/vbox/trunk/src/VBox/HostDrivers/Support/win/SUPDrv-win.cpp
                vbc = new VirtualBoxClientClass();
                var vbox = vbc.VirtualBox;
                RTR3InitExe(0, "", 0);
                var ses = vbc.Session;
                var machine = vbox.Machines.GetValue(0) as IMachine;
                ses.Name = "minecraft";
                machine.LockMachine(ses, LockType.LockType_VM);
                Console.WriteLine("Locking...");
                while (ses.State != SessionState.SessionState_Locked) ;
                Console.WriteLine("Locked");
                machine = ses.Machine;
                Console.WriteLine("Powering up...");
                ses.Console.PowerUp().WaitForCompletion(10000);
                Console.WriteLine("Framebuffer attach");
                ses.Console.Display.AttachFramebuffer(0, new VBFB(ses.Console.Display));
                return vbox;
            }
            catch(Exception e)
            {
                Console.WriteLine(e);
                Console.ReadLine();
                return null;
            }
        });

        public static void Main()
        {
            Init();
            Console.ReadLine();
        }
    }
}
