data-stream-processing
======================

Consumes, enriches, and passes on Wikipedia edits

Apologies - I didn't have to time to get it to stream to a server. It just posts each message separately.

Build with  'mvn clean compile assembly:single -DskipTests' then run the resulting jar file



Can be tested by running fakeServer.js as a node application and then running th full app against localhost:8000