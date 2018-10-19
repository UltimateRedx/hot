package com.hotelpal.service.service.runonce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class RunOnceTasks {
	private static final Logger logger = LoggerFactory.getLogger(RunOnceTasks.class);
	public static void run() {
		new RunOnceTasks().taskFonts();
	}
	private void taskFonts() {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		logger.info("===============================Here are all available fonts in the system.==================");
		for (String name : e.getAvailableFontFamilyNames()) {
			logger.info(name);
		}
		logger.info("============================================================================================");
	}
}
