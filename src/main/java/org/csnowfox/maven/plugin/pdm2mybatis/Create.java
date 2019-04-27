package org.csnowfox.maven.plugin.pdm2mybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csnowfox.maven.plugin.pdm2mybatis.entity.Column;
import org.csnowfox.maven.plugin.pdm2mybatis.entity.Key;
import org.csnowfox.maven.plugin.pdm2mybatis.entity.Table;
import org.csnowfox.maven.plugin.pdm2mybatis.entity.Xml;
import org.csnowfox.maven.plugin.pdm2mybatis.pdm.PdmParser;

/**
 * 代码生成服务类
 */
public class Create {

	public static void createFile(String packPath, String savePath, 
			String pathsql, String namesql,
			String pdmPath, String xmlpath, String sIsPersistence,
			String projName, String[] tablenames, String interfaceName) {
		boolean isOk = true;
		boolean isCreateXml = false;
		boolean isCreateSql = false;
		boolean isPersistence = !"NO".equalsIgnoreCase(sIsPersistence);

		if (projName == null) {
			projName = "komm";
		}
		if ((packPath == null) || (packPath.isEmpty())) {
			isOk = false;
			System.err.println("包路径packpath：不能为空");
		}
		if ((savePath == null) || (savePath.isEmpty())) {
			isOk = false;
			System.err.println("保存文件路径savepath：不能为空");
		}
		if ((pdmPath == null) || (pdmPath.isEmpty())) {
			isOk = false;
			System.err.println("指定PDM文件pdmPath：不能为空");
		}

		Xml xml = null;
		if ((xmlpath != null) && (xmlpath.length() > 0)) {
			isCreateXml = true;
			xml = new Xml();
		}
		
		if ((pathsql != null) && (pathsql.length() > 0)) {
			isCreateSql = true;
		}
		
		if (isOk) {
			PdmParser pp = new PdmParser();
			
			List<Table> tabs = new ArrayList<Table>();
			if ((tablenames == null) || (tablenames.length <= 0)) {
				Table[] tab = pp.parsePDM_VO(null, pdmPath);
				for (Table t : tab) {
					tabs.add(t);
				}
			} else {
				for (int a = 0; a < tablenames.length; a++) {
					Table[] tab = pp.parsePDM_VO(tablenames[a], pdmPath);
					for (Table t : tab) {
						tabs.add(t);
					}
				}
			}
			
			for (int i = 0; i < tabs.size(); i++) {
				Table tab = tabs.get(i);
				
				try {
					tab.setProjectName(projName);
					// 生成java类
					tab.writeout(tab, packPath, savePath, tab.getUser().getCode(), isPersistence, interfaceName);
					// 生成mapper的xml文件
					if (isCreateXml) {
						xml.writeout(tab, packPath, xmlpath);
					}
				} catch (Exception e) {
					LogUtils.logger.info("=====write error[tablename:"	+ tab.getTableName() + ",packPath:" + packPath + ",xmlpath:" + savePath + "]======");
					e.printStackTrace();
				}
			}

			// 判断是否需要生成ddl文件
			if (isCreateSql) {
				List<String> lismsg = generateSQL(tabs);

				try {
					CreateFile.writeFile(lismsg, pathsql, namesql);
				} catch (Exception e) {
					LogUtils.logger.info("=====write sql error ======");
					e.printStackTrace();
				}
			}
		}
	}
	
	private static List<String> generateSQL(List<Table> tabs) {
		List<String> lismsg = new ArrayList<String>();
		
		lismsg.add("spool execute.log\n");
		lismsg.add("\n");
		
		for (int i = 0; i < tabs.size(); i++) {
			Table tab = tabs.get(i);
			Column[] cols = tab.getCols();
			String tabName = tab.getUser().getName() + "." + tab.getTableCode();
			Key pk = tab.getPrimaryKey();
			
			List<String> createTabNsg = generateTableSQL(tabName, cols, pk);
			lismsg.addAll(createTabNsg);
			lismsg.add("\n");

			if (pk != null && pk.getColumnId() != null) {
				lismsg.add(generatePKSQL(tab, tabName, cols, pk));
			}
			lismsg.add("\n");
			
			lismsg.add(generateTabCommentSQL(tab, tabName));
			lismsg.add("\n");
			
			List<String> commentMsg = generateColCommentSQL(tabName, cols);
			lismsg.addAll(commentMsg);
			lismsg.add("\n");
		}
		
		lismsg.add("spool off\n");
		
		return lismsg;
	}
	
	private static Column find(Column [] cols, String code) {
		if (cols == null)
			return null;
		
		for (int i = 0 ; i < cols.length ; ++i) {
			if (code.equals(cols[i].getCode())) {
				return cols[i];
			}
		}
		
		return null;
	}
	
