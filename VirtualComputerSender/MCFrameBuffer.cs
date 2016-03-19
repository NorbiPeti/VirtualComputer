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
            LastFullUpdateTimer.Interval = 5000; //60s --> 5s: 2016.02.20.
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

        //public byte[][][] Screen = CreateJaggedArray<byte[][][]>(640, 480, 3);
        public volatile int[] Screen = new int[640 * 480]; //volatile: 2016.02.20.
        public void NotifyUpdateImage(uint aX, uint aY, uint aWidth, uint aHeight, Array aImage)
        {
            //var img = aImage.Cast<byte>().ToArray();
            Task.Run(() => //<-- 2016.02.20.
            {
                var img = (byte[])aImage;
                int x = 0;
                /*if (aWidth > 600)
                    Console.WriteLine("Updating screen..."); //2016.02.15.*/
                for (int j = (int)aY; j < aHeight && j < ScreenHeight; j++)
                {
                    for (int i = (int)aX; i < aWidth && i < ScreenWidth; i++)
                    {
                        if (x + 4 > aImage.Length)
                            return;
                        //Screen[i][j] = Color.FromArgb(img[x + 2], img[x + 1], img[x]);
                        //Screen[i][j][0] = img[x + 2];
                        //Screen[i][j][1] = img[x + 1];
                        //Screen[i][j][2] = img[x];
                        Screen[640 * j + i] = Color.FromArgb(img[x + 2], img[x + 1], img[x]).ToArgb();
                        x += 4;
                    }
                    x += (int)aX * 4;
                    int add = ((int)(aX + aWidth) - ScreenWidth);
                    if (add > 0)
                        x += add * 4;
                }
                /*if (aWidth > 600)
                    Console.WriteLine("Updated screen."); //2016.02.15.*/
            });
        }

        public void NotifyChange(uint aScreenId, uint aXOrigin, uint aYOrigin, uint aWidth, uint aHeight)
        {
        }

        public void Notify3DEvent(uint aType, Array aData)
        {
        }

        static T CreateJaggedArray<T>(params int[] lengths)
        {
            return (T)InitializeJaggedArray(typeof(T).GetElementType(), 0, lengths);
        }

        static object InitializeJaggedArray(Type type, int index, int[] lengths)
        {
            Array array = Array.CreateInstance(type, lengths[index]);
            Type elementType = type.GetElementType();

            if (elementType != null)
            {
                for (int i = 0; i < lengths[index]; i++)
                {
                    array.SetValue(
                        InitializeJaggedArray(elementType, index + 1, lengths), i);
                }
                //Console.WriteLine("Screen array sizes: " + array.Length + " " + ((Array)array.GetValue(0)).Length);
            }

            return array;
        }
    }
}
