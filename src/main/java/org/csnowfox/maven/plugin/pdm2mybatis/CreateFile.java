package org.csnowfox.maven.plugin.pdm2mybatis;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CreateFile {

	public static void writeFile(List<String> lismsg, String path,
			String filename) throws IOException {
		writeFile(lismsg, path, filename, "utf-8");
	}

	public static void writeFile(List<String> lismsg, String path,
			String filename, String filecharcode) throws IOException {
		if ((lismsg == null) || (lismsg.size() <= 0)) {
			return;
		}
		LogUtils.logger.info("===文件[" + filename + "," + path + "] ====");
		createPath(path);
		String name = path + filename;
		File f = new File(name);
		if (f.exists()) {
			f.delete();
		}
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(
				name, true), filecharcode);

		for (String line : lismsg) {
			out.write(line);
			out.flush();
		}

		closeStream(null, out);
	}

	public static void createFile(String path, String fileName, byte[] b) {
		File fPath = new File(path);
		if (!fPath.exists()) {
			fPath.mkdirs();
		}

		String fullName = path + fileName;
		System.out.println("准备生成=" + fullName);
		File file = new File(fullName);
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					file));
			out.write(b);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("写入文件异常，找不到指定文件：" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("写入文件IO异常：" + e.getMessage());
		}
	}

	public static void createPath(String path) {
		File fPath = new File(path);
		if (!fPath.exists()) {
			fPath.mkdirs();
		}
	}

	public static void createNewFile(String modelFile, String savePath,
			String fileName, String[][] replaceStr) throws IOException {
		if (new File(modelFile).exists()) {
			createPath(savePath);
			String name = savePath + fileName;
			System.out.println("save jsp=" + name);
			BufferedReader in = new BufferedReader(new FileReader(modelFile));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					name, true));
			String line = null;
			while ((line = in.readLine()) != null) {
				line = checkLine(line, replaceStr);
				out.write(line.getBytes());
				out.flush();
			}

			closeStream(in, out);
		} else {
			System.err.println("没有找到路径:" + modelFile + "下的model文件");
		}
	}

	public static String checkLine(String line, String[][] replaceStr) {
		String temp = line;
		try {
			temp = new String(temp.getBytes("GBK"), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			temp = line;
			System.err.println("line 字符集转换（GBK-utf8）异常：" + e.getMessage());
		}

		if (replaceStr != null) {
			for (int i = 0; i < replaceStr.length; i++) {
				temp = temp.replace(replaceStr[i][0], replaceStr[i][1]);
			}
		}
		temp = temp + System.getProperty("line.separator");

		return temp;
	}

	public static void closeStream(BufferedReader in, DataOutputStream out) {
		if (in != null) {
			try {
				in.close();
				in = null;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("BufferedReader 关闭异常:" + e.getMessage());
			}
		}
		if (out != null) {
			try {
				out.close();
				out = null;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("DataOutputStream 关闭异常:" + e.getMessage());
			}
		}
	}

	public static void closeStream(BufferedReader in, OutputStreamWriter out) {
		if (in != null) {
			try {
				in.close();
				in = null;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("BufferedReader 关闭异常:" + e.getMessage());
			}
		}
		if (out != null) {
			try {
				out.close();
				out = null;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("DataOutputStream 关闭异常:" + e.getMessage());
			}
		}
	}

	public static String toFirstLowerCase(String strSource) {
		char[] ch = strSource.toCharArray();
		if ((ch[0] >= 'A') && (ch[0] < 'Z')) {
			ch[0] = ((char) (ch[0] + ' '));
		}
		return new String(ch);
	}

	public static String toFirsttoUpperCase(String strSource) {
		char[] ch = strSource.toCharArray();
		if ((ch[0] >= 'a') && (ch[0] < 'z')) {
			ch[0] = ((char) (ch[0] - ' '));
		}
		return new String(ch);
	}
}