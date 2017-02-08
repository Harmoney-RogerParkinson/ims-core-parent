#ims-core-enterprise

Essentially a repository for the force-wsc and enterprise.jar files.
The force-wsc is actually built from a maven project so we need to decide where that source should be kept.
The Enterprise jar is generated from the WSDL (also included here) and ought to be done from maven.

The command line is:
java -cp force-wsc-39.0.1-uber.jar  com.sforce.ws.tools.wsdlc enterprise.wsdl  ./enterprise.jar

So it actually used the force-wsc to do the generation.