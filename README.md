#ims-core-parent

Consists of several projects that make up the ims core JEE server.

 * [ims-core-partner](./ims-core-partner/README.md) generates the Salesforce code from the partner WSDL  
 * [ims-core-database](./ims-core-database/README.md) contains the database configuration, entity classes and the DAOs. 
 * [ims-core-message-processor](./ims-core-message-processor/README.md) contains the code for reading messages off the Salesforce pushtopics. The messages are stored in the RabbitMQ queue.
 * [ims-core-queue-handler](./ims-core-queue-handler/README.md) contains the code for processing entries from the RabbitMQ queue queue.
 * [ims-core-server](./ims-core-server/README.md) builds a war file suitable for deployment.
 
 Tricky stuff that needs testing/fixing:

 * move to Harmoney repo
 * in a multi server environment we want just one server to subscribe to the pushTopic, if that server goes down another should pick it up. This is working for the scheduled queries, but not yet added for the subscribers.
 * If they want to adjust/add/remove old transactions we need to run the balance forward process on the relevant accounts/periods (including all periods from that period to the present). This is not catered for yet.
 * InvestorLoanTransactionBalanceForwardTest and InvestorFundTransactionBalanceForwardTest stopped working. Ignored for mow but needs review.

  
 
