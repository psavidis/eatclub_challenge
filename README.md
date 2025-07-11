# My Notes

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