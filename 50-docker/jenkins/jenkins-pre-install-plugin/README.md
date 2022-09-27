How to get a complete plugin list from jenkins

[plugin.txt](https://gist.github.com/noqcks/d2f2156c7ef8955619d45d1fe6daeaa9)


## get the jenkins cli.
curl 'localhost:51100/jnlpJars/jenkins-cli.jar' > jenkins-cli.jar

## call the Jenkins API.
java -jar jenkins-cli.jar -s http://localhost:8080 -auth admin:<password> groovy = < plugins.groovy > plugins.txt