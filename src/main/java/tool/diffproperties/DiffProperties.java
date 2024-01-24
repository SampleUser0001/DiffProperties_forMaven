package tool.diffproperties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DiffProperties {

    public static void main(String[] args) throws IOException{

        StringBuffer buffer = new StringBuffer();
        buffer.append("key").append("\t");

        int argsIndex = 0;
        final Path INPUT_PATH = Paths.get(args[argsIndex++]);
        final String PROPERTIES_FILE_NAME = args[argsIndex++];
        final Path OUTPUT_FILE = Paths.get(args[argsIndex++]);
        
        System.out.println("INPUT_PATH : " + INPUT_PATH);
        System.out.println("PROPERTIES_FILE_NAME : " + PROPERTIES_FILE_NAME);
        System.out.println("OUTPUT_FILE : " + OUTPUT_FILE);

        Map<Path , Properties> propertiesMap = new LinkedHashMap<Path , Properties>();
        List<String> keyList = new ArrayList<String>();
        try(DirectoryStream<Path> inputPathStream = Files.newDirectoryStream(INPUT_PATH)){
            inputPathStream.forEach( propertiesDir -> {
                Path propertiesPath = Paths.get(propertiesDir.toString(),PROPERTIES_FILE_NAME);
                System.out.println(propertiesPath.toString());
                Properties prop = new Properties();
                try {
                    prop.load(Files.newBufferedReader(propertiesPath,StandardCharsets.UTF_8));

                    propertiesMap.put(propertiesPath,prop);

                    for(Object value : prop.keySet()){
                        keyList.add(value.toString());
                    }
                    buffer.append(propertiesPath.getParent().toFile().getName()).append("\t");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        buffer.append("差分\r\n");

        keyList.stream().distinct().forEach( key -> {
            System.out.println(key);
            boolean isDiff = false;
            String tmpValue = null;
            buffer.append(key).append("\t");
            for(Properties prop : propertiesMap.values()){
                String value = prop.getProperty(key);
                if(value == null){
                    value = "指定なし";
                } else {
                    value = replace(value);
                }

                if(tmpValue == null){
                    tmpValue = value;
                }

                // 差分有無チェック
                if(!isDiff && !tmpValue.equals(value)){
                    isDiff = true;
                }

                buffer.append("\"" + value + "\"").append("\t");
            }

            // 差分出力
            if(isDiff){
                buffer.append("あり");
            } else {
                buffer.append("なし");
            }
            buffer.append("\r\n");
        });

        // Excelで開くのでできればShift-JISだが・・・
//         Charset charset = Charset.forName("Shift-JIS");
//         try{
//             write(buffer, OUTPUT_FILE, charset);
//         } catch (UnmappableCharacterException e){
//             // Shift-JISはこけた。UTF-8
//             e.printStackTrace();
//             charset = StandardCharsets.UTF_8;
//             write(buffer, OUTPUT_FILE, charset);
//         }
        Charset charset = StandardCharsets.UTF_8;
        write(buffer, OUTPUT_FILE, charset);

        System.out.println(charset.name());

    }

    private static void write(StringBuffer buffer , Path outputFilePath, Charset charset) throws IOException{
        Files.write(
                outputFilePath,
                Arrays.asList(buffer.toString().split("\r\n")) ,
                charset,
                StandardOpenOption.TRUNCATE_EXISTING);

    }

    /**
     * CR,LF,タブを"\r","\n","\t"の文字として出力できるように置換する。<br>
     * くそー気に入らない。
     * @return 置換後文字列
     */
    private static String replace(String str){
        return str
                .replaceAll("\\r", "\\\\r")
                .replaceAll("\\n", "\\\\n");
    }
}