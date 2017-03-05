Database Design
--

The approach is to create just one wide table that has a type and holds fields for IFT, ILT as well as IO and AccountSummary.

IFT and ILT will be populated from PushTopics with appropriate logic to handle reversals.
The other two are always summary records, ie Balance forward. They will be loaded using queries via the partner API.

So what does the wide table look like?

One table called Transactions
an Id field called imsid plus a unique index to hold the Salesforce Id (that field is named id). Not sure if we need the Name field as well, possibly not.
A field that specifies the Type. 3 or 4 values depending on what I find out about IO.
Although we might qualify it further for transaction types.
A reference to the reversal of this transaction (the imsid) need to *not* save the SF Id in one of those or we break the unique index.

In terms of fields we must have these (from existing python script queries)

From Investor Loan Transaction (loan__Investor_Loan_Account_Txns__c) :

```
Id
Name
Harmoney_account_number__c
loan__Investor_Loan__r.Id
loan__Investor_Loan__r.Name
loan__Investor_Loan__r.loan__Loan_Status__c
CreatedDate              
loan__Principal_Paid__c  
loan__Interest_Paid__c   
loan__Late_Fees_Paid__c  
loan__Tax__c             
loan__Total_Service_Charge__c              
loan__Charged_Off_Date__c
loan__Charged_Off_Fees__c
loan__Charged_Off_Interest__c              
loan__Charged_Off_Principal__c             
Investor_Txn_Fee__c      
loan__Txn_Code__c        
loan__Waived__c          
loan__Protect_Principal__c 
loan__Rebate_Amount_On_Payoff__c
Net_Amount__c           
Rejected__c
Reversed__c
Reverse_Rejected_Date__c
Management_Fee_Realised__c 
Sales_Commission_Fee_Realised__c           
Protect_Realised__c
```
The `loan__Investor_Loan__r` fields are in Investment Order. They will need to be fetched via some extra formula fields in SF and added to the pushTopic
We also need `reversed` and `rejected` and their date fields.

From Account_Summary__c (AccountSummary)
```
Id
Harmoney_Account_Number__c
Name                      
Outstanding_Principal__c  
loan__Deployed_Funds__c   
loan__Undeployed_Funds__c 
Total_Paid_Off_Amount__c  
Interests_Recived__c      
Late_Fees__c              
Service_Fees__c           
Total_Tax__c              
Total_Charged_Off_Principal__c
Total_Deposit__c          
Total_Withdrawal__c       
Full_investor_statement__c
loan__Investor_Tax_Configuration__r.loan__Tax_Percentage__c # needs a formula field
IRD_Number__c                                      
Account_Summary__c        
```

From Loan__Loan_Account__c (CL Contract)
```
Id
Name 
loan__Charged_Off_Date__c
loan__Protect_Enabled__c 
loan__Loan_Status__c     
loan__Closed_Date__c     
```

from Loan__Investor_Fund_Transaction__c (InvestorFundTransaction)

```
Id
Name
loan_account_c
loan__Transaction_Date__c
loan__Transaction_Type__c
loan__transaction_amount__c
loan__Cleared__c         
loan__Rejected__c
loan__Reversed__c
Reverse_Rejected_Date__c        
```

from loan__Investor_Loan__c (InvestmentOrder)
```
Id
Name
CreatedDate
loan__Loan__c     # Fkey to loan__Loan_Account__c
loan__Investment_Amount__c
HM_Investment_Amount__c   
Protect_Investment_Amount__c
loan__Charged_Off_Principal__c
loan__Charged_Off_Date__c 
loan__Loan_Status__c      
Payment_Protect_Management_Fees__c
Payment_Protect_Sales_Commission_Fees__c
Payment_Protect_Fee__c    
Payment_Protect_Rebated_Amount__c 
HM_Rollup_Outstanding_Principal__c
```

Resulting table:
```
Id
type
InvestmentOrderId
loan__Investor_Loan__r.Name
InvestmentOrderStatus__c
CreatedDate              
loan__Principal_Paid__c  
loan__Interest_Paid__c   
loan__Late_Fees_Paid__c  
loan__Tax__c             
loan__Total_Service_Charge__c              
loan__Charged_Off_Date__c
loan__Charged_Off_Fees__c
loan__Charged_Off_Interest__c              
loan__Charged_Off_Principal__c             
Investor_Txn_Fee__c      
loan__Txn_Code__c        
loan__Waived__c          
loan__Protect_Principal__c 
Management_Fee_Realised__c 
Sales_Commission_Fee_Realised__c           
loan__Rebate_Amount_On_Payoff__c           
Protect_Realised__c
```