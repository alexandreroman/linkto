apiVersion: v1
kind: Secret
metadata:
  name: kpack-git-secret
  namespace: linkto-kpack
  annotations:
    build.pivotal.io/git: https://github.com
type: kubernetes.io/basic-auth
stringData:
  username: user
  password: github-access-token
