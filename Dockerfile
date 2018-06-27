Dockerfile

FROM ubuntu

MAINTAINER Wilson Mar<wilsonmar@gmail.com>

RUN apt-get update && apt-get install -y nano && apt-get clean

EXPOSE 8484

ENTRYPOINT "put your code here" && /bin/bash
#exemple ENTRYPOINT service nginx start && service ssh start && /bin/bash "use && to separate your code"

WORKDIR TS3MusicBot
CMD "./TS3MusicBot_runscript.sh -account ... -port 8484 -webif-bind-ip 0.0.0.0"

after save your code on /root, create a new image docker using

docker build -t lucassimao/teste:0.1 /root/
start new container

docker run -it lucassimao/teste:0.1 /bin/bash