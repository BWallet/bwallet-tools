package com.bdx.bwallet.tools;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.hid4java.HidDevice;

public class ResetDeviceTool extends AbstractTool {

	public ResetDeviceTool() {
		super();
	}
	
	@Override
	public void run(CommandLine cl) {
		while (true) {
			List<HidDevice> devices = this.openAttachedDevices();
			for (HidDevice device : devices) {
				
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
