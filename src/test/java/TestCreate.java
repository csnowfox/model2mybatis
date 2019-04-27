import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.csnowfox.maven.plugin.pdm2mybatis.Create;


public class TestCreate {

	public static void main(String[] args){

		//String pathdao = "E:/workspace/P_170117_bugfix_mangoTV/isp/isp-api/src/main/java/com/csnowfox/isp/api/";
//		String pathdao = "E:/workspace/P_170209_B02_xinshou_clone/isp/isp-api/src/main/java/com/csnowfox/isp/api/";
		String pathdao = "Q:/temp/api/";
		//String pathsvn = "E:/workspace/P_170117_bugfix_mangoTV/isp/isp-api/src/main/resource/mybatis-config/mapper/";
		String pathsvn = "Q:/temp/mapper/";
		String pathpack = "com.csnowfox.hj.acp.api";
//		String pathpack = "com.csnowfox.isp.api";
		String projectname = "hj-acp";
		//String pathpdm = "E:/workspace/P_161216_B01_dw/hjpt/hj-acp/src/main/resource/mybatis-config/pdm/";
		String pathpdm = "Q:/temp/pdm/cif.pdm";
//		String tables = "hjpt:PRODUCT_EXTENSION";
		String tables = "ORDER_TAG_RELATE";
		String pathsql = "Q:/temp";
		String namesql = "MYTest.sql";
		String interfaceName = "com.csnowfox.hj.acp.api.service.SqlMapper";
		
		File pathFile = new File(pathpdm);
		if(pathFile.isFile()){
			Create.main(pathdao, pathsvn, pathsql, namesql, projectname, pathpack, pathpdm, tables, interfaceName);
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
						Create.main(pathdao, pathsvn, pathsql, namesql, projectname, pathpack.toLowerCase(), tmpFile.getAbsolutePath(), newTables, interfaceName);
					}
				}
			}catch(Exception ex){
				System.out.println(ex);
			}
		}
		
	}
}
