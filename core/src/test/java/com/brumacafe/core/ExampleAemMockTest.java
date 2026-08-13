package com.brumacafe.core;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ☕ TEST TEMPLATE FOR BRUMA CAFÉ TEAM ☕
 * 
 * This class serves as a tutorial on how to write tests using JUnit 5 and AEM Mocks.
 * Copy the structure of this class to create your own tests for components,
 * servlets, and OSGi services.
 */
@ExtendWith(AemContextExtension.class) // Enables AEM Mocks for tests in JUnit 5
class ExampleAemMockTest {

    // AemContext simulates the AEM environment, such as the JCR, Sling Models, and OSGi Services
    public final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // The setUp method is executed BEFORE each test.
        // Use it to prepare the virtual environment, inject configurations, and create resources in the in-memory JCR.
        
        // Example: Creating the virtual folder structure
        context.create().resource("/var/brumacafe");
    }

    @Test
    void testHappyPath_NodeCreationInJcr() throws PersistenceException {
        // 1. ARRANGE: Data we are going to save in the virtual JCR
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "Gustavo");
        properties.put("message", "Success test!");

        // 2. ACT: Simulate the creation of a node in the JCR using the mocked ResourceResolver
        Resource newNode = context.resourceResolver().create(
                context.resourceResolver().getResource("/var/brumacafe"), 
                "contact-1", 
                properties
        );
        context.resourceResolver().commit(); // Confirms the virtual save

        // 3. ASSERT: Verify if the node was actually created and properties are correct
        assertNotNull(newNode, "The contact node should not be null after creation");
        assertEquals("Gustavo", newNode.getValueMap().get("name", String.class), "The 'name' property should match the saved value");
        assertEquals("Success test!", newNode.getValueMap().get("message", String.class));
    }

    @Test
    void testFailurePath_PersistenceError() {
        // In this scenario, we demonstrate how to test exceptions, for example, when
        // attempting to create a node under a null parent path.
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "Error");

        // We use assertThrows to ensure the code throws a NullPointerException
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            // Attempting to create the node in a null parent path will cause an error
            context.resourceResolver().create(null, "failure", properties);
        });

        // We verify the error to ensure proper handling
        assertNotNull(exception);
    }
}
