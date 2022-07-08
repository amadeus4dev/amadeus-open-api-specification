///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.eclipse.microprofile.openapi:microprofile-openapi-api:3.0
//DEPS org.openapitools.empoa:empoa-simple-models-impl:2.0.0
//DEPS org.openapitools.openapistylevalidator:openapi-style-validator-lib:1.7
//DEPS org.openapitools.empoa:empoa-swagger-core:2.0.0
//DEPS io.swagger.parser.v3:swagger-parser:2.0.30
//DEPS org.slf4j:slf4j-jdk14:1.7.36
//DEPS com.fasterxml.jackson.core:jackson-annotations:2.13.1

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.List;
import java.util.Properties;

import org.openapitools.empoa.swagger.core.internal.SwAdapter;
import org.openapitools.openapistylevalidator.OpenApiSpecStyleValidator;
import org.openapitools.openapistylevalidator.ValidatorParameters;
import org.openapitools.openapistylevalidator.ValidatorParameters.NamingConvention;
import org.openapitools.openapistylevalidator.styleerror.StyleError;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import static java.util.function.Predicate.not;

public class OASValidator {

    //Configuration parameters
    //https://github.com/OpenAPITools/openapi-style-validator/blob/master/lib/src/main/java/org/openapitools/openapistylevalidator/ValidatorParameters.java
    Function<String, ValidatorParameters> createValidatorParameters = (configPath) -> {

        try {
            FileReader reader = new FileReader(configPath);
            Properties p = new Properties();
            p.load(reader);

            ValidatorParameters parameters = new ValidatorParameters();

            //INfo
            if(p.getProperty("validate.info.licence") != null) {
                parameters.setValidateInfoLicense(Boolean.parseBoolean(p.getProperty("validate.info.licence")));
            }

            if(p.getProperty("validate.info.description") != null) {
                parameters.setValidateInfoDescription(Boolean.parseBoolean(p.getProperty("validate.info.description")));
            }

            if(p.getProperty("validate.info.contact") != null) {
                parameters.setValidateInfoContact(Boolean.parseBoolean(p.getProperty("validate.info.contact")));
            }

            //Operation
            if(p.getProperty("validate.operation.operationid") != null) {
                parameters.setValidateOperationOperationId(Boolean.parseBoolean(p.getProperty("validate.operation.operationid")));
            }

            if(p.getProperty("validate.operation.description") != null) {
                parameters.setValidateOperationDescription(Boolean.parseBoolean(p.getProperty("validate.operation.description")));
            }

            if(p.getProperty("validate.operation.tag") != null) {
                parameters.setValidateOperationTag(Boolean.parseBoolean(p.getProperty("validate.operation.tag")));
            }

            if(p.getProperty("validate.operation.summary") != null) {
                parameters.setValidateOperationSummary(Boolean.parseBoolean(p.getProperty("validate.operation.summary")));
            }

            if(p.getProperty("validate.model.properties.example") != null) {
                parameters.setValidateModelPropertiesExample(Boolean.parseBoolean(p.getProperty("validate.model.properties.example")));
            }

            if(p.getProperty("validate.model.properties.description") != null) {
                parameters.setValidateModelPropertiesDescription(Boolean.parseBoolean(p.getProperty("validate.model.properties.description")));
            }

            if(p.getProperty("validate.model.properties.required") != null) {
                parameters.setValidateModelRequiredProperties(Boolean.parseBoolean(p.getProperty("validate.model.properties.required")));
            }

            if(p.getProperty("validate.model.nolocaldef") != null) {
                parameters.setValidateModelNoLocalDef(Boolean.parseBoolean(p.getProperty("validate.model.nolocaldef")));
            }

            //Naming
            if(p.getProperty("validate.naming") != null) {
                parameters.setValidateNaming(Boolean.parseBoolean(p.getProperty("validate.naming")));
            }

            if(p.getProperty("validate.naming.ignoreheaderx") != null) {
                parameters.setIgnoreHeaderXNaming(Boolean.parseBoolean(p.getProperty("validate.naming.ignoreheaderx")));
            }

            if(p.getProperty("validate.naming.path.convention") != null) {
                NamingConvention namingConvention = NamingConvention.valueOf(p.getProperty("validate.naming.path.convention"));
                parameters.setPathNamingConvention(namingConvention);
            }

            if(p.getProperty("validate.naming.header.convention") != null) {
                NamingConvention namingConvention = NamingConvention.valueOf(p.getProperty("validate.naming.header.convention"));
                parameters.setHeaderNamingConvention(namingConvention);
            }

            if(p.getProperty("validate.naming.parameter.convention") != null) {
                NamingConvention namingConvention = NamingConvention.valueOf(p.getProperty("validate.naming.parameter.convention"));
                parameters.setParameterNamingConvention(namingConvention);
            }

            if(p.getProperty("validate.naming.property.convention") != null) {
                NamingConvention namingConvention = NamingConvention.valueOf(p.getProperty("validate.naming.property.convention"));
                parameters.setPropertyNamingConvention(namingConvention);
            }

            return parameters;

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    };

    // OAS Validation process
    // Original idea from: https://github.com/OpenAPITools/openapi-style-validator/tree/master/maven-plugin
    BiFunction<ValidatorParameters, String, Integer> validate = (config, file) -> {

        try {
            OpenAPIParser openApiParser = new OpenAPIParser();
            ParseOptions parseOptions = new ParseOptions();
            parseOptions.setResolve(true);

            SwaggerParseResult parserResult = openApiParser.readLocation(file, null, parseOptions);
            io.swagger.v3.oas.models.OpenAPI swaggerOpenAPI = parserResult.getOpenAPI();

            org.eclipse.microprofile.openapi.models.OpenAPI openAPI = SwAdapter.toOpenAPI(swaggerOpenAPI);
            OpenApiSpecStyleValidator openApiSpecStyleValidator = new OpenApiSpecStyleValidator(openAPI);

            ValidatorParameters parameters = config;
            List<StyleError> result = openApiSpecStyleValidator.validate(parameters);
            if (!result.isEmpty()) {
                result.stream().map(StyleError::toString).forEach(m -> System.out.println(String.format("\t%s", m)));
                return 0;
            }
            return 1;

        } catch (RuntimeException e) {
            System.out.println("Error in parsing process");
            return 0;
        }
    };

    public static void main(String[] args) throws IOException {
        System.out.println("Validating the following OAS files:");

        //Process
        var configName = args[0];
        var specDir = args[1];
        var userDirPath = new File(System.getProperty("user.dir"));
        var specPath = userDirPath.getParent() + "/" + specDir;
        var configPath = userDirPath + "/" + configName;

        OASValidator oasValidator = new OASValidator();
        ValidatorParameters config = oasValidator.createValidatorParameters.apply(configPath);

        var specCounter = Files.walk(Path.of(specPath))
                .filter(not(Files::isDirectory))
                .count();

        var specValidatedCounter = Files.walk(Path.of(specPath))
                .filter(not(Files::isDirectory))
                .map(String::valueOf)
                .peek(System.out::println)
                .map(spec -> oasValidator.validate.apply(config, spec))
                .reduce(0, Integer::sum);

        //Assert
        if(specCounter != specValidatedCounter) {
            System.out.println("Review the OAS files provided, the validation failed.");
            System.exit(-1);
        } else {
            System.out.println(specValidatedCounter + " OAS files was validated successfully.");
            System.exit(0);
        }
    }
}