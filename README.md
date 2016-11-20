# DatabaseObfuscator
Integrates random data and the Java Faker library to allow the user to obfuscate a database

The project uses lombok so you will need to set that up in your IDE of choice.

Currently the only way to run this is to modify application.properties with the connection parameters for your database and the jdbc driver of choice, and then to build a command inside of the DBObfuscatorApplication.

There's no integrity checks right now on the command so it will just run until it bombs out.

I'm planning on adding support for JSON so that the obfuscate command can be built that way. Once it's out, the basic structure for a command is:
~~~~
{
    schema: "schemaName"
    tables: [{
        name: "myTableName",
        columns: [
          {
            name: "employeeName",
            fakerStrategy: "name.fullName"
          },
          {
            name: "accountNumber",
            strategy: "FINANCIAL_ACCOUNT"
          }
        ]
    }]
}
~~~~

**fakerStrategy** allows use to specify any faker data generation path. Currently faker methods that take input parameters are not supported but it would probably be simple to support a syntax like "address.streetAddress$true".

**strategy** will refer to an internal enumeration that can be expanded to include different datapoints
