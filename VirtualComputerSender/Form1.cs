using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using VirtualBox;

namespace VirtualComputerSender //Copyright © NorbiPeti 2015-2016
{
    public partial class Form1: Form
    {
        private static Form1 Instance;
        public Form1()
        {
            Instance = this;
            InitializeComponent();
            Screen = panel1.CreateGraphics();
            var vbox = new VirtualBoxClass();
            var session = new Session();
            var machine = (IMachine)vbox.Machines.GetValue(0);
            var progress = machine.LaunchVMProcess(session, "headless", "");
            progress.WaitForCompletion(100000);
            session.Console.Display.AttachFramebuffer(0, new NetFrameBuffer(session.Console.Display));
        }

        public static Graphics Screen;
    }
}
