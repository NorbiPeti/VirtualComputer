using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using VirtualBox;

namespace VirtualComputerWindows
{
    public class WinFrameBuffer : IFramebuffer
    {
        private IMCFrameBuffer framebuffer;
        public WinFrameBuffer(IMCFrameBuffer framebuffer)
        {
            this.framebuffer = framebuffer;
        }

        public void NotifyUpdate(uint aX, uint aY, uint aWidth, uint aHeight)
        {
            framebuffer.notifyUpdate(aX, aY, aWidth, aHeight);
        }

        public void NotifyUpdateImage(uint aX, uint aY, uint aWidth, uint aHeight, Array aImage)
        {
            framebuffer.notifyUpdateImage(aX, aY, aWidth, aHeight, (byte[])aImage);
        }

        public void NotifyChange(uint aScreenId, uint aXOrigin, uint aYOrigin, uint aWidth, uint aHeight)
        {
            framebuffer.notifyChange(aScreenId, aXOrigin, aYOrigin, aWidth, aHeight);
        }

        public int VideoModeSupported(uint aWidth, uint aHeight, uint aBpp)
        {
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
            Console.WriteLine("VHWA command! " + aCommand);
        }

        public void Notify3DEvent(uint aType, Array aData)
        {
            Console.WriteLine("3D event! " + aType);
        }

        public uint Width => 640;

        public uint Height => 480;

        public uint BitsPerPixel => 32;

        public uint BytesPerLine => 640;

        public BitmapFormat PixelFormat => BitmapFormat.BitmapFormat_BGRA;

        public uint HeightReduction => 0;

        public IFramebufferOverlay Overlay => null;

        public long WinId => 0;

        public Array Capabilities => new[] { FramebufferCapabilities.FramebufferCapabilities_RenderCursor, FramebufferCapabilities.FramebufferCapabilities_UpdateImage };
    }
}
