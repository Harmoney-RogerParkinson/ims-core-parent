#ims-core-parent

Consists of several projects that make up the ims core JEE server.

 * [ims-core-partner](./ims-core-partner/README.md) generates the Salesforce code from the partner WSDL  
 * [ims-core-database](./ims-core-database/README.md) contains the database configuration, entity classes and the DAOs. 
 * [ims-core-message-processor](./ims-core-message-processor/README.md) contains the code for reading messages off the Salesforce pushtopics. The messages are stored in the RabbitMQ queue.
 * [ims-core-queue-handler](./ims-core-queue-handler/README.md) contains the code for processing entries from the RabbitMQ queue queue.
 * [ims-core-server](./ims-core-server/README.md) builds a war file suitable for deployment.
 
 Tricky stuff that needs testing/fixing:

 * Scheduled lock
 * W/S API (needs more testing, ie with actual data)
 * db indexes (need to index all records by API and probably Account by Harmoney customer number)
 * default.properties in ims-server project needs verifying/rationalising
 * move to Harmoney repo

  
 
