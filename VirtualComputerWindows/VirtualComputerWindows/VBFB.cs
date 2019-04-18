using System;
using VirtualBox;

namespace VirtualComputerWindows
{
    internal class VBFB : IFramebuffer
    {
        private IDisplay display;

        public VBFB(IDisplay display)
        {
            this.display = display;
        }

        public void NotifyUpdate(uint aX, uint aY, uint aWidth, uint aHeight)
        {
            Console.WriteLine("Update: " + aX + " " + aY + " " + aWidth + " " + aHeight);
        }

        public void NotifyUpdateImage(uint aX, uint aY, uint aWidth, uint aHeight, Array aImage)
        {
            Console.WriteLine("UpdateImage: " + aX + " " + aY + " " + aWidth + " " + aHeight);
        }

        public void NotifyChange(uint aScreenId, uint aXOrigin, uint aYOrigin, uint aWidth, uint aHeight)
        {
            Console.WriteLine("Change: " + aXOrigin + " " + aYOrigin + " " + aWidth + " " + aHeight);
            display.QuerySourceBitmap(0, out var isd);
            var addr = new IntPtr();
            isd.QueryBitmapInfo(addr, out var w, out var h, out var bpp, out var bpl, out var bf);
            Console.WriteLine("Bitmap info: " + addr + " " + w + " " + h + " " + bpp + " " + bpl + " " + bf);
        }

        public int VideoModeSupported(uint aWidth, uint aHeight, uint aBpp)
        {
            return 1;
        }

        public uint GetVisibleRegion(ref byte aRectangles, uint aCount)
        {
            return aCount;
        }

        public void SetVisibleRegion(ref byte aRectangles, uint aCount)
        {
        }

        public void ProcessVHWACommand(ref byte aCommand, int aEnmCmd, int aFromGuest)
        {
        }

        public void Notify3DEvent(uint aType, Array aData)
        {
        }

        public uint Width => 640;

        public uint Height => 480;

        public uint BitsPerPixel => 32;

        public uint BytesPerLine => 640 * 4;

        public BitmapFormat PixelFormat => BitmapFormat.BitmapFormat_BGRA;

        public uint HeightReduction => 0;

        public IFramebufferOverlay Overlay => null;

        public long WinId => 0;

        //public Array Capabilities => new[] { FramebufferCapabilities.FramebufferCapabilities_UpdateImage };
        public Array Capabilities => new FramebufferCapabilities[] { };
    }
}