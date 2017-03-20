#ims-core-server

Generate a deployable war file which responds to web service requests such as:

 * Statement generator
 * Tax Certificate generator
 
It creates a thread to listen for messages from the bayeux queue, as implemented in the [Message Processor](../ims-core-message-processor/README.md).

A second thread is created to read from the internal queue (created by the [Queue Handler](../ims-core-queue-handler/README.md)) and update the core database. In practice the internal queue and the core database will be the same database. This thread managed to avoid conflicts with the above web services, ie if it is not running then it is suspended until the web service has completed, and if it is in progress then the web service must wait for it to complete (and suspend).

url (for the moment) is http://localhost:8080/ims-core-server/
Displays a blank page with a title in the browser window title

WSDL is at [http://localhost:8080/ims-core-server/endpoints/ims-core.wsdl](http://localhost:8080/ims-core-server/endpoints/ims-core.wsdl)
