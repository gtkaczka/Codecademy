# Coffee Orders Project

### Overview
This is a demo project used to explore Spring Boot features.

### Testing
You can use cURL to manually test your API endpoints.

Here are some example cURL requests and responses.

#### Add coffee order
```shell
curl --request POST "http://localhost:4000/orders" \
--header "Content-Type: application/json" \
--data "{
    \"blend\": \"French roast\",
    \"size\": \"Venti\",
    \"customer\": \"Joe\"
}"
```


#### Get coffee orders
```shell
curl --request GET "http://localhost:4000/orders"
```

#### Get coffee order 1
```shell
curl --request GET "http://localhost:4000/orders/1"
```

#### Fulfill coffee order 1
```shell
curl --request POST "http://localhost:4000/orders/1"
```