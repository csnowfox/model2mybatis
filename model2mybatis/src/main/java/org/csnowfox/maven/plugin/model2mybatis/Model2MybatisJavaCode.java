package org.csnowfox.maven.plugin.model2mybatis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csnowfox.maven.plugin.model2mybatis.entity.Ddl;
import org.csnowfox.maven.plugin.model2mybatis.entity.Table;
import org.csnowfox.maven.plugin.model2mybatis.parser.DDLParser;
import org.csnowfox.maven.plugin.model2mybatis.parser.PdmParser;
import org.csnowfox.maven.plugin.model2mybatis.parser.TableParser;
import org.csnowfox.maven.plugin.model2mybatis.utils.FileUtils;
import org.csnowfox.maven.plugin.model2mybatis.utils.MavenLogger;

/**
 * 代码生成服务类
 */
public class Model2MybatisJavaCode {

	/**
	 * 生成代码入口（多表生成）
	 * @param pathdao
	 * @param pathsql
	 * @param namesql
	 * @param projectname
	 * @param pathpack
	 * @param pathpdm
	 * @param tables
	 * @param interfaceName
	 */
	public static void createFiles(String pathdao,
								   String pathsql, String namesql,
								   String projectname, String pathpack, String pathpdm, String tables, String interfaceName) {

		MavenLogger.info("--- Start generating mybatis files ---");
		List<HashMap<String, String>> lis = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> mp1 = new HashMap<String, String>();

		mp1.put("PROJECTNAME", projectname);
		mp1.put("PACKPATH", pathpack);
		mp1.put("SAVEPATH", pathdao);
		mp1.put("modelPath", pathpdm);
		mp1.put("TABLES", tables);
		mp1.put("ISCREATE_PERSISTENCE", "YES");
		lis.add(mp1);

		for (HashMap<String, String> mp : lis) {
			String packPath = (String) mp.get("PACKPATH");
			String savePath = (String) mp.get("SAVEPATH");
			String modelPath = (String) mp.get("modelPath");
			String tablename = (String) mp.get("TABLES");
			String[] tablenames = tablename == null ? null : tablename.split(";");
			String sIsPersistence = (String) mp.get("ISCREATE_PERSISTENCE");
			String projName = (String) mp.get("PROJECTNAME");

			try {
				createFile(packPath, savePath, pathsql, namesql, modelPath, sIsPersistence,
                        projName, tablenames, interfaceName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 单表生成
	 * @param packPath
	 * @param savePath
	 * @param pathsql
	 * @param namesql
	 * @param modelPath
	 * @param sIsPersistence
	 * @param projName
	 * @param tablenames
	 * @param interfaceName
	 */
	public static void createFile(String packPath, String savePath, 
			String pathsql, String namesql,
			String modelPath, String sIsPersistence,
			String projName, String[] tablenames, String interfaceName) throws IOException {

		boolean isCreateSql = false;
		boolean isPersistence = !"NO".equalsIgnoreCase(sIsPersistence);

		if (projName == null) {
			projName = "csnowfox";
		}
		if ((packPath == null) || (packPath.isEmpty())) {
			System.err.println("包路径packpath：不能为空");
			return;
		}
		if ((savePath == null) || (savePath.isEmpty())) {
			System.err.println("保存文件路径savepath：不能为空");
			return;
		}
		if ((modelPath == null) || (modelPath.isEmpty())) {
			System.err.println("指定PDM文件modelPath：不能为空");
			return;
		}
		
		if ((pathsql != null) && (pathsql.length() > 0)) {
			isCreateSql = true;
		}

		TableParser parser = null;
		if (modelPath.substring(modelPath.lastIndexOf(".")).toUpperCase().equals(".PDM")) {
			parser = new PdmParser();
		}
		if (modelPath.substring(modelPath.lastIndexOf(".")).toUpperCase().equals(".SQL")) {
			parser = new DDLParser();
		}

		// 从定义文件解析出table信息
		List<Table> tabs = parser.getTables(modelPath, tablenames);

		// 生成java类
		for (int i = 0; i < tabs.size(); i++) {
			Table tab = tabs.get(i);
			try {
				tab.setProjectName(projName);
				tab.writeout(tab, packPath, savePath, tab.getUser().getCode(), interfaceName);
			} catch (Exception e) {
				MavenLogger.info("=====write error[tablename:"	+ tab.getTableName() + ",packPath:" + packPath + ",xmlpath:" + savePath + "]======");
				e.printStackTrace();
			}
		}

		// 判断是否需要生成ddl文件
		if (isCreateSql) {
			List<String> lismsg = Ddl.generateSQL(tabs);

			try {
				FileUtils.writeFile(lismsg, pathsql, namesql);
			} catch (Exception e) {
				MavenLogger.info("=====write sql error ======");
				e.printStackTrace();
			}
		}
	}
	

}
