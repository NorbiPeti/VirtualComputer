package sznp.virtualcomputer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {

	public static boolean extractFromJar(final String fileName,
										 final String dest) throws IOException {
		if (getRunningJar() == null) {
			return false;
		}
		final File file = new File(dest);
		if (file.isDirectory()) {
			file.mkdir();
			return false;
		}
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		final JarFile jar = getRunningJar();
		final Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			final JarEntry je = e.nextElement();
			if (!je.getName().contains(fileName)) {
				continue;
			}
			final InputStream in = new BufferedInputStream(
					jar.getInputStream(je));
			final OutputStream out = new BufferedOutputStream(
					new FileOutputStream(file));
			copyInputStream(in, out);
			jar.close();
			return true;
		}
		jar.close();
		return false;
	}

	private static void copyInputStream(final InputStream in,
										final OutputStream out) throws IOException {
		try {
			final byte[] buff = new byte[4096];
			int n;
			while ((n = in.read(buff)) > 0) {
				out.write(buff, 0, n);
			}
		} finally {
			out.flush();
			out.close();
			in.close();
		}
	}

	public static URL getJarUrl(final File file) throws IOException {
		return new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
	}

	public static JarFile getRunningJar() throws IOException {
		if (!RUNNING_FROM_JAR) {
			return null; // null if not running from jar
		}
		String path = new File(JarUtils.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath()).getAbsolutePath();
		path = URLDecoder.decode(path, "UTF-8");
		return new JarFile(path);
	}

	private static boolean RUNNING_FROM_JAR = false;

	static {
		final URL resource = JarUtils.class.getClassLoader().getResource(
				"plugin.yml");
		if (resource != null) {
			RUNNING_FROM_JAR = true;
		}
	}

}