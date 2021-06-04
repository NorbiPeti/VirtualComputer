using System;
using VirtualBox;

namespace VirtualComputerWindows
{
    public class MCFramebuffer : IFramebuffer
    {
        public void NotifyUpdate(uint aX, uint aY, uint aWidth, uint aHeight)
        {
            Console.Error.WriteLine($"Unexpected NotifyUpdate: {aX} {aY} {aWidth} {aHeight}");
        }

        public void NotifyUpdateImage(uint aX, uint aY, uint aWidth, uint aHeight, Array aImage)
        {
            Console.WriteLine("NotifyUpdateImage");
        }

        public void NotifyChange(uint aScreenId, uint aXOrigin, uint aYOrigin, uint aWidth, uint aHeight)
        {
            Console.WriteLine("NotifyChange");
        }

        public int VideoModeSupported(uint aWidth, uint aHeight, uint aBpp)
        {
            Console.WriteLine("VideoModeSupported");
            return 1;
        }

        public uint GetVisibleRegion(ref byte aRectangles, uint aCount)
        {
            return 0;
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

        public uint Width { get; } = 640;
        public uint Height { get; } = 480;
        public uint BitsPerPixel { get; } = 32;
        public uint BytesPerLine { get; } = 640 * 4;
        public BitmapFormat PixelFormat { get; } = BitmapFormat.BitmapFormat_BGRA;
        public uint HeightReduction { get; } = 0;
        public IFramebufferOverlay Overlay { get; }
        public long WinId { get; } = 0;
        public Array Capabilities { get; } = new[] {FramebufferCapabilities.FramebufferCapabilities_UpdateImage};
    }
}