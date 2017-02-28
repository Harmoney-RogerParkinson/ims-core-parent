#ims-core-database

Contains configuration and connection code for the local database as well as any DAO classes etc.
In practice the database is a postgres db running on AWS. The default setting assumes a database on localhost for testing ie jdbc:postgresql:localhost:imscore

##Tables

----
|Salesforce| Postgres |
|----|----|
|loan__Account__c | Account|
|account_summary__c | AccountSummary|
|loan__Investor_Loan__c | InvestmentOrder|
|loan__Investor_Fund_Transaction__c | InvestorFundTransaction|
|loan__Investor_Loan_Account_Txns__c | InvestorLoanTransaction|

Database properties (including defaults)

database.dialect=org.hibernate.dialect.PostgreSQLDialect
database.datasource.class=org.postgresql.Driver
database.url=jdbc:postgresql:imscore
database.user=postgres
database.password=postgres
database.hbm2ddl.auto=

Override these with environment variables as necessary.
The database.hbm2ddl.auto is for auto creation. Default does nothing but change to create or create-drop for automatic db creation/deletion.