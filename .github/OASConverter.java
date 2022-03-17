///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS com.fasterxml.jackson.core:jackson-databind:2.13.2
//DEPS com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2
//DEPS com.google.code.gson:gson:2.8.7

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static java.util.function.Predicate.not;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

//jbang OASConverter.java folder2
public class OASConverter {

    public static void main(String... args) throws IOException {

        System.out.println("Process to clone OAS files in YAML format into JSON format");

        //Convert YAML to JSON
        Function<String, Integer> convertYamlToJson = (yaml) -> {
            try {

                //Convert YAML -> JSON
                ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
                Object obj = yamlReader.readValue(new File(yaml), Object.class);
                ObjectMapper jsonWriter = new ObjectMapper();
                var json = jsonWriter.writeValueAsString(obj);

                //Prettify JSON
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement je = JsonParser.parseString(json);
                String prettyJsonString = gson.toJson(je);

                //Store in disk
                System.out.println(yaml);
                var fileName = yaml.replace("yaml", "json");
                System.out.println(fileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                writer.write(prettyJsonString);
                writer.close();

                return 1;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return 0;
            }
        };

        //Process
        var specDir = args[0];
        var userDirPath = new File(System.getProperty("user.dir"));
        var specPath = userDirPath.getParent() + "/" + specDir;
        var validExtension = ".yaml";

        var specCounter = Files.walk(Path.of(specPath))
                .filter(not(Files::isDirectory))
                .filter(f -> f.toString().endsWith(validExtension))
                .map(String::valueOf)
                .peek(System.out::println)
                .map(convertYamlToJson)
                .peek(System.out::println)
                .count();
    }
}