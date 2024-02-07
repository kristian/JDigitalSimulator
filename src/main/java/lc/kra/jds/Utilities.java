/*
 * JDigitalSimulator
 * Copyright (C) 2017 Kristian Kraljic
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lc.kra.jds;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.JFileChooser;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

public final class Utilities {
	public static enum TranslationType { TEXT, ALTERNATIVE, TITLE, EXTERNAL, TOOLTIP, DESCRIPTION; }

	public static final String
		CONFIGURATION_LOCALIZATION_LANGUAGE = "localization.language",
		CONFIGURATION_LOOK_AND_FEEL_CLASS = "lookandfeel.class",
		CONFIGURATION_LOOK_AND_FEEL_NAME = "lookandfeel.name",
		CONFIGURATION_ANSI_SYMBOLS = "symbols.ansi",
		CONFIGURATION_WINDOW_SIZE = "window.size",
		CONFIGURATION_WINDOW_LOCATION = "window.location",
		CONFIGURATION_WINDOW_MAXIMIZED = "window.maximized";
	public static final Locale[] SUPPORTED_LOCALES = {Locale.ENGLISH, Locale.GERMAN};

	private static Locale currentLocale = Locale.getDefault();
	private static ResourceBundle translationBundle;
	private static SimpleClassLoader simpleClassLoader, legacyClassLoader;

	private static Properties configuration = new Properties();

	private static boolean useAnsiSymbols = false;

	public static Object copy(Object object) throws CloneNotSupportedException { //deep clone using serilization
		Object copy = null;
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			try { objectOutput.writeObject(object); }
			finally { objectOutput.close(); }
			ObjectInputStream objectInput = new AlternateClassLoaderObjectInputStream(new ByteArrayInputStream(byteOutput.toByteArray()), Utilities.getSimpleClassLoader());
			try { copy = objectInput.readObject(); }
			finally { objectInput.close(); }
		} catch(Exception e) { e.printStackTrace(); throw new CloneNotSupportedException(); }
		return copy;
	}
	public static boolean isCopying() {
		for(StackTraceElement element:Thread.currentThread().getStackTrace())
			if(element.getClassName().equals(Utilities.class.getName())&&element.getMethodName().equals("copy"))
				return true;
		return false;
	}

	public static boolean isCloneable(Object object) { return isCloneable(object, false); }
	public static boolean isCloneable(Object object, boolean deep) {
		if(object==null) return false;
		return isCloneable(object.getClass(), deep);
	}
	private static boolean isCloneable(Class<?> cls, boolean deep) {
		if(cls==null) return false;
		for(Class<?> interfce:cls.getInterfaces())
			if(interfce.equals(Cloneable.class))
				if(deep) {
					Class<?> supercls = cls.getSuperclass();
					if(supercls!=null&&!supercls.equals(Object.class))
						return isCloneable(supercls, deep);
					else return true;
				} else return true;
		return false;
	}

	public static Locale getCurrentLocale() { return Utilities.currentLocale; }
	public static void setCurrentLocale(Locale locale) {
		currentLocale = locale;
		translationBundle = null;
		Locale.setDefault(locale);
	}
	private static ResourceBundle getTranslationBundle() {
		return translationBundle!=null?translationBundle:(translationBundle=
				ResourceBundle.getBundle("lc/kra/jds/TranslationBundle", currentLocale));
	}
	public static String getTranslation(String key) { return getTranslation(key, TranslationType.TEXT); }
	public static String getTranslation(String key, Object... variables) { return getTranslation(key, TranslationType.TEXT, variables); }
	public static String getTranslation(String key, TranslationType type) { return getTranslation(key, type, (Object[])null); }
	public static String getTranslation(String key, TranslationType type, Object... variables) {
		ResourceBundle bundle = getTranslationBundle();
		if(type!=TranslationType.TEXT&&type!=TranslationType.EXTERNAL)
			key = new StringBuilder(key).append('.').append(type.toString().toLowerCase()).toString();
		if(!bundle.containsKey(key))
			bundle = ResourceBundle.getBundle("lc/kra/jds/TranslationBundle");
		if(!bundle.containsKey(key))
			switch(type) {
			case TEXT: case TITLE: return new StringBuilder("text missing ("+key+")").toString();
			case ALTERNATIVE: return getTranslation(key);
			case EXTERNAL: return key;
			default: return null; }
		else return MessageFormat.format(bundle.getString(key), variables);
	}

	public static boolean useAnsiSymbols(){
		return useAnsiSymbols;
	}
	public static void setUseAnsiSymbols(boolean value){
		useAnsiSymbols = value;
	}

	public static URL getResource(String name) {
		if(!name.startsWith("/"))
			name = "/lc/kra/jds/"+name;
		return Utilities.class.getResource(name);
	}

	public static <Type> Type getField(Class<?> cls, String fieldName) { return getField(cls, fieldName, null); }
	@SuppressWarnings("unchecked") public static <Type> Type getField(Class<?> cls, String fieldName, Type dfault) {
		try {
			Field field = cls.getField(fieldName);
			Object value = field.get(null);
			if(value==null||(value instanceof String&&value.toString().isEmpty()))
				return dfault;
			return (Type) value;
		} catch(Exception e) { return dfault; }
	}

	protected static class DebuggingObjectOutputStream extends ObjectOutputStream {
		private static final Field DEPTH_FIELD;
		static {
			try {
				DEPTH_FIELD = ObjectOutputStream.class.getDeclaredField("depth");
				DEPTH_FIELD.setAccessible(true);
			} catch(NoSuchFieldException e) { throw new AssertionError(e); }
		}

		public DebuggingObjectOutputStream(OutputStream out) throws IOException {
			super(out);
			enableReplaceObject(true);
		}

		@Override
		protected Object replaceObject(Object object) throws IOException {
			int currentDepth = currentDepth();
			for(int ident=0;ident<currentDepth;ident++)
				System.out.print("-");
			System.out.println(object);
			return super.replaceObject(object);
		}

		private int currentDepth() {
			try { return ((Integer)DEPTH_FIELD.get(this))-1; }
			catch(IllegalAccessException e) {
				throw new AssertionError(e);
			}
		}
	}

	public static Cipher createCipher(String hash, int mode) throws GeneralSecurityException {
		DESKeySpec keySpecification = new DESKeySpec(hash!=null?hash.getBytes():new byte[0]);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(keySpecification);
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(mode, key);
		return cipher;
	}

	public static String computeHash(String text) {
		if(text==null||text.isEmpty())
			return null;
		try {
			byte[] digits = MessageDigest.getInstance("SHA").digest(text.getBytes());
			StringBuilder hash = new StringBuilder();
			for(byte digit:digits)
				hash.append(Integer.toHexString(digit&0xff));
			return hash.toString();
		} catch (NoSuchAlgorithmException e) { return Integer.toString(text.hashCode()); }
	}

	public static boolean isNumeric(String number) { try { Integer.parseInt(number); return true; } catch(NumberFormatException e) { return false; }}
	public static boolean isOdd(int number) { return (number&1)==1; } //last bit is 1
	public static boolean isEven(int number) { return (number&1)==0; } //last bit is 0

	private static File getConfigurationFile() {
		try {
			File file = new File(getLocalPath().toURI()).getParentFile().listFiles(new FilenameFilter() {
				@Override public boolean accept(File dir, String name) {
					return name.equals("JDigitalSimulator.properties");
				}
			})[0];
			if(!file.exists()||file.isDirectory()||!file.canRead())
				throw new FileNotFoundException();
			return file;
		}	catch(Exception e) { //URISyntaxException, NullPointerException, ArrayIndexOutOfBoundsException, FileNotFoundException
			return new File(System.getProperty("java.io.tmpdir")+"JDigitalSimulator.properties");
		}
	}
	private static boolean loadConfiguration() {
		if(!configuration.isEmpty()) return true;
		File file = getConfigurationFile();
		if(!file.exists()) return false;
		try {
			configuration.load(new FileInputStream(file));
		}	catch (Exception e) {	return false;	}
		return true;
	}
	private static boolean writeConfiguration() {
		if(configuration.isEmpty()) return true;
		File file = getConfigurationFile();
		try {
			if(! file.exists()) file.createNewFile();
			configuration.store(new BufferedOutputStream(new FileOutputStream(file)), new String());
		}	catch (IOException e) { return false;	}
		return true;
	}
	public static boolean hasConfiguration(String key) {
		return (loadConfiguration()&&configuration.containsKey(key));
	}
	public static String getConfiguration(String key) {
		if(!loadConfiguration()||!configuration.containsKey(key)) return null;
		return configuration.getProperty(key);
	}
	public static boolean setConfiguration(String key, String value) {
		configuration.setProperty(key, value);
		return writeConfiguration();
	}

	public static SimpleClassLoader getSimpleClassLoader() {
		if(simpleClassLoader==null)
			simpleClassLoader = new SimpleClassLoader();
		return simpleClassLoader;
	}
	public static class SimpleClassLoader extends ClassLoader {
		public Class<?> loadClass(File file) throws FileNotFoundException, IOException {
			byte[] bytes = new byte[(int)file.length()]; BufferedInputStream input = null;
			try { (input=new BufferedInputStream(new FileInputStream(file))).read(bytes); }
			finally { input.close(); }
			return loadClass(bytes);
		}
		public Class<?> loadClass(ZipFile file, ZipEntry entry) throws IOException {
			byte[] bytes = new byte[(int)entry.getSize()];
			file.getInputStream(entry).read(bytes);
			return loadClass(bytes);
		}
		protected Class<?> loadClass(byte[] bytes) {
			Class<?> cls = null;
			cls = defineClass(null, bytes, 0, bytes.length);
			resolveClass(cls);
			return cls;
		}
	}
	public static void initLegacyClassLoader(File dir) {
		if (!dir.isDirectory()) return;
		List<File> asmFiles = new ArrayList<>();
		for(File file:dir.listFiles())
			if(file.getName().matches("asm.*\\.jar"))
				asmFiles.add(file);
		if (!asmFiles.isEmpty()) {
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			for (File asmFile : asmFiles) {
				try {
					try {
						Method method = classLoader.getClass().getDeclaredMethod("addURL", URL.class);
						method.setAccessible(true);
						method.invoke(classLoader, asmFile.toURI().toURL());
					} catch (NoSuchMethodException e) {
						Method method = classLoader.getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
						method.setAccessible(true);
						method.invoke(classLoader, asmFile.getAbsolutePath());
					}
				} catch (Exception e) { e.printStackTrace(); }
			}

			try { legacyClassLoader = new LegacyClassLoader(); }
			catch (Throwable t) { t.printStackTrace(); } // it is important to catch throwable here, in order to catch NoClassDefFound*Error* in case an ASM class is missing
		}
	}
	public static boolean hasLegacyClassLoader() {
		return legacyClassLoader != null;
	}
	public static SimpleClassLoader getLegacyClassLoader() {
		return legacyClassLoader;
	}
	public static class LegacyClassLoader extends SimpleClassLoader {
		public static final String LEGACY_PACKAGE_PREFIX = "de/ksquared/", PACKAGE_PREFIX = "lc/kra/";
		@Override
		protected Class<?> loadClass(byte[] bytes) {
			return super.loadClass(modifyClass(bytes));
		}
		private byte[] modifyClass(byte[] bytes) {
			ClassReader reader = new ClassReader(bytes);
			ClassWriter writer = new ClassWriter(reader, 0);
			reader.accept(new ClassRemapper(writer, new Remapper() {
				@Override
				public String map(String typeName) {
					if (typeName.startsWith(LEGACY_PACKAGE_PREFIX)) {
						return replaceLegacyPackage(typeName);
					}
					return typeName;
				}
			}), ClassReader.EXPAND_FRAMES);
			return writer.toByteArray();
		}
		public static String replaceLegacyPackage(String name) {
			return name.replaceFirst(Pattern.quote(LEGACY_PACKAGE_PREFIX), Matcher.quoteReplacement(PACKAGE_PREFIX));
		}
	}

	public static class AlternateClassLoaderObjectInputStream extends ObjectInputStream {
		protected final ClassLoader alternateClassLoader;
		public AlternateClassLoaderObjectInputStream(InputStream in, ClassLoader alternateClassLoader) throws IOException {
			super(in); this.alternateClassLoader = alternateClassLoader; }
		@Override protected Class<?> resolveClass(ObjectStreamClass descriptor) throws IOException, ClassNotFoundException{
			try{ return Class.forName(descriptor.getName(), false, alternateClassLoader); }
			catch(ClassNotFoundException e){
				return super.resolveClass(descriptor);
			}
		}
	}
	public static class LegacyObjectInputStream extends AlternateClassLoaderObjectInputStream {
		public static final String LEGACY_PACKAGE_PREFIX = "de.ksquared.", PACKAGE_PREFIX = "lc.kra.";
		public LegacyObjectInputStream(InputStream in) throws IOException {
			super(in, getSimpleClassLoader());
		}
		@Override protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
			ObjectStreamClass descriptor = super.readClassDescriptor(); String name = descriptor.getName();
			if(name.startsWith(LEGACY_PACKAGE_PREFIX)) {
				String newName = replaceLegacyPackage(name);
				try {
					// do not return a new ObjectStreamClass looked up from new class definitions, but modify the descriptor read from the stream, otherwise some flags could be wrong, like the flag "hasWriteObjectData" resulting in an exception when trying to read the object
					Field nameField = ObjectStreamClass.class.getDeclaredField("name");
					nameField.setAccessible(true);
					nameField.set(descriptor, newName);
				} catch (IllegalAccessException | NoSuchFieldException e) {
					throw new RuntimeException(e);
				}
			}
			return descriptor;
		}
		public static String replaceLegacyPackage(String name) {
			return name.replaceFirst("^(\\[L)?"+Pattern.quote(LEGACY_PACKAGE_PREFIX), "$1"+Matcher.quoteReplacement(PACKAGE_PREFIX));
		}
	}

	public static URL getLocalPath() {
		URL path = Utilities.class.getProtectionDomain().getCodeSource().getLocation();
		try { return new URL(URLDecoder.decode(path.toString(), "UTF-8")); }
		catch(Exception e) { return path; }
	}

	public static File getFile(URL ressource) {
		StringBuilder file = new StringBuilder();
		String host = ressource.getHost(), path = ressource.getPath();
		if(host!=null&&!host.isEmpty())
			file.append(File.separatorChar).append(File.separatorChar).append(host);
		return new File(file.append(path.split("!", 2)[0]).toString());
	}

	public static boolean isWindows() { return System.getProperty("os.name").startsWith("Windows"); }

	public static String cropString(String text, int length) {
		if(text.length()<=length)
			return text;
		return text.substring(0, length)+"...";
	}

	public static class RememberFileChooser extends JFileChooser {
		private static final long serialVersionUID = 1l;
		public RememberFileChooser() { super(getLastDirectory()); }
		private static File getLastDirectory() {
			String directory = getConfiguration("directory");
			if(directory!=null) {
				File file = new File(directory);
				if(file.exists()&&file.isDirectory())
					return file;
				else return null;
			} else return null;
		}
		@Override public void approveSelection() {
			super.approveSelection();
			File selected = getSelectedFile();
			if(!selected.isDirectory())
				setConfiguration("directory", selected.getParentFile().toString());
		}
	}
}
