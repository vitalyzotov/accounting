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

Shared dependencies are not included: ddd-shared, money-money, gov-model