	private static List<String> generateTableSQL(String tabName, Column[] cols, Key pk) {
		List<String> lismsg = new ArrayList<String>();
		
		Column [] keyCols = null;
		if (pk != null) {
			keyCols = pk.qryColumn(cols);
		}
		
		lismsg.add("\n");
		lismsg.add("prompt\n");
		lismsg.add("prompt Create table " + tabName + "\n");
		lismsg.add("prompt =================================\n");
		lismsg.add("prompt\n");
		lismsg.add("prompt\n");
		lismsg.add("create table " + tabName + "\n");
		lismsg.add("(\n");
		for (int j = 0 ; j < cols.length ; ++j) {
			StringBuffer columnBuf = new StringBuffer();
			
			columnBuf.append("  ")
			         .append(String.format("%-20s", cols[j].getCode()))
			         .append(" ")
			         .append(String.format("%-15s", cols[j].getTypefull()));

			Column keyCol = find(keyCols, cols[j].getCode());
			if (keyCol != null) {
				columnBuf.append(" not null");
			}
			if (j < cols.length - 1) {
				columnBuf.append(",");
			}
			columnBuf.append("\n");
			
			lismsg.add(columnBuf.toString());
		}
		lismsg.add(");\n");

		return lismsg;
	}
	
	private static String generatePKSQL(Table tab, String tabName, Column[] cols, Key pk) {
		StringBuffer keyBuf = new StringBuffer();
		Column [] keyCols = pk.qryColumn(cols);
		
		StringBuffer key = new StringBuffer();
		for (int j = 0 ; j < keyCols.length ; ++j) {
			key.append(keyCols[j].getCode());
			if (j < keyCols.length - 1) {
				key.append(",");
			}
		}
		
		keyBuf.append("alter table ")
			  .append(tabName)
			  .append(" add constraint PK_")
			  .append(tab.getTableCode())
			  .append(" primary key")
			  .append("(" + key + ");\n");
		
		return keyBuf.toString();
	}
	
	private static String generateTabCommentSQL(Table tab, String tabName) {
		StringBuffer commentBuf = new StringBuffer();
		
		String comment = "";
		if (tab.getComment() != null && !tab.getComment().isEmpty()) {
			comment = tab.getComment();
		}
		else {
			comment = tab.getTableName();
		}
		
		commentBuf.append("comment on table ")
				  .append(tabName)
				  .append(" is '")
				  .append(comment)
				  .append("';\n");
		
		return commentBuf.toString();
	}

	private static List<String> generateColCommentSQL(String tabName, Column[] cols) {
		List<String> lismsg = new ArrayList<String>();
		
		for (int j = 0 ; j < cols.length ; ++j) {
			StringBuffer colCommentBuf = new StringBuffer();
			
			String colComment = "";
			if (cols[j].getComment() != null && !cols[j].getComment().isEmpty()) {
				colComment = cols[j].getComment();
			}
			else {
				colComment = cols[j].getName();
			}
			
			colCommentBuf.append("comment on column ")
					     .append(tabName)
					     .append(".")
					     .append(cols[j].getCode())
					     .append(" is '")
					     .append(colComment)
					     .append("';\n");
			
			lismsg.add(colCommentBuf.toString());
		}
		
		return lismsg;
	}

	/**
	 * 生成代码入口
	 * @param pathdao
	 * @param pathsvn
	 * @param pathsql
	 * @param namesql
	 * @param projectname
	 * @param pathpack
	 * @param pathpdm
	 * @param tables
	 * @param interfaceName
	 */
	public static void main(String pathdao, String pathsvn,
							String pathsql, String namesql,
							String projectname, String pathpack, String pathpdm, String tables, String interfaceName) {

		List<HashMap<String, String>> lis = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> mp1 = new HashMap<String, String>();

		mp1.put("PROJECTNAME", projectname);
		mp1.put("PACKPATH", pathpack);
		mp1.put("SAVEPATH", pathdao);
		mp1.put("XMLPATH", pathsvn);
		mp1.put("PDMPATH", pathpdm);
		mp1.put("TABLES", tables);
		mp1.put("ISCREATE_PERSISTENCE", "YES");
		lis.add(mp1);

		for (HashMap<String, String> mp : lis) {
			String packPath = (String) mp.get("PACKPATH");
			String savePath = (String) mp.get("SAVEPATH");
			String pdmPath = (String) mp.get("PDMPATH");
			String tablename = (String) mp.get("TABLES");
			String[] tablenames = tablename == null ? null : tablename.split(";");
			String xmlpath = (String) mp.get("XMLPATH");
			String sIsPersistence = (String) mp.get("ISCREATE_PERSISTENCE");
			String projName = (String) mp.get("PROJECTNAME");

			createFile(packPath, savePath, pathsql, namesql, pdmPath, xmlpath, sIsPersistence,
					projName, tablenames, interfaceName);
		}
	}
}
