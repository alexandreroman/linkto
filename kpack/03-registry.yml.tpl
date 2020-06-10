---
apiVersion: v1
kind: Secret
metadata:
  name: kpack-registry-secret
  namespace: linkto-kpack
  annotations:
    build.pivotal.io/docker: https://harbor.withtanzu.com
type: kubernetes.io/basic-auth
stringData:
  username: johndoe
  password: changeme
