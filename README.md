# RedShirt Example SQS Lambda (Java)

Template example of an AWS Lambda configured to react to an SQS queue with a Java handler.

Features over baseline AWS template:

* Dependency injection scaffolding (Google Guice)
* Environment variable-based configuration
* Multi-threading
* Batch failure handling

This Java template was created as part of an experiment in using Cursor to port a simple .NET project ([RedShirt.Example.SqsLambda](https://github.com/adeutscher/RedShirt.Example.SqsLambda)) into Java. See below for more notes on how well the prompt did as well as adjustments that had to be made and considerations for future development on this Java template.

# Initialisation

To change the package and Maven artifact names en-masse for your purposes, use the `init-repo.sh` script:

```bash
bash init-repo.sh com.acme.orders.sqslambda
```

This replaces `foo.bar` and `foo-bar` throughout the project (and renames module directories / Java source trees).

# Local testing

```bash
docker compose -f test/local/docker-compose.yaml up --build
python test/local/send-request.py
```

# Cursor Notes

This template was partially created as a Cursor experiment to see how the technology would handle converting a project from one established language to another. The agent's mandate was broad in that it was essentially given the instruction to take a .NET project from one location and translate it to a Java project in another location. From another angle, the parameters of this task were specific in that it was given a very specific set of classes to port over. The Composer model was used for this experiment.

The original prompt made significant progress, but there were several adjustments that needed to be made:

* JDK 21 used in `pom.xml` and the Docker image choices instead of the most recent LTS of JDK 25.
* Had to update external libraries.
* The testing library Mockito was raising a warning over build settings.
* The `send-request.py` convenience script for local testing did not catch that the JSON parsing for the Java `MessageHandler` class that `Function.class` inherits from is more case-sensitive than the `DefaultLambdaJsonSerializer` from the .NET version, resulting in null messages.
* As part of the setup, I opted to go with Google Guice due to a search's recommendation as a lightweight and Lambda-friendly dependency injection framework. That being said, there is currently an issue with JDK 24+ projects and Guice where the injection process will call a deprecated function in. Part of me wonders if this was part of the reason that JDK 21 was initially chosen. The local test of the template code is functional, but a giant warning about a deprecated method call doesn't feel great.
* The code itself didn't have too many places to go wrong in terms of fundamentals. However there are some considerations:
  * Some of the code felt a bit weird to me in terms of double-getters, though they were simple getters (e.g. a ternary statement on `getRecords()` with a possible follow-up action to `getRecords()`
  * The environment-variable-based configuration system seems to parse and test correctly, however the options then seem to have been left behind in dependency injection initialization. There are Java frameworks that are friendlier to this, but the obsensibly more Lambda-friendly systems that I chose do not seem to have a 1-to-1 of .NET's style of using `IOptions<ConfigurationModel>` and its related setup methods. Cursor's failure to account for this is part a guidance issue on my part, as the original .NET Lambda template didn't actually have a need for any explicit `IOptions` use compared to my more complex JobWorker template. It was also a bit ambitious of me to assume that something very Microsoft-y like `IOptions` would have an immediate Java equivalent.

# References

* https://github.com/google/guice
* https://docs.aws.amazon.com/lambda/latest/dg/services-sqs-errorhandling.html
