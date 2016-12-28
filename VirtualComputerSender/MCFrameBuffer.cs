using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Timers;
using VirtualBox;

namespace VirtualComputerSender //Copyright © NorbiPeti 2015-2016
{
    public class MCFrameBuffer : IFramebuffer
    {
        private IDisplay Display;
        private Timer LastFullUpdateTimer;
        private UdpClient Client;

        public MCFrameBuffer(IDisplay display)
        {
            Display = display;
            LastFullUpdateTimer = new Timer();
            LastFullUpdateTimer.Interval = 5000;
            LastFullUpdateTimer.Elapsed += UpdateScreen;
            Client = new UdpClient();
            Client.Connect(new IPEndPoint(IPAddress.Loopback, 5896));
            LastFullUpdateTimer.Start();
        }

        private void UpdateScreen(object sender, EventArgs args)
        {
            Display.InvalidateAndUpdateScreen(0);
        }

        public IntPtr Address
        {
            get
            {
                return IntPtr.Zero;
            }
        }

        public const uint CBitsPerPixel = 32;
        public uint BitsPerPixel
        {
            get
            {
                return CBitsPerPixel;
            }
        }

        public uint BytesPerLine
        {
            get
            {
                return ScreenWidth;
            }
        }

        public uint Height
        {
            get
            {
                Console.WriteLine("Screen height queried.");
                return ScreenHeight;
            }
        }

        public uint HeightReduction
        {
            get
            {
                return 2;
            }
        }

        public IFramebufferOverlay Overlay
        {
            get
            {
                return null;
            }
        }

        public BitmapFormat PixelFormat
        {
            get
            {
                return BitmapFormat.BitmapFormat_RGBA;
            }
        }

        public uint Width
        {
            get
            {
                Console.WriteLine("Screen width queried.");
                return ScreenWidth;
            }
        }

        public long WinId
        {
            get
            {
                return 0;
            }
        }

        public Array Capabilities
        {
            get
            {
                return new FramebufferCapabilities[] { FramebufferCapabilities.FramebufferCapabilities_UpdateImage };
            }
        }

        public uint GetVisibleRegion(ref byte aRectangles, uint aCount)
        {
            throw new InvalidOperationException("This should not be used.");
        }

        public void NotifyUpdate(uint aX, uint aY, uint aWidth, uint aHeight)
        {

        }

        public void ProcessVHWACommand(ref byte aCommand)
        {
        }

        public void SetVisibleRegion(ref byte aRectangles, uint aCount)
        {
        }

        public int VideoModeSupported(uint aWidth, uint aHeight, uint aBpp)
        {
            return 1;
        }

        public const int ScreenWidth = 640;
        public const int ScreenHeight = 480;

        public volatile int[] Screen = new int[640 * 480];
        public void NotifyUpdateImage(uint aX, uint aY, uint aWidth, uint aHeight, Array aImage)
        {
            Task.Run(() =>
            {
                var img = (byte[])aImage;
                int x = 0;
                for (int j = (int)aY; j < aHeight && j < ScreenHeight; j++)
                {
                    for (int i = (int)aX; i < aWidth && i < ScreenWidth; i++)
                    {
                        if (x + 4 > aImage.Length)
                            return;
                        Screen[640 * j + i] = Color.FromArgb(img[x + 2], img[x + 1], img[x]).ToArgb(); //TODO: Touchscreen and/or left click right click forwarding
                        x += 4;
                    }
                    x += (int)aX * 4;
                    int add = ((int)(aX + aWidth) - ScreenWidth);
                    if (add > 0)
                        x += add * 4;
                }
            });
        }

        public void NotifyChange(uint aScreenId, uint aXOrigin, uint aYOrigin, uint aWidth, uint aHeight)
        {
        }

        public void Notify3DEvent(uint aType, Array aData)
        {
        }
    }
}
