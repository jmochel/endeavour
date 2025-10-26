# Setting up a model for testing 

A failure analyis can be created with any combination of  
* failure type enum
* cause (an exception)
* title string
* template string, 
* argument object array
* detail string

* The failure type can be a standard default failure type enum (if no type is provided) or a custom failure type. 
* The cause can be not provided, or it can be an exception.
* The title can be not provided, or it can be a string.
* The template can be not provided, or it can be a string.
* The arguments can be not provided, or it can be an object array.
* The detail can be not provided, or it can be a string.

* If failure analysis is created without a failure type and there is no cause, then a generic default failure type is used.
* If failure analysis is created without a failure type and there is a cause, then a generic default exception failure type is used.
* If failure analysis is created with a failure type, then the title and template are taken from the failure type 
* If failure analysis is created with a cause, then the cause supplies a detail string from its message.

* A template is used to generate a detail string. 
* Any placeholder of the template is replaced with the corresponding argument or "NotSupplied" if there is no corresponding argument. 

* A provided title overrides a title derived from a failure type.
* A provided template overrides a template derived from a failure type.
* A provided detail string overrides a detail string derived from a template and arguments.

## Scenarios

| Component 1 | Component 2 | Component 3 | Expected Behavior |
|-------------|-------------|-------------|------------------|
| Failure Type | Cause | Title | Result |
| ✔ No Type | No Cause | No Title | Generic type title, empty detail |
| ✔ No Type | No Cause | With Title | Provided title, empty detail |
| No Type | With Cause | No Title | Generic exception title, cause message as detail |
| No Type | With Cause | With Title | Provided title, cause message as detail |
| Custom Type | No Cause | No Title | Type's title, empty detail |
| Custom Type | No Cause | With Title | Provided title, empty detail |
| Custom Type | With Cause | No Title | Type's title, cause message as detail |
| Custom Type | With Cause | With Title | Provided title, cause message as detail |
| Template | Args | Detail | Result |
|-------------|----------|------------|------------|
| No Template | No Args | No Detail | Empty detail |
| No Template | No Args | With Detail | Provided detail |
| No Template | With Args | No Detail | Empty detail |
| No Template | With Args | With Detail | Provided detail |
| With Template | No Args | No Detail | Template with "NotSupplied" |
| With Template | No Args | With Detail | Provided detail |
| With Template | With Args | No Detail | Expanded template |
| With Template | With Args | With Detail | Provided detail |
| Cause Message | Template | Detail | Result |
|------------------|-------------|------------|------------|
| No Message | No Template | No Detail | Empty detail |
| No Message | No Template | With Detail | Provided detail |
| No Message | With Template | No Detail | Template with "NotSupplied" |
| No Message | With Template | With Detail | Provided detail |
| With Message | No Template | No Detail | Cause message as detail |
| With Message | No Template | With Detail | Provided detail |
| With Message | With Template | No Detail | Cause message as detail |
| With Message | With Template | With Detail | Provided detail |
Precedence Rules Matrix:
| Scenario | Title Source | Detail Source | Final Title | Final Detail |
|----------|-------------|---------------|-------------|--------------|
| Type Only | Type | None | Type's title | Empty |
| Type + Title | Title | None | Provided title | Empty |
| Type + Cause | Type | Cause | Type's title | Cause message |
| Type + Title + Cause | Title | Cause | Provided title | Cause message |
| Type + Template | Type | Template | Type's title | Expanded template |
| Type + Title + Template | Title | Template | Provided title | Expanded template |
| Type + Detail | Type | Detail | Type's title | Provided detail |
| All Components | Title | Detail | Provided title | Provided detail |