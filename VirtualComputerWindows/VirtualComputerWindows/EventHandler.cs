using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VirtualComputerWindows
{
	public class EventHandler
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

		public void handleEvent(long iEvent)
		{
			if (!enabled)
				return;
			handler.handleEvent(iEvent);
		}

		public void disable()
		{
			enabled = false;
		}
	}
}
