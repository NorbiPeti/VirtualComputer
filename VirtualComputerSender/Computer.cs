using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using VirtualBox;

namespace VirtualComputerSender //Copyright © NorbiPeti 2015-2016
{
    public class Computer
    { //Self-note: Don't add/edit public members
        private VirtualBoxClass vbox;
        private Session session;
        public Computer()
        {
            vbox = new VirtualBoxClass();
            session = new Session();
        }

        public void Start()
        {
            var machine = (IMachine)vbox.Machines.GetValue(0);
            var progress = machine.LaunchVMProcess(session, "headless", "");
            progress.WaitForCompletion(100000);
            var fb = new MCFrameBuffer(session.Console.Display);
            Screen = fb.Screen; //fb.Screen is assigned on object creation
            session.Console.Display.AttachFramebuffer(0, fb);
            session.Console.Display.SetSeamlessMode(0);
        }

        public bool PowerButton()
        {
            if (session.State != SessionState.SessionState_Locked || session.Machine == null)
            {
                Start();
                return true;
            }
            else
            {
                session.Console.PowerButton();
                if (session.State != SessionState.SessionState_Locked)
                    Screen = null;
                return false;
            }
        }

        public void PowerOff()
        {
            if (session.State == SessionState.SessionState_Locked)
            {
                session.Console.PowerDown().WaitForCompletion(10000);
                Screen = null;
            }
        }

        public void Reset()
        {
            if (session.State == SessionState.SessionState_Locked)
                session.Console.Reset();
        }

        private volatile int[] Screen;

        public int[] GetScreenPixelColors()
        {
            if (Screen == null)
            {
                Screen = new int[640 * 480];
                for (int i = 0; i < Screen.Length; i++)
                    Screen[i] = Color.Black.ToArgb();
            }
            return Screen;
        }

        [DllImport("user32.dll")]
        static extern uint MapVirtualKey(uint uCode, uint uMapType);

        const uint MAPVK_VK_TO_VSC = 0x00;
        const uint MAPVK_VSC_TO_VK = 0x01;
        const uint MAPVK_VK_TO_CHAR = 0x02;
        const uint MAPVK_VSC_TO_VK_EX = 0x03;
        const uint MAPVK_VK_TO_VSC_EX = 0x04;

        public void PressKey(string key, short durationorstate)
        {
            if (session.State == SessionState.SessionState_Locked)
            {
                int code = 0;
                if (key == "testall")
                {
                    int x = 0;
                    session.Console.Keyboard.PutScancodes(new int[128].Select(i => x++).ToArray());
                    return;
                }
                //Virtual key code taken from Kennedy.ManagedHooks project
                //Release key scan code concept taken from VirtualBox source code (KeyboardImpl.cpp:putCAD())
                //+128
                code = (int)MapVirtualKey((uint)(VirtualKeys)Enum.Parse(typeof(VirtualKeys), key, true), MAPVK_VK_TO_VSC);
                int codeShift = (int)MapVirtualKey((uint)VirtualKeys.ShiftLeft, MAPVK_VK_TO_VSC);
                int codeCtrl = (int)MapVirtualKey((uint)VirtualKeys.ControlLeft, MAPVK_VK_TO_VSC);
                int codeAlt = (int)MapVirtualKey((uint)VirtualKeys.AltLeft, MAPVK_VK_TO_VSC);
                if (durationorstate != -2) //<-- 2016.02.22.
                    session.Console.Keyboard.PutScancode(code);
                if (durationorstate == 0 || durationorstate == -2)
                    session.Console.Keyboard.PutScancodes(new int[] { code + 128, codeCtrl + 128, codeShift + 128, codeAlt + 128 });
                if (durationorstate > 0)
                {
                    Timer t = new Timer();
                    t.Tick += delegate
                    {
                        session.Console.Keyboard.PutScancode(code + 128);
                        t.Stop();
                    };
                    t.Interval = durationorstate;
                    t.Start();
                }
            }
        }
        
        public void UpdateMouse(int x, int y, int z, int w, string mbs)
        {
            if (session.State != SessionState.SessionState_Locked)
                return;
            int state = 0;
            if (mbs.Length > 0)
                state = (int)(MouseBS)Enum.Parse(typeof(MouseBS), mbs, true);
            session.Console.Mouse.PutMouseEvent(x, y, z, w, state);
        }

        public void FixScreen()
        {
            session.Console.Display.SetSeamlessMode(0);
            session.Console.Display.SetVideoModeHint(0, 1, 0, 0, 0, 640, 480, 32);
        }

        ~Computer()
        {
            if (session.State == SessionState.SessionState_Locked)
                session.Machine.SaveState();
        }
    }
}
