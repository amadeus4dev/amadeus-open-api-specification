# OAS Validator

**Last update:** 09/03/2022

## Description

It is necessary to validate multiple [OAS files](https://swagger.io/specification/) in an easy way for multiple files
in the Github Action workflow.
Currently, the project [OpenAPI Style Validator](https://github.com/OpenAPITools/openapi-style-validator) only supports
the validation for a single OAS file.

Using Github Actions, it is possible to execute [JBang Java programs](https://www.jbang.dev), so [OAS Validator](OASValidator.java) add
the missing support from [OpenAPI Style Validator] in a single file.

## Parameters

The Script has support for the following options:

|Option|Type|Possible Values|Description|
|---|---|---|---|
|validateInfoLicense|boolean|`true`, `false`|Ensures that there is a license section in the info section|
|validateInfoDescription|boolean|`true`, `false`|Ensures that there is a description attribute in the info section|
|validateInfoContact|boolean|`true`, `false`|Ensures that there is a contact section in the info section|
|validateOperationOperationId|boolean|`true`, `false`|Ensures that there is an operation id for each operation|
|validateOperationDescription|boolean|`true`, `false`|Ensures that there is a description for each operation|
|validateOperationTag|boolean|`true`, `false`|Ensures that there is a tag for each operation|
|validateOperationSummary|boolean|`true`, `false`|Ensures that there is a summary for each operation|
|validateModelPropertiesExample|boolean|`true`, `false`|Ensures that the properties of the Schemas have an example value defined|
|validateModelPropertiesDescription|boolean|`true`, `false`|Ensures that the properties of the Schemas have a description value defined|
|validateModelRequiredProperties|boolean|`true`, `false`|Ensures that all required properties of the Schemas are listed among their properties|
|validateModelNoLocalDef|boolean|`true`, `false`|Not implemented yet|
|validateNaming|boolean|`true`, `false`|Ensures the names follow a given naming convention|
|ignoreHeaderXNaming|boolean|`true`, `false`|Exclude from validation header parameters starting with `x-`|
|pathNamingConvention|string|`CamelCase`, `HyphenUpperCase`, `HyphenCase`, `UnderscoreCase`, `UnderscoreUpperCase`, `AnyCase`|Naming convention for paths|
|parameterNamingConvention|string|`CamelCase`, `HyphenUpperCase`, `HyphenCase`, `UnderscoreCase`, `UnderscoreUpperCase`, `AnyCase`|Naming convention for parameters|
|headerNamingConvention|string|`CamelCase`, `HyphenUpperCase`, `HyphenCase`, `UnderscoreCase`, `UnderscoreUpperCase`, `AnyCase`|Naming convention for headers|
|propertyNamingConvention|string|`CamelCase`, `HyphenUpperCase`, `HyphenCase`, `UnderscoreCase`, `UnderscoreUpperCase`, `AnyCase`|Naming convention for properties|

**Note:** Documentation extracted from:  [OpenAPI Style Validator](https://github.com/OpenAPITools/openapi-style-validator)

## How to run in Local?

Run the following statement from the path: `.github/`

```
jbang OASValidator.java oasv-1a4dev-config.properties spec  
```
