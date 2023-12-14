Welcome to the Trivially Numeric repository!

This repository stores both the client and server components. Both are currently configured for local use,
but you first need to create a self-signed certificate and Java keystore in a /Certificates folder in the root directory.

Once the certificates are created (and added to your JVM's Cacerts), you can run the server and as many clients as you want.

Configuration of the server is handled in the ConfigConstants class, while the client has the ip and port hard-coded.