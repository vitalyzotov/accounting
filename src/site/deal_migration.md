MySQL 8.0.27 is required to use JSON functions

Create deals for transactions

    insert into deal_ (`DEAL_UID`, `DATE`, `AMOUNT`, `CURRENCY`, `DESCRIPTION`, `COMMENT`, `CATEGORY_ID`)
    select
        uuid() as DEAL_UID,
        (select `DATE` from operation_ where id=tx_.PRIMARY_OP) as `DATE`,
        0 as AMOUNT,
        (select `CURRENCY` from operation_ where id=tx_.PRIMARY_OP) as `CURRENCY`,
        JSON_ARRAY(tx_.PRIMARY_OP, tx_.SECONDARY_OP) as DESCRIPTION,
        JSON_ARRAY() as COMMENT,
        (select `CATEGORY_ID` from operation_ where id=tx_.PRIMARY_OP) as `CATEGORY_ID`
    from
        tx_
    ;

Create deals from qr_operations table 

    insert into deal_ (`DEAL_UID`, `DATE`, `AMOUNT`, `CURRENCY`, `DESCRIPTION`, `COMMENT`, `CATEGORY_ID`)
    select
        uuid() as DEAL_UID,
        (select `DATE` from operation_ where id=JSON_EXTRACT(R1, '$[0]')) as `DATE`,
        (select SUM(op1.AMOUNT * IF(op1.OP_TYPE='+', 1, -1)) from operation_ as op1 where op1.ID member of(R1)) as AMOUNT,
        (select `CURRENCY` from operation_ where id=JSON_EXTRACT(R1, '$[0]')) as `CURRENCY`,
        R1 as DESCRIPTION,
        R2 as COMMENT,
        (select `CATEGORY_ID` from operation_ where id=JSON_EXTRACT(R1, '$[0]')) as `CATEGORY_ID`
    from (
        select R2, JSON_ARRAYAGG(OPERATION_ID) as R1 from (
            select OPERATION_ID, JSON_ARRAYAGG(QR_ID) as R2 from qr_operations group by OPERATION_ID
        ) as S1 group by S1.R2
    ) as S2
    ;

Add deal operations 

    insert into deal_operations (`DEAL_ID`, `OPERATION_ID`)
    select deals.id as `DEAL_ID`, j1.`OPERATION_ID` as `OPERATION_ID` from deal_ deals
    CROSS JOIN JSON_TABLE(
        deals.description,
        '$[*]' COLUMNS(
            `OPERATION_ID` VARCHAR(128) path '$'
        )
    ) as J1;

Add deal receipts

    insert into deal_receipts (`DEAL_ID`, `RECEIPT_ID`)
    SELECT deals.id AS `DEAL_ID`, QR.CHECK_ID as `RECEIPT_ID`
    FROM deal_ deals
    CROSS JOIN JSON_TABLE(
            deals.comment,
            '$[*]' COLUMNS(
            `RECEIPT_ID` VARCHAR(128) PATH '$'
        )
    ) AS J1
    LEFT JOIN qr_code qr ON qr.id=J1.`RECEIPT_ID`
    ;

Fix descriptions and comments

    UPDATE deal_ d
    LEFT JOIN (select min(dop.OPERATION_ID) as OPERATION_ID, dop.DEAL_ID as DEAL_ID FROM deal_operations dop group by deal_id) j1 ON j1.deal_id=d.id
    LEFT JOIN operation_ op ON op.ID=j1.OPERATION_ID
    SET d.description = op.description, d.comment = op.comment
    ;

Add deal purchases

    INSERT INTO deal_purchases (`DEAL_ID`, `PURCHASE_ID`, `PURCHASE_ORDER`)
    SELECT 
        DEAL_ID, 
        PURCHASE_ID, 
        PURCHASE_ORDER 
    FROM (
        SELECT
            PURCHASE_ID,
            CASE DEAL_ID
                WHEN @dealId THEN @rownum := @rownum + 1
                ELSE @rownum := 0
            END AS PURCHASE_ORDER,
            @dealId := DEAL_ID as DEAL_ID
        FROM (
            SELECT min(u3.DEAL_ID) as DEAL_ID, u3.PURCHASE_ID as PURCHASE_ID FROM (
                SELECT
                    d.id AS DEAL_ID,
                    p1.PURCHASE_ID AS PURCHASE_ID
                FROM deal_ AS d
                LEFT JOIN deal_operations AS dop ON dop.DEAL_ID = d.ID
                LEFT JOIN purchase_ AS p1 ON (p1.OPERATION_ID = dop.OPERATION_ID)
                WHERE PURCHASE_ID IS NOT NULL
                UNION
                SELECT
                    d.id AS DEAL_ID,
                    p2.PURCHASE_ID AS PURCHASE_ID
                FROM deal_ AS d
                LEFT JOIN deal_receipts AS dor ON dor.DEAL_ID = d.ID
                LEFT JOIN purchase_ AS p2 ON (p2.CHECK_ID = dor.RECEIPT_ID)
                WHERE PURCHASE_ID IS NOT NULL
                ORDER BY DEAL_ID ASC
            ) as u3
            GROUP BY u3.PURCHASE_ID
        ) AS u1
        JOIN (SELECT @rownum := 0, @dealId := 0) AS r
        ORDER BY DEAL_ID, PURCHASE_ID
    ) AS u2;
