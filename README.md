# How to run the Application

The application can be run using:

- **IntelliJ IDEA Runner**: Simply run the `main` method of `org.eatclub.challenge.Main` class.
- **Maven Command Line**
  - `Option1`
    -  Run `mvn spring-boot:run` in the project root directory (`EatClub_Technical_Challenge`)
  - `Option 2`
    - Build the project using `mvn clean package -DskipTests` in the project root directory (`EatClub_Technical_Challenge`)
    - Run `java -jar target/app.jar`

Using Java `24` and maven `3.9.10`

**Note:** It might be possible to run the project using earlier java versions, a speculation that has not been tested.

# How to run the Tests

- **Maven Command Line**
  - Run `mvn clean install` in the project root directory (`EatClub_Technical_Challenge`)
    - This will run both unit and integration tests.
- **IntelliJ IDEA Runner**
  - `mvn clean package -DskipTests` - Builds the `app.jar` into the `target` folder
  - Run the tests using the `Run Tests` button in IntelliJ IDEA
    - This will run both unit and integration tests.
    - Integration tests will pickup the `app.jar` and build a docker image from it and continue their execution.

# My Solution

The solution encountered several challenges with ambiguous requirements during the development of the challenge.
To facilitate progress, a problem analysis was conducted to identify trade-offs and make decisions.
The decisions made are documented under the **Problems** section.

A summary of the business rules implemented in the solution is provided in the **Summary of Business Rules** section.

For information on the technical decisions made, refer to the **Technical Decisions** section.

## Summary of Business Rules

**Restaurant Hours**
- **Reliability**: In a nutshell, the application treats restaurant hours as more reliable than deal hours (**source of truth**).
- **No normalisation**: If a restaurant has invalid hours, the application does not apply normalisation to the restaurant hours (e.g assume the restaurant closes at 12:00am given an invalid end time)

**Deal Hours**
- **Flexibility**:
  - **A.** The application favors deal start if its within the restaurant hours.
  - **B.** Prefer restaurant open / close over deal open / close if the deal start is outside the restaurant hours.
  - **C.** If deal hours are not available (both start, end), the application uses the restaurant hours as a fallback.

**Deal Logging**
- **Invalid Deals**: The application logs invalid deals that lack temporal information under `logs/invalid-deals.log`.
  - **Why**
    - The logging acts as a PoC for demonstrating the necessity to log invalidate data for future handling
    - In a more complex system, these entries could be dumped into other systems for further processing, such as a data warehouse or a monitoring system.

**Comments**

- Given more time, the problem analysis and decision making would have been better documented in Github Tickets.
- The commit history does not follow any convention on the messages due to time constraints to make fast progress.
  - In a real project, a format that categorizes commits by type (e.g., `feat`, `fix`, `chore`) would have been used instead.

# Problems

## Problem 1

**Description**: A Deal references a duration of time which falls outside the working hours of the restaurant.

**Example**:
- Restaurant (6pm-9pm)
- Deal (3pm-9pm)

**Solutions**:
    
**A.** Constrain deal validity within restaurant operating hours.
- In that case above deal would be valid between 6pm and 9pm
- ✅ **Pros**:
  - Respects both sources of truth: doesn't discard deals or blindly trust potentially wrong times.
  - Balances flexibility and safety: preserves deals without exposing times when the restaurant is likely closed.
  - Good user experience: prevents surfacing unusable deals
  - Easy to implement
- ❌ **Cons**:
  - May lead to partial deals being shown (e.g., "3–9pm deal" shown as "6–9pm"), which could confuse users if not explained.
  - If the restaurant hours are wrong or outdated, it might hide valid early deals.

**B.** Trust the deal hours over the restaurant hours
- Implies the restaurant might be open earlier than listed — risky.
- Could result in surfacing deals when the restaurant is actually closed.
  - ✅ **Pros**:
    - Preserves all deals in full.
    - Useful if restaurant hours are often wrong or incomplete in the data.
  - ❌ **Cons**:
    - False positives

