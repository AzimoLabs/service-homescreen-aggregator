# Service Homescreen-aggregator
Based on the part of our homescreen aggregator project we are showing examples for various implementation mechanisms like DynamoDB polymorphic model, rules engine and CDC tests.

## DynamoDB polymorphic model
TODO: Add link to article on medium after publishing.
  
In the article mentioned above, we describe how we implemented a polymorphic model in DynamoDB with dynamic mapping to POJO's.
Additionally, you can see how to configure DynamoDB in spring boot and run integration tests with an embedded version of that storage.   
To see how to store objects in Dynamo and map them to POJO's on retrieval, look at the class [UserStateRepository](src/main/java/com/azimo/quokka/aggregator/dynamodb/UserStateRepository.java) and the corresponding test.
There you will find examples of all the store/delete/retrieve methods. In the test you will also see an example on how to run an embedded DynamoDB.
Config of DynamoDB is very simple in spring boot. You can find it [here](src/main/java/com/azimo/quokka/aggregator/config/aws/DynamoDBConfig.java)

# License

    Copyright (C) 2016 Azimo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.         
