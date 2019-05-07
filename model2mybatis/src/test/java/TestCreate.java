import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.csnowfox.maven.plugin.model2mybatis.Model2MybatisJavaCode;

/**
 * 本地测试类
 */
public class TestCreate {

	public static void main(String[] args){

		String pathdao = "D:/temp/api/";
		String pathpack = "cn.shijinshi.unusual.transaction.dao";
		String projectname = "hj-acp";
		String pathpdm = "E:\\github\\model2mybatis\\model2mybatis\\src\\test\\java\\fund.sql";
		String tables = "cust_relate;risk_stocks;sh50_stocks;stock_yes_info";
		String pathsql = "D:/temp";
		String namesql = "MYTest.sql";
		String interfaceName = "com.csnowfox.hj.acp.api.service.SqlMapper";


		File pathFile = new File(pathpdm);
		if(pathFile.isFile()){
			// if pathpdm is file
			Model2MybatisJavaCode.createFiles(pathdao, pathsql, namesql, projectname, pathpack, pathpdm, tables, interfaceName);
		}else{
			//  if pathpdm is dir
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
						Model2MybatisJavaCode.createFiles(pathdao, pathsql, namesql, projectname, pathpack.toLowerCase(), tmpFile.getAbsolutePath(), newTables, interfaceName);
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
}
