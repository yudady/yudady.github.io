FROM jenkins/jenkins:lts-jdk11
# RUN jenkins-plugin-cli --plugins pipeline-model-definition github-branch-source:1.8



COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt