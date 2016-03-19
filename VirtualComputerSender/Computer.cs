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
            //if (machine.State == MachineState.MachineState_PoweredOff) //2016.02.09.
            //{
            var progress = machine.LaunchVMProcess(session, "headless", "");
            progress.WaitForCompletion(100000);
            //}
            //else if (machine.State == MachineState.MachineState_Paused || machine.State == MachineState.MachineState_Running)
            //machine.LockMachine(session, LockType.LockType_Write); //2016.02.09.
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

        //private Color[,] Screen;
        //private byte[][][] Screen;
        private volatile int[] Screen; //<-- volatile: 2016.02.20.

        /*public byte[][][] GetScreen()
        {
            return Screen;
        }*/

        /*public byte GetScreenPixelColor(int x, int y, int rgb)
        {
            return Screen[x][y][rgb];
        }*/

        public int[] GetScreenPixelColors()
        {
            if (Screen == null)
            {
                Screen = new int[640 * 480];
                for (int i = 0; i < Screen.Length; i++)
                    Screen[i] = Color.Black.ToArgb();
            }
            return Screen; //TO!DO: Pass events to plugin
        }

        [DllImport("user32.dll")]
        static extern uint MapVirtualKey(uint uCode, uint uMapType); //2016.02.10.

        const uint MAPVK_VK_TO_VSC = 0x00;
        const uint MAPVK_VSC_TO_VK = 0x01;
        const uint MAPVK_VK_TO_CHAR = 0x02;
        const uint MAPVK_VSC_TO_VK_EX = 0x03;
        const uint MAPVK_VK_TO_VSC_EX = 0x04;

        /*private bool Shift = false;
        private bool Ctrl = false;
        private bool Alt = false;*/

        public void PressKey(string key, short durationorstate) //durationstate: 2016.02.22.
        {
            if (session.State == SessionState.SessionState_Locked)
            {
                //session.Console.Keyboard.ReleaseKeys();
                int code = 0;
                if (key == "testall")
                {
                    int x = 0;
                    session.Console.Keyboard.PutScancodes(new int[128].Select(i => x++).ToArray());
                    return;
                }
                //switch (key.ToLower()[0])
                //switch(key.ToLower())
                //{
                //case 'a':
                //code = 65;
                //code = 31; //2016.02.09.
                //code = 1 | (int)Keys.A; //2016.02.09.
                //code = 128 | (int)Keys.A; //2016.02.10.
                //code = BitConverter.ToInt32(new byte[4] { (byte)Keys.A, 0x00, 0x00, 0x01 }, 0);
                //code = 0x41;
                //code = (int)MapVirtualKey(0x41, MAPVK_VK_TO_VSC); //SUCCESS - 2016.02.10.
                //Virtual key code taken from Kennedy.ManagedHooks project
                //Release key scan code concept taken from VirtualBox source code (KeyboardImpl.cpp:putCAD())
                //+128
                code = (int)MapVirtualKey((uint)(VirtualKeys)Enum.Parse(typeof(VirtualKeys), key, true), MAPVK_VK_TO_VSC); //2016.02.11.
                int codeShift = (int)MapVirtualKey((uint)VirtualKeys.ShiftLeft, MAPVK_VK_TO_VSC); //2016.02.22.
                int codeCtrl = (int)MapVirtualKey((uint)VirtualKeys.ControlLeft, MAPVK_VK_TO_VSC); //2016.02.22.
                int codeAlt = (int)MapVirtualKey((uint)VirtualKeys.AltLeft, MAPVK_VK_TO_VSC); //2016.02.22.
                //Console.WriteLine("Key: " + key + " - Code: " + code); //2016.02.11.
                /*bool release = true; //2016.02.11.
                if ((key.ToLower() == "shiftleft" || key.ToLower() == "shiftright"))
                { //2016.02.11.
                    if (!Shift)
                    {
                        Shift = true;
                        release = false;
                    }
                    else
                        Shift = false;
                }
                else if ((key.ToLower() == "controlleft" || key.ToLower() == "controlright"))
                { //2016.02.11.
                    if (!Ctrl)
                    {
                        Ctrl = true;
                        release = false;
                    }
                    else
                        Ctrl = false;
                }
                else if ((key.ToLower() == "altleft" || key.ToLower() == "altright"))
                { //2016.02.11.
                    if (!Alt)
                    {
                        Alt = true;
                        release = false;
                    }
                    else
                        Alt = false;
                }*/
                //break;
                //default:
                //break;
                //}
                //Console.WriteLine("Code. " + code);
                if (durationorstate != -2) //<-- 2016.02.22.
                    session.Console.Keyboard.PutScancode(code);
                //if (release)
                if (durationorstate == 0 || durationorstate == -2) //<-- 2016.02.22.
                    session.Console.Keyboard.PutScancodes(new int[] { code + 128, codeCtrl + 128, codeShift + 128, codeAlt + 128 }); //2016.02.11. - Shift, etc.: 2016.02.22.
                if (durationorstate > 0)
                { //2016.02.22.
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
        
        //public void UpdateMouse(int x, int y, int z, int w, int mbs) //MouseButtonState --> int: 2016.02.12.
        public void UpdateMouse(int x, int y, int z, int w, string mbs) //int --> string: 2016.02.22.
        {
            if (session.State != SessionState.SessionState_Locked)
                return; //2016.02.27.
            int state = 0; //<-- 2016.02.22.
            if (mbs.Length > 0) //<-- 2016.02.22.
                state = (int)(MouseBS)Enum.Parse(typeof(MouseBS), mbs, true);
            session.Console.Mouse.PutMouseEvent(x, y, z, w, state);
        }

        public void FixScreen()
        {
            session.Console.Display.SetSeamlessMode(0);
            session.Console.Display.SetVideoModeHint(0, 1, 0, 0, 0, 640, 480, 32);
        }

        ~Computer()
        { //2016.02.09.
            if (session.State == SessionState.SessionState_Locked)
                //session.Machine.SaveState().WaitForCompletion(10000);
                session.Machine.SaveState(); //2016.02.20.
        }
    }
}
