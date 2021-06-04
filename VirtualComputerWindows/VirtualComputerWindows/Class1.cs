using System;
using VirtualBox;

namespace VirtualComputerWindows
{
    public static class Class1
    {
        public static void Main(string[] args)
        {
            Console.WriteLine("Starting VirtualBox...");
            var vbox = new VirtualBoxClass();
            var machines = vbox.Machines.As<IMachine>();
            var session = new SessionClass();
            //machines[0].LockMachine(session, LockType.LockType_VM);
            //var prog = session.Console.PowerUp(); - Unknown error initializing kernel driver (VERR_SUPDRV_NOT_BUDDING_VM_PROCESS_1)
            var prog = machines[0].LaunchVMProcess(session, "headless", Array.Empty<string>());
            prog.WaitForCompletion(-1);
            Console.WriteLine("Error: " + prog.ErrorInfo?.Text);
            session.Console.Display.AttachFramebuffer(0, new MCFramebuffer());
            Console.WriteLine("All set up");
            Console.ReadLine();
        }

        public static T[] As<T>(this Array array) => (T[]) array;
    }
}
