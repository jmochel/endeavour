# Operation Outcomes

* Outcome
  * Success
    * Has success value
      * May be supplied
  * Failure
    * Has Failure value
      * Has Cause
      * Has category/title
      * Has detail
  * Partial Success 
    * Has Failure value
      * Has Cause
      * Has category/title
      * Has detail
    * Has success value
      * May be supplied ?

* Events
  * Create
    * Outcome 
      * from Fxn
      * from throwing Fxn
    * Success
      * From value
      * From value supplier ?
    * Failure
      * From cause
      * From type
      * From message template
    * Partial Success
      * From ????
  * Transform
    * Success<X> to Success<Y>
    * Success<X> to Failure
    * Success<X> to Partial Success<X> 
    * Failure to Success?
    * Failure<X> to Failure<Y>
    * Failure to Partial Success<X>
    * Partial Success<X> to Success<X>
    * Partial Success<X> to Failure

