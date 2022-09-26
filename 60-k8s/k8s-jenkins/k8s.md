# jenkins


```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jenkins-depl
spec:
  replicas: 1
  selector:
    matchLabels:
      app: jenkins
  template:
    metadata:
      labels:
        app: jenkins
    spec:
      containers:
        - name: jenkins
          image: jenkins/jenkins:lts-alpine
          imagePullPolicy: IfNotPresent
          #          volumeMounts:
          #            - name: jenkins-volume
          #              mountPath: /mnt/d/var/jenkins_home
          #            - name: jenkins-localtime
          #              mountPath: /etc/localtime
          env:
            - name: JAVA_OPTS
              value: '-Xms256m -Xmx1024m -Duser.timezone=Asia/Shanghai'
            - name: TRY_UPGRADE_IF_NO_MARKER
              value: 'true'
          ports:
            - name: http
              containerPort: 8080
            - name: agent
              containerPort: 50000
          resources:
            requests:
              cpu: 1000m
              memory: 1Gi
            limits:
              cpu: 1200m
              memory: 2Gi
#      restartPolicy: Always
#      serviceAccountName: jenkins-admin

#      volumes:
#        - name: jenkins-localtime
#          hostPath:
#            path: /etc/localtime
#        - name: jenkins-volume
#          hostPath:
#            path: /home/jenkins/jenkins_home
---
apiVersion: v1
kind: Service
metadata:
  labels:
    app: jenkins-svc
  name: jenkins-svc
spec:
  selector:
    app: jenkins
  ports:
    - name: http
      protocol: TCP
      port: 8080
      targetPort: 8080
    - port: 50000
      targetPort: 50000
      name: agent
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: jenkins-ing
spec:
  ingressClassName: nginx
  rules:
    - host: "yudady.gq"
      http:
        paths:
          - pathType: Prefix
            path: "/"
            backend:
              service:
                name: jenkins-svc
                port:
                  number: 8080
#---
#apiVersion: v1
#kind: ServiceAccount
#metadata:
#  labels:
#    k8s-app: jenkins
#  name: jenkins-admin
#---
#apiVersion: rbac.authorization.k8s.io/v1
#kind: ClusterRole
#metadata:
#  name: jenkins-rbac
#rules:
#  - apiGroups: ["","extensions","app"]
#    resources: ["pods","pods/exec","deployments","replicasets"]
#    verbs: ["get","list","watch","create","update","patch","delete"]
#---
#apiVersion: rbac.authorization.k8s.io/v1
## This role binding allows "jane" to read pods in the "default" namespace.
## You need to already have a Role named "pod-reader" in that namespace.
#kind: RoleBinding
#metadata:
#  name: jenkins-admin
##  labels:
##    k8s-app: jenkins
#subjects:
#  - kind: ServiceAccount
#    name: jenkins-admin
#roleRef:
#  kind: ClusterRole
#  name: jenkins-rbac
#  apiGroup: rbac.authorization.k8s.io


```