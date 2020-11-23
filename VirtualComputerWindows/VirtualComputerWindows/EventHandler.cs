using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using VirtualBox;

namespace VirtualComputerWindows
{
	public class EventHandler : IEventListener
	{
		private readonly IEventHandler handler;
		private bool enabled = true;

		/**
		 * New MSCOM event handler.
		 *
		 * @param handler The handle method that handles what needs to be handled
		 */
		public EventHandler(IEventHandler handler)
		{
			this.handler = handler;
		}

		public void HandleEvent(IEvent aEvent)
		{
			if (!enabled)
				return;
			handler.handleEvent((long)Marshal.GetIDispatchForObject(aEvent));
		}

		public void disable()
		{
			enabled = false;
		}
    }
}
