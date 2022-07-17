A skeleton application for OpenRNDR with full gradle build.

Clone this and change it as needed.

# jpackage

````shell
./gradlew :app:jpackage
````

This will create a jpackage executable (tested in windows no less!) in app/build/jpackage

Note, the logging isn't working correctly for FULL, change to SIMPLE if you're deploying the application as exe.
Haven't worked out why, there seems to be some missing classes or something wierd is happening in the log4j library
when running the exe. At SIMPLE level this doesn't happen.

For running in IntelliJ, just click the "run" button on the main executable.