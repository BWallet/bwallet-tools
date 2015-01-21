package com.bdx.bwallet.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Main {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		Options opt = new Options();
		opt.addOption(OptionBuilder.withLongOpt("cmd").withDescription("The cmd that want to run, e.g. update-firmware, save-cpu-sn.").withValueSeparator('=').hasArg().create());
		opt.addOption(OptionBuilder.withLongOpt("firmware").withDescription("The path of firmware use to update device.").withValueSeparator('=').hasArg().create());
		opt.addOption(OptionBuilder.withLongOpt("output").withDescription("The path of output file use to save serial number of cpu.").withValueSeparator('=').hasArg().create());
		opt.addOption("f", "force",  false, "Force to overwrite output file.");
		opt.addOption("h", "help",  false, "Print help for the command.");
		
		String formatstr = "java -jar bwallet-tools.jar --cmd=<update-firmware|save-cpu-sn> [--firmware=<file>] [--output=<file>] [-f/--force] [-h/--help]";
		HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new PosixParser();
        CommandLine cl = null;
        try {
            cl = parser.parse( opt, args );
        } catch (ParseException e) {
            formatter.printHelp( formatstr, opt );
        }
        // -h --help
        if (cl.hasOption("h")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(formatstr, "", opt, "");
            return;
        }
        if (cl.hasOption("cmd")) {
            String cmd = cl.getOptionValue("cmd");
            if ("update-firmware".equals(cmd)) {
            	WriteFirmwareTool tool = new WriteFirmwareTool();
            	tool.run(cl);
            } else {
            	System.out.println("Unknown cmd.");
            }
        }
	}

}
