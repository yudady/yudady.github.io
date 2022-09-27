How to get a complete plugin list from jenkins

[plugin.txt](https://gist.github.com/noqcks/d2f2156c7ef8955619d45d1fe6daeaa9)


## get the jenkins cli.
curl 'localhost:51100/jnlpJars/jenkins-cli.jar' > jenkins-cli.jar

## call the Jenkins API.
java -jar jenkins-cli.jar -s http://localhost:51100 -auth admin:admin list-plugins > plugins-new.txt

# jenkins-cli
java -jar jenkins-cli.jar -s http://localhost:51100 -auth admin:admin help




java -jar jenkins-cli.jar -s http://localhost:51100 -auth admin:admin help install-plugin


## [Here is an example to get the list of plugins from an existing server:](https://github.com/jenkinsci/docker/blob/master/README.md)
````shell
JENKINS_HOST=username:password@myhost.com:port
curl -sSL "http://$JENKINS_HOST/pluginManager/api/xml?depth=1&xpath=/*/*/shortName|/*/*/version&wrapper=plugins" | perl -pe 's/.*?<shortName>([\w-]+).*?<version>([^<]+)()(<\/\w+>)+/\1 \2\n/g'|sed 's/ /:/'

##

JENKINS_HOST=admin:admin@localhost:51100
curl -sSL "http://$JENKINS_HOST/pluginManager/api/xml?depth=1&xpath=/*/*/shortName|/*/*/version&wrapper=plugins" | perl -pe 's/.*?<shortName>([\w-]+).*?<version>([^<]+)()(<\/\w+>)+/\1 \2\n/g'|sed 's/ /:/'




````