**C.** Ignore the deals that start before the restaurant opens
- In that case above deal would not be valid
- ✅ **Pros**:
  - Strict and clean: avoids bad user experiences.
  - Enforces trust in restaurant hours. 
  - Simple to implement.
- ❌ **Cons**:
  - Data loss: potentially valid deals discarded.
  - May be aggressive — e.g., a 5:30pm deal for a restaurant that opens at 6pm will be discarded.
  - Could frustrate businesses if they see their deals hidden

**Decision**

**Solution A** is chosen. 

**Why**: Favoring
- Flexibility over Data Loss: Preserving deals is important, even if it means showing them during restaurant hours.
- Validity of Restaurant Hours Over Deal Hours: Restaurant hours are more likely to be accurate, so we trust them more.

## Problem 2

**Description:** The data of the API appears to be not normalized. The following cases have been found for Deal Temporal data:

- Deals that use "open" / "close" fields
- Deals that use "start" / "end" fields
- Deals with no temporal data at all

**Solutions:**

- **A.** Normalize all deals to use "start" / "end" fields
  - ✅ **Pros**:
    - Consistent format across all deals.
    - Easier to query and manipulate.
  - ❌ **Cons**:
    - Requires transformation of existing data.
    - May lose some semantic meaning of "open"/"close".
    - Cannot recover from totally missing time
    
- **B.** Fallback to Restaurant Hours when Deal Time is Missing
  - ✅ **Pros**:
    - Provides a default time range for deals without explicit times.
    - Ensures all deals have a valid time range.
  - ❌ **Cons**:
    - Assumes restaurant hours represent deal times
    - May mislead users about actual deal times
    - Data quality risk if restaurant times are wrong
      
- **C**: Ignore Deals with Missing Temporal Data
  - ✅ **Pros**:
    - Prevents invalid or confusing deals
    - Avoids making assumptions
    - Easy to implement
  - ❌ **Cons**:
    - May hide valid deals that simply lack data
    - Data coverage drops
    - Risk of silently discarding real offers
      
- **D:** Log or Flag Invalid Deals for Cleanup
    - ✅ **Pros**:
      - Supports long-term data cleanup
      - Transparency for debugging
      - Complements other solutions
    - ❌ **Cons**:
      - Doesn’t fix the issue immediately
      - Needs log monitoring or moderation tools
      - Adds system complexity
        
- **E.** Trust Deal Time Over Restaurant Time
    - ✅ **Pros**:
        - Preserves all deal data
        - Useful when restaurant hours are unreliable
    - ❌ **Cons**:
        - Risks showing deals when restaurants are closed
        - Bad user experience, potential business complaints

**Decision**

**A Combination of Solutions A + B + C** is chosen.

**Why**: Favoring
- Functional deals
- Safe user experience
- Long-term path to improve data

## Technical Decisions

**Build**

- **Java Version**: `Java 24`
- **Maven Version**: `3.9.10`

**Application**

- `Spring Boot` is used for the application
    - **Why:**
        - Fast development speed
        - DI, Web and Rest Support

**Testing**

- `Docker` is used to package the application into a container and run Integration Tests against the API
    - **Why**: To treat the application as a black box and ensure it works without knowing any details of the internal implementation.
- `Test Containers` latest version is used to build a docker image dynamically from a Dockerfile and run the application in a container
    - **Why**: To automate the process of running the tests as much as possible.
    - The only prerequisite is a built spring boot application jar file in the target folder
    - The docker image will be built by the tests on the fly upon test execution.
- `LogCaptor` is used for testing the logging output of the application
    - **Why**: The logging of invalid deals is an important business requirements for this solution which deserves its own testing.
- `AssertJ` is used for fluent assertions in the tests
    - **Why**: To make the tests more readable and maintainable.
- `Swagger` is used to document the REST API
    - **Why**
        - API documentation
        - Ease of manual testing