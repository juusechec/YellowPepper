Java 11 because is LTS

Drools for tax logic

API https://api.exchangeratesapi.io/latest?base=USD
is obsolete and isn't working without api key, so I've create one
to use the newest, but the base option of the api is only available for paid users
so, I don't use, instead I've mock a similar


API Design
master-slave
GET /v1/customers/130303/accounts/30303
command pattern (redundant)
POST /v1/customers/130303/retrieve-account
{
    "account": "12345600"
}
{
    "status": "OK",
    "errors": [],
    "account_balance": 70000.00
}
entity-collection
GET /v1/transactions?customers=&accounts=
POST /v1/transactions/
{
    "amount": 5000,
    "currency": "USD",
    "origin_account": "12345600",
    "destination_account": "12345601",
    "description": "Hey dude! I am sending you the money you loaned to me lastweek."
}
{
    "status": "OK",
    "errors": [],
    "tax_collected": 50.00,
    "CAD": 66,928861615
}

