# readingisgood

A bookstore application example

### Local development

 - run docker-compose up and wait for services to go alive
 - Since my knowledge of Mongodb is limited, could not set up proper credentials.
You may need to adjust credentials and db name either by updating `com.example.readingisgood.config.MongoConfig` or 
creating a user inside the container with `exec -it` and using mongo shell
 - You can either use your IDE or build the application with docker using the dockerfile and go check out `http://localhost:8080/swagger-ui/`
 - An admin user with credentials `admin@admin.com` and password `admin` will automatically be created on application startup
 - You can use `post:/api/public/auth` endpoint to get yourself a token to use later
 - Alternatively, you can create your own customer using `post:/api/public/user`
 - This will create a user with `CUSTOMER` role, who is not capable of listing all the orders and statistics
 - The password will be automatically generated. Check the logs (it would be an email sent to the user in real life) 
 - Got to `http://localhost:8080/swagger-ui/` to see all the endpoints

### Tech Stack

It is a monolithic application written in Java(11+) and Spring boot. Storage is MongoDB and event bus is ElasticMQ (SQS)
Note that the code base is far from ideal. If you find any code duplicates and/or inconsistencies, please bare with me: i need more time to fix those :)

#### Authentication
- Spring security is used. Any endpoint with `/api/public` is publicly available
- Similarly, you need an authenticated user for endpoints under `/api/private`
- Authentication of requests are done using `Jwt` tokens
- Standard authorization header must be used
- You can obtain your jwt using auth api (see swagger-ui)

#### Authorization
- Authorization of users are really simple: a user is either CUSTOMER or MANAGER
- Customers can only access information of their own, managers have access on the entire system

#### User entity
- This entity holds information about users and their credentials
- Password hashing and random generated salts are used
- user email is the primary key (_id)

#### Book entity
- This entity holds information about books and how many of them ara available in the store
- ISBN is the primary key (_id)
- To update the stock, use the stock update endpoint (see swagger-ui)
- NOTE: stock updates are done via an event bus. You will see your changes asynchronously as you refresh the page
- A negative stock update may be rejected if this results in stock being less than zero

#### Order entity
- This entity holds information about orders 
- The most used and complicated one
- When an order is placed, its status is initially `PLACED`
- It than will get processed via the same event bus that updates store
- If the count in the store is sufficient to supply this order, it will be `ACCEPTED`
- else will get `REJECTED`
- You can track orders and monthly stats via given endpoints (see swagger-ui)
- _id of this entity is constructed as follows: `{yyyy_MM}#userId#random-uuid`
- This will help us to utilize the range queries: `id > 2022_03 && id < 2022_04`
- This way we can greatly reduce the data size before aggregation starts and achieve higher performances! (Consider monthly stats)

#### How the Stock is kept consistent?
- Order placed messages and stock update requests are consumed by the same consumer
- Messages are groupped by book ISBN. 
- It means that any stock update operation will be performed by the same consumer thread