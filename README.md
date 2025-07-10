# My Notes

- The data of the API appear not normalized. The following cases have been found for Deal Temporal data:
  - Deals that use "open" / "close" fields
  - Deals that use "start" / "end" fields
  - Deals with no temporal data at all

## Problem 1

**Description**: A Deal references a duration of time which falls outside the working hours of the restaurant.
**Example**:
- Restaurant (6pm-9pm)
- Deal (3pm-9pm)

**Solutions**:
    
**A.** Constrain deal validity within restaurant operating hours.
- In that case above deal would be valid between 6pm and 9pm
- ✅ Pros:
  - Respects both sources of truth: doesn't discard deals or blindly trust potentially wrong times.
  - Balances flexibility and safety: preserves deals without exposing times when the restaurant is likely closed.
  - Good user experience: prevents surfacing unusable deals
  - Easy to implement
- ❌ Cons:
  - May lead to partial deals being shown (e.g., "3–9pm deal" shown as "6–9pm"), which could confuse users if not explained.
  - If the restaurant hours are wrong or outdated, it might hide valid early deals.

**B.** Trust the deal hours over the restaurant hours
- Implies the restaurant might be open earlier than listed — risky.
- Could result in surfacing deals when the restaurant is actually closed.
  - ✅ **Pros**:
    - Preserves all deals in full.
    - Useful if restaurant hours are often wrong or incomplete in the data.
  - ❌ Cons:
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

# Task 1

Write an API that returns a list of all the available restaurantDto deals that are active at a specified
time of day.

- The API will take a single parameter named timeOfDay as a string parameter.
Eg: timeOfDay = “10:30” will query for all active deals at 10.30am.


- The API will return an array of objects containing all matching deals, that looks like this:

```json
{
  "deals": [
    {
      "restaurantObjectId": "...",
      "restaurantName": "...",
      "restaurantAddress1": "...",
      "restarantSuburb": "...",
      "restaurantOpen": "...",
      "restaurantClose": "...",
      "dealObjectId": "...",
      "discount": "...",
      "dineIn": "...",
      "lightning": "...",
      "qtyLeft": "..."
    }
  ]
}
```

- You can query this API for the necessary data: https://eccdn.com.au/misc/challengedata.json

## Testing your code

What will the API return for each of these cases:
- timeOfDay = 3:00pm
- timeOfDay = 6:00pm
- timeOfDay = 9:00pm

# Task 2

- Write an API that calculates the ‘peak’ time window, during which most deals are available.
- This API does not take any parameters. 

The API will return an object that looks like this:

```json
{
  "peakTimeStart": "...",
  "peakTimeEnd": "..."
}
```

# Task 3 - Bonus Points

For bonus points you can choose one (or both!) of the following tasks:
- Design a DB schema
- Deploy your solution to AWS

## Design a DB Schema

Select a database technology, and design a schema that you would use to store the data that is
returned by this API:
https://eccdn.com.au/misc/challengedata.json
- You do not need to build or deploy this database, just provide a design for the schema
- Tell us which database you will be using to host this schema and a brief description of
why you chose this database technology
- Provide your design in the form of a diagram, accompanied by brief notes/comments so
that we can understand it.
Provide the diagram in a file that can be easily viewed, eg a jpg/pdf printout or screen
capture, mermaid diagram, draw.io diagram.

## Deploy your solution to AWS
Deploy your solution to AWS. Grant us access to an AWS account including all AWS services
that comprise your solution, and tell us how to test your solution.