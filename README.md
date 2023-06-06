online-store

Used frameworks:
- H2 as data storage
- Spring Boot
- JPA as data access

Run all tests: ./gradlew clean test

Note: 2 tests fail since missing requirements regarding inventory and removing deals
- StoreUserIntegrationTest > testUserCanNotBuyQuantityThatExceedsInventory() FAILED
- StoreUserIntegrationTest > testRemovedDealShouldNotBeAppliedToReceipt() FAILED

Build the application: 
```
./gradlew clean build
```

Build the application without tests: 
```
./gradlew clean build -x test
```

Run the application:
```
cd ./build/libs 
java -jar online-store-1.0-SNAPSHOT.jar
```

Application is exposed on localhost:8080 and can be tested using curl. Below example commands

Add user John Doe

``` 
curl -X POST -H "Content-Type: application/json" -d '{"name":"John Doe"}' localhost:8080/admin/user/add
```
Response:
```json
{"id":1,"name":"John Doe","basket":{"id":1,"items":null}}
```

Add user Andy Ma 
```
curl -X POST -H "Content-Type: application/json" -d '{"name":"Andy Ma"}' localhost:8080/admin/user/add
```
Response:
```json
{"id":2,"name":"Andy Ma","basket":{"id":2,"items":null}}
```

Get all users
```
curl -X GET -H "Content-Type: application/json" localhost:8080/admin/user/all
```
Response:
```json
[{"id":1,"name":"John Doe","basket":{"id":1,"items":[]}},{"id":2,"name":"Andy Ma","basket":{"id":2,"items":[]}}]
```

Removing user with id=1
```
curl -X DELETE -H "Content-Type: application/json" localhost:8080/admin/user/remove/1
```
Response:
no response

Get again all users:
```
curl -X GET -H "Content-Type: application/json" localhost:8080/admin/user/all
```

Response:
```json
[{"id":2,"name":"Andy Ma","basket":{"id":2,"items":[]}}]
```