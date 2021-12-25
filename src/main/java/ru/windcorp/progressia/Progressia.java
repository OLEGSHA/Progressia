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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;

import ru.windcorp.progressia.common.util.crash.CrashReports;

/**
 * A class providing access to build metadata.
 */
public class Progressia {

	private static final String NAME = "Progressia";
	private static String version;
	private static String gitCommit;
	private static String gitBranch;
	private static String buildId;

	static {
		try {
			Manifest manifest = findManifest();

			if (manifest == null) {
				setDevelopmentMetadata();
				LogManager.getLogger().info(
					"Manifest with Specification-Title not found. "
						+ "Either you are in a development environment or something has gone horribly wrong with classloaders."
				);
			} else {
				fillMetadata(manifest);
			}
		} catch (Throwable t) {
			CrashReports.crash(t, "Something went wrong while loading metadata");
		}
	}

	private static Manifest findManifest() {
		try {
			Enumeration<URL> resources = Progressia.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			Collection<IOException> exceptions = new ArrayList<>();

			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();

				try {

					Manifest manifest = new Manifest(url.openStream());
					Attributes mainAttributes = manifest.getMainAttributes();
					if (NAME.equals(mainAttributes.getValue("Specification-Title"))) {
						return manifest;
					}

				} catch (IOException e) {
					exceptions.add(e);
				}
			}

			if (exceptions.isEmpty()) {
				return null;
			}

			IOException scapegoat = null;
			for (IOException e : exceptions) {
				if (scapegoat == null) {
					scapegoat = e;
				} else {
					scapegoat.addSuppressed(e);
				}
			}

			throw CrashReports.report(scapegoat, "Could not read manifest");
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not read manifest");
		}
	}

	private static void setDevelopmentMetadata() {
		version = "dev";
		gitCommit = "-";
		gitBranch = "-";
		buildId = "-";
	}

	private static void fillMetadata(Manifest manifest) {
		version = getAttributeOrCrash(manifest, "Implementation-Version");
		gitCommit = getAttributeOrCrash(manifest, "Implementation-Version-Git-Commit");
		gitBranch = getAttributeOrCrash(manifest, "Implementation-Version-Git-Branch");
		buildId = getAttributeOrCrash(manifest, "Implementation-Version-BuildId");
	}

	private static String getAttributeOrCrash(Manifest manifest, String key) {
		String result = manifest.getMainAttributes().getValue(key);
		if (result == null) {
			throw CrashReports.report(null, "Manifest exists but attribute " + key + " not found");
		}
		return result;
	}

	public static String getName() {
		return NAME;
	}

	/**
	 * Returns the version of the game as a String. Version data is retrieved
	 * from a {@code META-INF/MANIFEST.MF} file located in the main JAR. Version
	 * format depends on way the game was built:
	 * <ul>
	 * <li><code>dev</code> if no matching manifest was found, e.g. when launching from an IDE</li>
	 * <li>The value of <code>Implementation-Version</code> specified in the manifest:
	 * <ul>
	 * <li>[Stage-]Major.Minor.Patch, e.g. <code>alpha-0.3.2</code> or <code>1.4.2</code>, for released versions</li>
	 * <li>BuildId, e.g. <code>WJ7</code>, for snapshots built by automation systems</li>
	 * <li>YYYY-MM-DD, e.g. <code>2021-12-32</code>, for snapshots built manually</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @return the version
	 */
	public static String getVersion() {
		return version;
	}
	
	public static String getFullerVersion() {
		if (isDefaultGitBranch() || "-".equals(gitBranch)) {
			return version;
		} else {
			return String.format("%s/%s", version, gitBranch);
		}
	}
	
	/**
	 * @return the buildId or <code>"-"</code>
	 */
	public static String getBuildId() {
		return buildId;
	}
	
	/**
	 * @return the Git commit or <code>"-"</code>
	 */
	public static String getGitCommit() {
		return gitCommit;
	}
	
	public static String getGitCommitShort() {
		if (gitCommit == null || "-".equals(gitCommit)) {
			return gitCommit;
		}
		
		return gitCommit.substring(0, Math.min(7, gitCommit.length()));
	}
	
	/**
	 * @return the Git branch or <code>"-"</code>
	 */
	public static String getGitBranch() {
		return gitBranch;
	}
	
	public static boolean isDefaultGitBranch() {
		return "master".equals(gitBranch) || "main".equals(gitBranch);
	}
	
	public static String getFullVersion() {
		return String.format("%s/%s/%s/%s", version, gitBranch, getGitCommitShort(), buildId);
	}

}
