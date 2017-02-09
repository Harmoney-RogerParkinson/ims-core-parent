#ims-core-parent

Consists of several projects that make up the ims core JEE server.

 * [ims-core-enterprise](./ims-core-enterprise/README.md) generates the Salesforce code from the enterprise WSDL  
 * [ims-core-partner](./ims-core-partner/README.md) generates the Salesforce code from the partner WSDL  
 * [ims-core-database](./ims-core-database/README.md) contains the database configuration, entity classes and the DAOs. 
 * [ims-core-message-processor](./ims-core-message-processor/README.md) contains the code for reading messages off the bayeux queue put there by Salesforce triggers. The messages are stored in the internal (database) queue.
 * [ims-core-queue-handler](./ims-core-queue-handler/README.md) contains the code for processing entries from the internal (database) queue.
 * [ims-core-server](./ims-core-server/README.md) builds a war file suitable for deployment. 
 
