# Testing and Code Quality - Bruma Café

This folder contains the backend (Java) unit tests using the **JUnit 5** (Jupiter) framework alongside **AEM Mocks**. The goal is to ensure the code is functioning correctly before deployment.

## How to Run the Tests

To quickly execute the test suite, open your terminal at the root of the project (`Bruma-Cafe`) and run the following command:

```bash
mvn clean test -pl core
```

*The `-pl core` flag tells Maven to build only the `core` module, saving time during local validation.*

## Code Coverage Report (JaCoCo)

The project is configured with the **JaCoCo** plugin. Whenever you run the tests using the command above, an HTML coverage report will be generated automatically.

### How to view it:

1. Navigate to the `core/target/site/jacoco/` folder.
2. Open the `index.html` file in any web browser.
3. You will see a detailed dashboard showing the percentage of classes, methods, and lines covered by the unit tests. Always strive to keep this rate as high as possible!

## Example Test (To Copy)

If you are new to the project or to AEM Mocks, take a look at the **`ExampleAemMockTest.java`** file.
It was written specifically as a **Living Tutorial**. In it, you will find the basic structure of a test:

- How to initialize the `AemContext`
- How to create virtual nodes in the JCR (`/var/brumacafe`)
- How to simulate *Happy Paths* and Failure scenarios (Exceptions)

Feel free to copy the structure of this file whenever you need to create a new test for your OSGi Service, Sling Model, or Servlet!
