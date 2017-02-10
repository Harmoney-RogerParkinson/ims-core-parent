#ims-core-queue-handler

The role of the Queue Handler is to process entries in the internal queue table left there by the message processor. The Message Processor does some minimal handling but the bulk of the unpacking is done here. Specific tasks:

 * mapping the updated record structure into the local database tables. This is somewhat configurable to allow for names etc changing in the Salesforce database.
 * creating checkpoint records showing balance-to-date for the current customer.
 
There are flags in the Queue Handler to indicate whether it is suspended or not, and whether it is running or not. The surrounding infrastructure uses these flags to coordinate queue handling with other services. Specifically the Queue Handler is paused when another service needs to run (eg statements), but if the Queue Handler is already in progress then the other service must wait for it to complete (and then pause itself).

--
https://spring.io/guides/gs/messaging-rabbitmq/
https://blog.codecentric.de/en/2011/04/amqp-messaging-with-rabbitmq/
https://www.rabbitmq.com/install-debian.html
https://www.rabbitmq.com/ec2.html
http://docs.spring.io/spring-amqp/reference/htmlsingle/

sudo rabbitmq-server start

sudo rabbitmqctl add_user harmoney harmoney
sudo rabbitmqctl add_vhost harmoney
sudo rabbitmqctl set_permissions -p harmoney harmoney ".*" ".*" ".*"
sudo rabbitmq-plugins enable rabbitmq_management

http://localhost:15672/
guest/guest

