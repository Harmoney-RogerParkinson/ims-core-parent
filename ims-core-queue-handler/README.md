#ims-core-queue-handler

The role of the Queue Handler is to process entries in the internal queue table left there by the message processor. The Message Processor does some minimal handling but the bulk of the unpacking is done here. Specific tasks:

 * mapping the updated record structure into the local database tables. This is somewhat configurable to allow for names etc changing in the Salesorce database.
 * creating checkpoint records showing balance-to-date for the current customer.
 
There are flags in the Queue Handler to indicate whether it is suspended or not, and whether it is running or not. The surrounding infrastructure uses these flags to coordinate queue handling with other services. Specifically the Queue Handler is paused when another service needs to run (eg statements), but if the Queue Handler is already in progress then the other service must wait for it to complete (and then pause itself).

