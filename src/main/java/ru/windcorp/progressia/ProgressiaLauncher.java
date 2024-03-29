/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia;

import org.apache.logging.log4j.LogManager;

import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.crash.analyzers.OutOfMemoryAnalyzer;
import ru.windcorp.progressia.common.util.crash.providers.*;
import ru.windcorp.progressia.test.LayerTitle;

public class ProgressiaLauncher {

	public static String[] arguments;
	private static Proxy proxy;

	public static void launch(String[] args, Proxy proxy) {
		arguments = args.clone();
		setupCrashReports();
		
		LogManager.getRootLogger().info("Launching " + Progressia.getName() + " version " + Progressia.getFullVersion());

		proxy.initialize();
		ProgressiaLauncher.proxy = proxy;
		GUI.addTopLayer(new LayerTitle("Title"));
	}
	
	public static Proxy getProxy() {
		return proxy;
	}

	private static void setupCrashReports() {
		// Context providers
		CrashReports.registerProvider(new VersionProvider());
		CrashReports.registerProvider(new OSContextProvider());
		CrashReports.registerProvider(new RAMContextProvider());
		CrashReports.registerProvider(new JavaVersionContextProvider());
		CrashReports.registerProvider(new OpenALContextProvider());
		CrashReports.registerProvider(new ArgsContextProvider());
		CrashReports.registerProvider(new LanguageContextProvider());
		CrashReports.registerProvider(new ScreenContextProvider());
		// Analyzers
		CrashReports.registerAnalyzer(new OutOfMemoryAnalyzer());

		Thread.setDefaultUncaughtExceptionHandler((Thread thread, Throwable t) -> {
			CrashReports.crash(t, "Uncaught exception in thread %s", thread.getName());
		});
	}

}
