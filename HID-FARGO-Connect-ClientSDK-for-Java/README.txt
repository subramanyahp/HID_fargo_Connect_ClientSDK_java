# HID FARGO Connect Client SDK for Java

Welcome to the Java SDK for HID FARGO Connect.  

## Getting Started

A sample console has been provided in the source directory of the SDK distribution
that demonstrates the usage of the Java SDK. A gradle build script has also been
provided with the application to enable the application to be easily built and run
from the command line.

The gradle build script, build.gradle, declares the third-party jar files needed
to build and run the sample application. These dependencies are automatically
downloaded by the gradle build as necessary. The Java SDK library and associated
javadoc jar are in the "libs" folder.

Please follow the instructions below to get started with the sample project.

### Prerequisites
The following prerequisites are needed to get the Sample Project running.

1) HID FARGO Connect API Key
2) HID FARGO Connect Integration Services URL
3) HID FARGO Connect Client Certificates and passwords
4) A Java 8 JDK with java.exe available on the system path
5) A text editor or a Java IDE such as Intellij IDEA, Eclipse or Netbeans


### Installation

1) Install a Java 8 JDK if one is not already installed.
2) Add the JDK bin directory to the path so java.exe can be executed from the command line
3) Unzip the HID FARGO Connect Client SDK for Java into a work directory
3) Copy the HID FARGO Connect Client Certificates into the root or the work directory
4) Open the SampleApplication.java source file in a text editor or Java IDE
5) Edit the sample application and update the settings in the following method
   with the provided files and values.

  private static CardServicesClient configureClient() {

    CardServicesClientConfig clientConfig = new CardServicesClientConfig();

    /*
     * FARGO Connect server base URI (e.g. "https://my.server.com:18443"). A
     * default port number of 18443 is used if none is specified in the URI.
     */
    clientConfig.setServerBaseUri("");

    /*
     * Server Api key assigned to the client. The Api key is configured in the
     * FARGO Connect Portal.
     */
    clientConfig.setApiKey("");

    /*
     * Client SSL certificate used for server authentication. The certificate
     * must be in PKCS#12 format and may be configured using a certificate file
     * or alternatively as a stream using the setAuthCertStream method.
     */
    clientConfig.setAuthCertFile("");

    /*
     * Password for the client authentication certificate
     */
    clientConfig.setAuthCertPassword("");

    /*
     * Client data encryption certificate. The certificate must be in PKCS#12
     * format and may be configured using a certificate file or alternatively
     * as a stream using the setEncryptionCertStream method.
     */
    clientConfig.setEncryptionCertFile("");

    /*
     * Password for the data encryption certificate
     */
    clientConfig.setEncryptionCertPassword("");

    /*
     * Client Certificate Authority (CA) trust chain certificate(s). The trust chain
     * certificates must be in PEM format and may be configured using a certificate
     * file or alternatively as a stream using the setCaCertStream method.
     */
    clientConfig.setCaCertFile("");

    /*
     * Create the SDK client
     */
    return new CardServicesClient(clientConfig);
  }

6) Open a command prompt in the root of the work directory and execute gradle to build
   and run the application with the following command. On Linux systems the gradlew
   shell script may need to have the execute bit set.

   gradlew clean run
  
Note: Please see the comments in the sample application code and developer
documentation found in the "docs" directory of the project.
