using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VirtualComputerWindows
{
    public interface IMCFrameBuffer
    {
        void notifyUpdate(long x, long y, long width, long height);

        void notifyUpdateImage(long x, long y, long width, long height, byte[] image);

        void notifyChange(long screenId, long xOrigin, long yOrigin, long width, long height);
    }
}
