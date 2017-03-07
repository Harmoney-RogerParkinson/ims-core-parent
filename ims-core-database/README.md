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

Note that the generated DDL script can be used to create the db, but it probably needs optimisation for production.

----
keep this for now...

	<complexType name="Account">
		<annotation>
			<documentation>Salesforce name: loan__Account__c</documentation>
			<appinfo>
				<annox:annotate>
					<ims:SalesforceName tableName="Account" />
				</annox:annotate>
			</appinfo>
		</annotation>

		<sequence>
			<element name="imsid" type="long">
				<xsd:annotation>
					<xsd:appinfo>
						<hj:id>
							<orm:generated-value strategy="AUTO" />
						</hj:id>
					</xsd:appinfo>
				</xsd:annotation>
			</element>
			<element name="id" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" insertable="false"
								updatable="false" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Id" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="name" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Name" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="version" type="long">
				<annotation>
					<appinfo>
						<hj:version />
					</appinfo>
				</annotation>
			</element>

			<element name="outstandingPrincipal" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Outstanding_Principal__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Outstanding_Principal__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="deployedFunds" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Deployed_Funds__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Deployed_Funds__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="undeployedFunds" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Undeployed_Funds__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Undeployed_Funds__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="totalPaidOffAmount" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Total_Paid_Off_Amount__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Total_Paid_Off_Amount__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="interestsRecived" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Interests_Recived__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Interests_Recived__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="lateFees" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Late_Fees__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Late_Fees__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>

			<element name="serviceFees" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Service_Fees__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Service_Fees__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="totalTax" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Total_Tax__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Total_Tax__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="totalChargedOffPrincipal" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Total_Charged_Off_Principal__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Total_Charged_Off_Principal__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="totalDeposit" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Total_Deposit__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Total_Deposit__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="totalWithdrawal" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Total_Withdrawal__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Total_Withdrawal__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>

			<element name="fullInvestorStatement" type="boolean">
			<!-- Partner gives an error for this
				<annotation>
					<documentation>Salesforce name: Full_investor_statement__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Full_investor_statement__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			 -->
			</element>
			<element name="taxPercentage" type="long">
				<annotation>
					<documentation>Salesforce name: loan__Tax_Percentage__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Investor_Tax_Configuration__r.loan__Tax_Percentage__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element> <!-- loan__Investor_Tax_Configuration__r.loan__Tax_Percentage__c -->
			<element name="IRDNumber" type="string">
				<annotation>
					<documentation>Salesforce name: IRD_Number__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="IRD_Number__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="accountStatus" type="string">
				<annotation>
					<documentation>Salesforce name: Account_Status__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Account_Status__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="investmentOrders" type="tns:InvestmentOrder"
				maxOccurs="unbounded" minOccurs="0" />
			<element name="investorFundTransactions" type="tns:InvestorFundTransaction"
				maxOccurs="unbounded" minOccurs="0" />
		</sequence>
	</complexType>
	<complexType name="AccountSummary">
		<annotation>
			<documentation>Salesforce name: account_summary__c</documentation>
			<appinfo>
				<annox:annotate>
					<ims:SalesforceName tableName="account_summary__c" />
				</annox:annotate>
			</appinfo>
		</annotation>
		<sequence>
			<element name="imsid" type="long">
				<xsd:annotation>
					<xsd:appinfo>
						<hj:id>
							<orm:generated-value strategy="AUTO" />
						</hj:id>
					</xsd:appinfo>
				</xsd:annotation>
			</element>
			<element name="id" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Id" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="name" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Name" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="version" type="long">
				<annotation>
					<appinfo>
						<hj:version />
					</appinfo>
				</annotation>
			</element>
		</sequence>
	</complexType>
	<complexType name="InvestorFundTransaction">
		<annotation>
			<documentation>Salesforce name: loan__Investor_Fund_Transaction__c
			</documentation>
			<appinfo>
				<annox:annotate>
					<ims:SalesforceName tableName="loan__Investor_Fund_Transaction__c" />
				</annox:annotate>
			</appinfo>
		</annotation>
		<sequence>
			<element name="imsid" type="long">
				<xsd:annotation>
					<xsd:appinfo>
						<hj:id>
							<orm:generated-value strategy="AUTO" />
						</hj:id>
					</xsd:appinfo>
				</xsd:annotation>
			</element>
			<element name="id" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Id" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="name" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Name" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="version" type="long">
				<annotation>
					<appinfo>
						<hj:version />
					</appinfo>
				</annotation>
			</element>
			<element name="transactionDate" type="date">
				<annotation>
					<documentation>Salesforce name: loan__Transaction_Date__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Transaction_Date__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="transactionType" type="string">
				<annotation>
					<documentation>Salesforce name: loan__Transaction_Type__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Transaction_Type__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="transactionAmount" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__transaction_amount__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__transaction_amount__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="cleared" type="boolean">
				<annotation>
					<documentation>Salesforce name: loan__Cleared__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Cleared__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="rejected" type="boolean">
				<annotation>
					<documentation>Salesforce name: loan__Rejected__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Rejected__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="account" type="tns:Account" maxOccurs="1"
				minOccurs="1">
				<annotation>
					<documentation>Salesforce name: Account</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Account" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
		</sequence>
	</complexType>

	<complexType name="InvestmentOrder">
		<annotation>
			<documentation>Salesforce name: loan__Investor_Loan__c
			</documentation>
			<appinfo>
				<annox:annotate>
					<ims:SalesforceName tableName="loan__Investor_Loan__c" />
				</annox:annotate>
			</appinfo>
		</annotation>
		<sequence>
			<element name="imsid" type="long">
				<xsd:annotation>
					<xsd:appinfo>
						<hj:id>
							<orm:generated-value strategy="AUTO" />
						</hj:id>
					</xsd:appinfo>
				</xsd:annotation>
			</element>
			<element name="id" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Id" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="name" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Name" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="version" type="long">
				<annotation>
					<appinfo>
						<hj:version />
					</appinfo>
				</annotation>
			</element>
			<element name="loan" type="string" nillable="false">
				<annotation>
					<documentation>Salesforce name: loan__Loan__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Loan__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="account" type="tns:Account" maxOccurs="1"
				minOccurs="1">
				<annotation>
					<documentation>Salesforce name: Account</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Account__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="createdDate" type="date" nillable="false">
				<annotation>
					<documentation>Salesforce name: CreatedDate</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="CreatedDate" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="investmentAmount" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Investment_Amount__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Investment_Amount__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="HMInvestmentAmount" type="tns:currency">
				<annotation>
					<documentation>IF( loan__Loan__r.loan__Protect_Enabled__c == true, Protect_Investment_Amount__c - HP_Rebate_Amount__c, loan__Investment_Amount__c )
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="HM_Investment_Amount__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="protectInvestmentAmount" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Protect_Investment_Amount__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Protect_Investment_Amount__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="chargedOffPrincipal" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Charged_Off_Principal__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Charged_Off_Principal__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="chargedOffDate" type="date">
				<annotation>
					<documentation>Salesforce name: loan__Charged_Off_Date__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Charged_Off_Date__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="loanStatus" type="string">
				<annotation>
					<documentation>text(loan__Loan__r.loan__Loan_Status__c)
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Loan_Status__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="paymentProtectManagementFees" type="tns:currency">
				<annotation>
					<documentation>IF( loan__Loan__r.loan__Protect_Enabled__c = True, loan__Loan__r.HP_Management_Fee__c * loan__Share__c, 0 )
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Payment_Protect_Management_Fees__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="paymentProtectSalesCommissionFees" type="tns:currency">
				<annotation>
					<documentation>IF( loan__Loan__r.loan__Protect_Enabled__c = True, loan__Loan__r.HP_Sale_Commission_Fee__c * loan__Share__c, 0 )
					</documentation>
				</annotation>
			</element>
			<element name="paymentProtectFee" type="tns:currency">
				<annotation>
					<documentation>IF( loan__Loan__r.loan__Protect_Enabled__c = True, loan__Loan__r.loan__Protect_fee_amount__c * loan__Share__c, 0 )
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Payment_Protect_Fee__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="paymentProtectRebatedAmount" type="tns:currency">
				<annotation>
					<documentation>IF( loan__Loan__r.loan__Protect_Enabled__c = True, loan__Loan__r.loan__Protect_Borrower_Rebate__c * loan__Share__c, 0 )
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Payment_Protect_Rebated_Amount__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="HMRollupOutstandingPrincipal" type="tns:currency">
				<annotation>
					<documentation>
					loan__Investment_Amount__c - HM_Rollup_Pricipal_Paid__c - HM_Rollup_Total_Charged_Off_Principal__c - Waived_Principle__c - Payment_Protect_Rebated_Amount__c + HM_Rollup_Recovered_Principal__c
					HM_Rollup_Pricipal_Paid__c = ILT.Principal filter (Transaction Code==PAYMENT APPROVAL) AND (Waived==False)
					HM_Rollup_Total_Charged_Off_Principal__c = ILT.Charged off principal filter Transaction Code==CHARGE OFF
					Waived_Principle__c = ILT.Principal filter (Transaction Code==PAYMENT APPROVAL) AND (Waived==True)
					Payment_Protect_Rebated_Amount__c = IF( loan__Loan__r.loan__Protect_Enabled__c = True, loan__Loan__r.loan__Protect_Borrower_Rebate__c * loan__Share__c, 0 )
					HM_Rollup_Recovered_Principal__c = ILT.Principal filter (Transaction Code==PAYMENT APPROVAL) AND (Waived==False) AND (Recovery Payment==True)
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="HM_Rollup_Outstanding_Principal__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="investorLoanTransactions" type="tns:InvestorLoanTransaction"
				maxOccurs="unbounded" minOccurs="0">
				<annotation>
					<documentation>Salesforce name: InvestorLoanTransactions
					</documentation>
				</annotation>
			</element>
		</sequence>
	</complexType>

	<complexType name="InvestorLoanTransaction">
		<annotation>
			<documentation>Salesforce name: loan__Investor_Loan_Account_Txns__c
			</documentation>
			<appinfo>
				<annox:annotate>
					<ims:SalesforceName tableName="loan__Investor_Loan_Account_Txns__c" />
				</annox:annotate>
				<annox:annotate>
					<annox:annotate annox:class="javax.persistence.NamedQueries">
						<annox:annotate annox:class="javax.persistence.NamedQuery"
							name="InvestorLoanTransaction.imsid" query="select i from InvestorLoanTransaction i where i.imsid=:imsid">
						</annox:annotate>
						<annox:annotate annox:class="javax.persistence.NamedQuery"
							name="InvestorLoanTransaction.id" query="select i from InvestorLoanTransaction i where i.id=:id">
						</annox:annotate>
						<annox:annotate annox:class="javax.persistence.NamedQuery"
							name="InvestorLoanTransaction.all" query="select i from InvestorLoanTransaction i">
						</annox:annotate>
					</annox:annotate>
				</annox:annotate> 
			</appinfo>
		</annotation>
		<sequence>
			<element name="imsid" type="long">
				<xsd:annotation>
					<xsd:appinfo>
						<hj:id>
							<orm:generated-value strategy="AUTO" />
						</hj:id>
					</xsd:appinfo>
				</xsd:annotation>
			</element>
			<element name="id" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Id" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="name" type="string" nillable="false">
				<annotation>
					<appinfo>
						<hj:basic>
							<orm:column nullable="false" unique="true" />
						</hj:basic>
						<annox:annotate>
							<ims:SalesforceName fieldName="Name" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="version" type="long">
				<annotation>
					<appinfo>
						<hj:version />
					</appinfo>
				</annotation>
			</element>
			<element name="txnType" type="tns:ItemType">
				<annotation>
					<documentation>Salesforce name: loan__Txn_Type__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Txn_Type__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="investmentOrderId" type="string">
				<annotation>
					<documentation>Salesforce name: loan__Investor_Loan__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Investor_Loan__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="netAmount" type="tns:currency">
				<annotation>
					<documentation>loan__Txn_Amount__c - loan__Tax__c - loan__Total_Service_Charge__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Net_Amount__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="createdDate" type="date">
				<annotation>
					<documentation>Salesforce name: CreatedDate</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="CreatedDate" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="accountId" type="string">
				<annotation>
					<documentation>Salesforce name: Account_ID__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Account_ID__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="principalPaid" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Principal_Paid__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Principal_Paid__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="interestPaid" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Interest_Paid__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Interest_Paid__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="lateFeesPaid" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Late_Fees_Paid__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Late_Fees_Paid__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="tax" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Tax__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Tax__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="totalServiceCharge" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Total_Service_Charge__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Total_Service_Charge__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="chargedOffDate" type="date">
				<annotation>
					<documentation>Salesforce name: loan__Charged_Off_Date__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Charged_Off_Date__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="chargedOffFees" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Charged_Off_Fees__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Charged_Off_Fees__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="chargedOffInterest" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Charged_Off_Interest__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Charged_Off_Interest__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="chargedOffPrincipal" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Charged_Off_Principal__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Charged_Off_Principal__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="investorTxnFee" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: Investor_Txn_Fee__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Investor_Txn_Fee__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="txnCode" type="tns:TxnCode">
				<annotation>
					<documentation>Salesforce name: loan__Txn_Code__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Txn_Code__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="waived" type="boolean">
				<annotation>
					<documentation>Salesforce name: loan__Waived__c</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Waived__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="protectPrincipal" type="tns:currency">
				<annotation>
					<documentation>(loan__Principal_Paid__c * (loan__Loan_Payment_Transaction__r.loan__Loan_Account__r.loan__Protect_fee_amount__c / (loan__Loan_Payment_Transaction__r.loan__Loan_Account__r.loan__Loan_Amount__c - loan__Loan_Payment_Transaction__r.loan__Loan_Account__r.loan__Protect_fee_amount__c )))
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Protect_Principal__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="managementFeeRealised" type="tns:currency">
				<annotation>
					<documentation>
					IF(OR(loan__Loan_Payment_Transaction__r.Remark__c == 'Full waiver', TEXT(loan__Txn_Code__c) == 'CHARGE OFF') ,loan__Investor_Loan__r.loan__Loan__r.Extra_Protect_Realised__c * loan__Investor_Loan__r.loan__Share_rounded__c, IF(OR(loan__Loan_Payment_Transaction__r.Remark__c == 'Early repayment - with waiver', CONTAINS(loan__Loan_Payment_Transaction__r.Remark__c, 'Rewrite')), (loan__Loan_Payment_Transaction__r.Original_Protect_Realised__c + loan__Investor_Loan__r.loan__Loan__r.Extra_Protect_Realised__c) * loan__Investor_Loan__r.loan__Share_rounded__c, Protect_Realised__c)) * loan__Investor_Loan__r.loan__Loan__r.HP_Management_Fee__c / loan__Investor_Loan__r.loan__Loan__r.loan__Protect_fee_amount__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Management_Fee_Realised__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="salesCommissionFeeRealised" type="tns:currency">
				<annotation>
					<documentation>
					IF(loan__Loan_Payment_Transaction__r.Remark__c == 'Early repayment - no waiver', (loan__Investor_Loan__r.loan__Loan__r.loan__Protect_fee_amount__c - loan__Investor_Loan__r.loan__Loan__r.Protect_Realised_Active__c + loan__Loan_Payment_Transaction__r.Original_Protect_Realised__c) * loan__Investor_Loan__r.loan__Share_rounded__c, IF(CONTAINS(loan__Loan_Payment_Transaction__r.Remark__c, 'Rewrite'), (loan__Loan_Payment_Transaction__r.Original_Protect_Realised__c + loan__Investor_Loan__r.loan__Loan__r.Extra_Protect_Realised__c) * loan__Investor_Loan__r.loan__Share_rounded__c, IF(TEXT(loan__Txn_Code__c) == 'CHARGE OFF', loan__Investor_Loan__r.loan__Loan__r.Protect_Charge_Off__c * loan__Investor_Loan__r.loan__Share_rounded__c, Protect_Realised__c))) * loan__Investor_Loan__r.loan__Loan__r.HP_Sale_Commission_Fee__c / loan__Investor_Loan__r.loan__Loan__r.loan__Protect_fee_amount__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Sales_Commission_Fee_Realised__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="rebateAmountOnPayoff" type="tns:currency">
				<annotation>
					<documentation>Salesforce name: loan__Rebate_Amount_On_Payoff__c
					</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="loan__Rebate_Amount_On_Payoff__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="protectRealised" type="tns:currency">
				<annotation>
					<documentation>IF(loan__Investor_Loan__r.loan__Loan__r.loan__Protect_Enabled__c == TRUE, IF(TEXT(loan__Txn_Code__c) == 'CHARGE OFF', 0, loan__Loan_Payment_Transaction__r.Protect_Realised__c * loan__Investor_Loan__r.loan__Share_rounded__c), 0)
</documentation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Protect_Realised__c" />
							<ims:Negateable/>
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="rejected" type="boolean">
				<annotation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Rejected__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="reversed" type="boolean">
				<annotation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Reversed__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
			<element name="reversedOrRejectedDate" type="date">
				<annotation>
					<appinfo>
						<annox:annotate>
							<ims:SalesforceName fieldName="Reverse_Rejected_Date__c" />
						</annox:annotate>
					</appinfo>
				</annotation>
			</element>
		</sequence>
	</complexType>
