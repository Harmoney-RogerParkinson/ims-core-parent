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