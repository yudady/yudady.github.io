
# repo


## helm
https://helm.sh/docs/topics/registries/

helm package .

helm push test-chart-0.1.0.tgz oci://localhost:5000/helm-charts


helm registry login -u myuser localhost:5000


Implementors - OCI Registry As Storage
https://oras.land/implementors/
