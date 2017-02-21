
    alter table ACCOUNT 
        drop constraint FK_cwgb4037nvhymt2q9uoscj8td;

    alter table INVESTMENTORDER 
        drop constraint FK_kx6pt0mxwxxd14ek0vy97jghk;

    alter table INVESTMENTORDER 
        drop constraint FK_9s74yay910705y4r0uislfs4l;

    alter table INVESTORFUNDTRANSACTION 
        drop constraint FK_tlffrhi1y7k3nxbdkr4gkn9al;

    alter table INVESTORFUNDTRANSACTION 
        drop constraint FK_mtvywguknwo9r7x5l874yirr0;

    alter table INVESTORLOANTRANSACTION 
        drop constraint FK_2la6cnkt2nt0st8kx40qwp8kk;

    alter table INVESTORLOANTRANSACTION 
        drop constraint FK_o83cqfxe6esr8c7r65r8m0bj5;

    drop table if exists ACCOUNT cascade;

    drop table if exists ACCOUNTSUMMARY cascade;

    drop table if exists INVESTMENTORDER cascade;

    drop table if exists INVESTORFUNDTRANSACTION cascade;

    drop table if exists INVESTORLOANTRANSACTION cascade;

    drop sequence hibernate_sequence;

    create table ACCOUNT (
        HARMONEYACCOUNTNUMBERC bigint not null,
        IRDNUMBERC varchar(255),
        FULLINVESTORSTATEMENTC bit,
        INTERESTSRECIVEDC decimal(8,2),
        LATEFEESC decimal(8,2),
        LOANDEPLOYEDFUNDSC decimal(8,2),
        LOANTAXPERCENTAGEC bigint,
        LOANUNDEPLOYEDFUNDSC decimal(8,2),
        NAME_ varchar(255) not null,
        OUTSTANDINGPRINCIPALC decimal(8,2),
        SERVICEFEESC decimal(8,2),
        TOTALCHARGEDOFFPRINCIPALC decimal(8,2),
        TOTALDEPOSITC decimal(8,2),
        TOTALPAIDOFFAMOUNTC decimal(8,2),
        TOTALTAXC decimal(8,2),
        TOTALWITHDRAWALC decimal(8,2),
        VERSION_ bigint,
        ID bigint,
        primary key (HARMONEYACCOUNTNUMBERC)
    );

    create table ACCOUNTSUMMARY (
        ID bigint not null,
        VERSION_ bigint,
        primary key (ID)
    );

    create table INVESTMENTORDER (
        ID bigint not null,
        HMINVESTMENTAMOUNTC decimal(8,2),
        HMROLLUPOUTSTANDINGPRINCIPALC decimal(8,2),
        CREATEDDATE date,
        LOANCHARGEDOFFDATEC date,
        LOANCHARGEDOFFPRINCIPALC decimal(8,2),
        LOANINVESTMENTAMOUNTC decimal(8,2),
        LOANLOANSTATUSC varchar(255),
        NAME_ varchar(255) not null,
        PAYMENTPROTECTFEEC decimal(8,2),
        PAYMENTPROTECTMANAGEMENTFEESC decimal(8,2),
        PAYMENTPROTECTREBATEDAMOUNTC decimal(8,2),
        PAYMENTPROTECTSALESCOMMISSIO_0 decimal(8,2),
        PROTECTINVESTMENTAMOUNTC decimal(8,2),
        VERSION_ bigint,
        ACCOUNT_INVESTMENTORDER_HARM_0 bigint,
        INVESTMENTORDERS_ACCOUNT_HAR_0 bigint,
        primary key (ID)
    );

    create table INVESTORFUNDTRANSACTION (
        ID bigint not null,
        LOANCLEAREDC bit,
        LOANREJECTEDC bit,
        LOANTRANSACTIONAMOUNTC decimal(8,2),
        LOANTRANSACTIONDATEC date,
        LOANTRANSACTIONTYPEC varchar(255),
        NAME_ varchar(255) not null,
        VERSION_ bigint,
        ACCOUNT_INVESTORFUNDTRANSACT_0 bigint,
        INVESTORFUNDTRANSACTIONS_ACC_0 bigint,
        primary key (ID)
    );

    create table INVESTORLOANTRANSACTION (
        ID bigint not null,
        CREATEDDATE date,
        INVESTORTXNFEEC decimal(8,2),
        LOANCHARGEDOFFDATEC decimal(8,2),
        LOANCHARGEDOFFFEESC decimal(8,2),
        LOANCHARGEDOFFINTERESTC decimal(8,2),
        LOANCHARGEDOFFPRINCIPALC decimal(8,2),
        LOANINTERESTPAIDC decimal(8,2),
        LOANLATEFEESPAIDC decimal(8,2),
        LOANPRINCIPALPAIDC decimal(8,2),
        LOANPROTECTPRINCIPALC decimal(8,2),
        LOANREBATEAMOUNTONPAYOFFC decimal(8,2),
        LOANTAXC decimal(8,2),
        LOANTOTALSERVICECHARGEC decimal(8,2),
        LOANTXNCODEC varchar(255),
        LOANWAIVEDC decimal(8,2),
        MANAGEMENTFEEREALISEDC decimal(8,2),
        NAME_ varchar(255) not null,
        NETAMOUNT decimal(8,2),
        PROTECTREALISEDC decimal(8,2),
        SALESCOMMISSIONFEEREALISEDC decimal(8,2),
        TRANSACTIONTYPE varchar(255),
        VERSION_ bigint,
        INVESTMENTORDER_INVESTORLOAN_0 bigint,
        INVESTORLOANTRANSACTIONS_INV_0 bigint,
        primary key (ID)
    );

    alter table ACCOUNT 
        add constraint FK_cwgb4037nvhymt2q9uoscj8td 
        foreign key (ID) 
        references ACCOUNTSUMMARY;

    alter table INVESTMENTORDER 
        add constraint FK_kx6pt0mxwxxd14ek0vy97jghk 
        foreign key (ACCOUNT_INVESTMENTORDER_HARM_0) 
        references ACCOUNT;

    alter table INVESTMENTORDER 
        add constraint FK_9s74yay910705y4r0uislfs4l 
        foreign key (INVESTMENTORDERS_ACCOUNT_HAR_0) 
        references ACCOUNT;

    alter table INVESTORFUNDTRANSACTION 
        add constraint FK_tlffrhi1y7k3nxbdkr4gkn9al 
        foreign key (ACCOUNT_INVESTORFUNDTRANSACT_0) 
        references ACCOUNT;

    alter table INVESTORFUNDTRANSACTION 
        add constraint FK_mtvywguknwo9r7x5l874yirr0 
        foreign key (INVESTORFUNDTRANSACTIONS_ACC_0) 
        references ACCOUNT;

    alter table INVESTORLOANTRANSACTION 
        add constraint FK_2la6cnkt2nt0st8kx40qwp8kk 
        foreign key (INVESTMENTORDER_INVESTORLOAN_0) 
        references INVESTMENTORDER;

    alter table INVESTORLOANTRANSACTION 
        add constraint FK_o83cqfxe6esr8c7r65r8m0bj5 
        foreign key (INVESTORLOANTRANSACTIONS_INV_0) 
        references INVESTMENTORDER;

    create sequence hibernate_sequence;
