package com.bdx.bwallet.tools;

import org.apache.commons.cli.CommandLine;

public interface Tool {
	static final int VENDOR_ID = 0x534c;
	static final int PRODUCT_ID = 0x01;
	void run(CommandLine cl);
}
