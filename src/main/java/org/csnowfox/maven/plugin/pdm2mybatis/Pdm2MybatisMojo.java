package org.csnowfox.maven.plugin.pdm2mybatis;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.csnowfox.maven.plugin.pdm2mybatis.utils.MavenLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 通过pdm文件生成mybatis generator格式的数据库访问类
 * maven插件入口
 * @goal echo
 * 
 */
public class Pdm2MybatisMojo extends AbstractMojo {

	/**
	 * 指定生成的dao文件所在的路径
     * @parameter expression="${pathdao}"
     * @required
     */
	private String pathdao;
	
	/**
	 * 生成java类的包基础路径
     * @parameter expression="${pathpack}"
     * @required
     */
	private String pathpack;
	
	/**
	 * 项目名称
     * @parameter expression="${projectname}"
     * @required
     */
	private String projectname;
	
	/**
	 * 建模文件pdm所在路径
     * @parameter expression="${pathpdm}"
     * @required
     */
	private String pathpdm;
	
	/**
	 * 数据库表，格式：用户名1:表名1;表名2;表名3|用户名2:表名4;表名5
     * @parameter expression="${tables}"
     * @required
     */
	private String tables;
	
	/**
	 * 编译生成sql所在路径
     * @parameter expression="${pathsql}"
     * @required
     */
	private String pathsql;
	
	/**
	 * 编译生成sql所在路径文件名
     * @parameter expression="${namesql}"
     * @required
     */
	private String namesql;
	
	/**
	 * 生成mapper类继承的父接口
     * @parameter expression="${interfaceName}"
     * @required
     */
	private String interfaceName;

	/**
	 *
	 * @parameter expression="${basedir}"
	 * @required
	 */
	private File basedir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("根据pdm生成dao代码");

		// 初始化日志系统
		MavenLogger.init(getLog());

		String baseDirStr = "";
		try {
			baseDirStr = basedir.getCanonicalPath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		pathdao = baseDirStr + File.separator + pathdao;
		pathpdm = baseDirStr + File.separator + pathpdm;
		pathsql = baseDirStr + File.separator + pathsql;

		getLog().info("pathdao = " + pathdao);
		getLog().info("pathpdm = " + pathpdm);
		getLog().info("pathsql = " + pathsql);
		
		/**
		 * 支持多文件
		 * */
		File pathFile = new File(pathpdm);
		if(pathFile.isFile()){
			Pdm2MybatisJavaCode.createFiles(pathdao, pathsql, namesql,
					projectname, pathpack, pathpdm, tables, interfaceName);
		}else{
			try{
				Map<String,String> userMap = new HashMap<String, String>();
				StringTokenizer fileTokenizer = new StringTokenizer(tables, "|");
				while(fileTokenizer.hasMoreTokens()){
					String fileToken = fileTokenizer.nextToken().trim();
					StringTokenizer userTokenizer = new StringTokenizer(fileToken, ":");
					String userToken = userTokenizer.nextToken().trim();
					String tableListString = userTokenizer.nextToken().trim();
					userMap.put(userToken, tableListString);
				}
				File[] subFile = pathFile.listFiles();
				for(File tmpFile : subFile){
					String tmpFileName = tmpFile.getName();
					int dotIndex = tmpFileName.lastIndexOf(".");
					tmpFileName = tmpFileName.substring(0, dotIndex);
					if(userMap.containsKey(tmpFileName)){
						String newTables = userMap.get(tmpFileName);
						Pdm2MybatisJavaCode.createFiles(pathdao, pathsql, tmpFileName + "_" + namesql,
								projectname, pathpack.toLowerCase(), 
								tmpFile.getAbsolutePath(), newTables, interfaceName);
					}
				}
			}catch(Exception ex){
				getLog().info("pathpdm = " + pathpdm + "do not exist");
			}
		}
	}
}
