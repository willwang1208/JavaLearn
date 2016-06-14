package org.whb.postgres.tools;

import org.whb.postgres.tools.bsonimport.BsonImport;
import org.whb.postgres.tools.presstest.PressTest;
/**
 * 
 * nohup /opt/jdk1.8.0_66/bin/java -jar postgresql-tools-jar-with-dependencies.jar BsonImport jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null /mfpdata1/2016-05-26/dump/jelly_ios/ >> out22.log 2>&1 &
 * 
 * sh psql_pt_runner.sh start
 * 
 * @author whb
 *
 */
public class PostgresTools {

    public static void main(String[] args) {
        String methodName = args[0];
        
        if("BsonImport".equalsIgnoreCase(methodName)){
            BsonImport.main(parseArgs(args));
        }else if("PressTest".equalsIgnoreCase(methodName)){
            PressTest.main(parseArgs(args));
        }
    }
    
    public static String[] parseArgs(String[] args){
        String[] dest = new String[args.length - 1];
        if(dest.length > 0){
            System.arraycopy(args, 1, dest, 0, dest.length);
        }
        return dest;
    }
}
