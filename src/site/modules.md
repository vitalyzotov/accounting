```mermaid
flowchart TD
    accounting --> accounting-module
    accounting --> cashreceipt-module
    
    cashreceipt-module --> cashreceipt-model
    cashreceipt-module --> purchases-model
   

   accounting-module --> cashreceipt-model
    accounting-module --> purchases-model
    accounting-module --> banking-model
    accounting-module --> accounting-model
    
    loan-model --> calendar-model;

    accounting-model --> loan-model;
    accounting-model --> banking-model;
    accounting-model --> calendar-model;
    accounting-model --> cashreceipt-model;

```

```plantuml
@startuml
    artifact "accounting"
    artifact accounting_module as "accounting-module"
    artifact cashreceipt_module as "cashreceipt-module"
    artifact cashreceipt_model as "cashreceipt-model"
    artifact purchases_model as "purchases-model"
    artifact banking_model as "banking-model"
    artifact accounting_model as "accounting-model"
    artifact loan_model as "loan-model"
    artifact calendar_model as "calendar-model"
    
    accounting -- accounting_module
    accounting -- cashreceipt_module
    
    cashreceipt_module -- cashreceipt_model
    cashreceipt_module -- purchases_model
    
    accounting_module -- cashreceipt_model
    accounting_module -- purchases_model
    accounting_module -- banking_model
    accounting_module -- accounting_model
    
    loan_model -- calendar_model
    
    accounting_model -- loan_model
    accounting_model -- banking_model
    accounting_model -- calendar_model
    accounting_model -- cashreceipt_model
@enduml
```

Shared dependencies are not included: ddd-shared, money-money, gov-model
