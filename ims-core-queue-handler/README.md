#ims-core-queue-handler

The role of the Queue Handler is to process entries in the internal queue table left there by the message processor. The Message Processor does some minimal handling but the bulk of the unpacking is done here. Specific tasks:

 * mapping the updated record structure into the local database tables. This is somewhat configurable to allow for names etc changing in the Salesforce database.
 * creating checkpoint records showing balance-to-date for the current customer.
 
There are flags in the Queue Handler to indicate whether it is suspended or not, and whether it is running or not. The surrounding infrastructure uses these flags to coordinate queue handling with other services. Specifically the Queue Handler is paused when another service needs to run (eg statements), but if the Queue Handler is already in progress then the other service must wait for it to complete (and then pause itself).

--
https://spring.io/guides/gs/messaging-rabbitmq/
https://blog.codecentric.de/en/2011/04/amqp-messaging-with-rabbitmq/
https://www.rabbitmq.com/install-debian.html
https://www.rabbitmq.com/ec2.html
http://docs.spring.io/spring-amqp/reference/htmlsingle/

sudo rabbitmq-server start

sudo rabbitmqctl add_user harmoney harmoney
sudo rabbitmqctl add_vhost harmoney
sudo rabbitmqctl set_permissions -p harmoney harmoney ".*" ".*" ".*"
sudo rabbitmq-plugins enable rabbitmq_management

http://localhost:15672/
guest/guest

## Receivers and Processors

We expect there to be multiple receivers because we expect there to be multiple topics.
Each topic will listen for a specific record and so each of the receivers here will handle a specific record.
Messages arrive as Map<String,Map<String,Object>> where the outer map contains two inner maps. One of these describes what happened to the record (deleted, added etc) and the other describes the field names and new values. Some conversion of the values is needed to manage decimal to BigDecimal, string to date etc. The mapping is handled in the unpacker.

Each receiver has to instantiate the record it is interested in, unpack it, and call the relevant DAO to save it. There is probably extra processing to manage rollups at that point too. The receiver, therefore, must know what the record types are, ie there is a dependency on the ims-core-database project. In practice each receiver delegates the processing to a processing class. So the receivers are, in fact, just methods on one class nd the processing for a specific type of record is handled in the relevant delegate class. There are two of these and they are nearly identical because they just invoke the relevant DAO for their record type. The detailed processing happens in the DAO.

There are two extra processor classes that are invoked from query classes. Each record from the query is passed to the processor where all records are assumed to be a 'create' type and the relevant DAO performs a create. The queries, in turn, are invoked by the scheduler. They do not use the queuing mechanism.

## Balance Forward

Assume we want to generate a balance forward record once a month, though the time period might be configurable (weekly?, daily?).
Each month a scheduled process runs to search the ILT records for the last balance forward entry and everything after that time stamp. We probably want to have it search up to a second time stamp so we can make this more repeatable/testable. Using the totals of both the balance forward entry and the other ILT records we create a balance forward entry.

Possibly if there is already one there we update it with the new totals.
Possibly we run this for a fixed period from the beginning of time so that once it generates one month balance forward it goes on to create/update another for the next month etc.

Same process for IFT, just different fields being totaled. Possibly different period?

The subsequent generation of statements just needs to find the balance forward nearest/before the start date and add up from there. It no longer need to go back to the beginning of time.


