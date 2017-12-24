Job INFRA_Job_Ping:
features:addurl  mvn:http://admin:admin123@server:8081/nexus/content/repositories/releases\!ch.mno.dummyapp.job.G_Technique/INFRA_Job_Ping-feature/666.0/xml
features:install INFRA_Job_Ping-feature    KO -> installe mais run le job ?



features:listurl | grep frastr
  true    mvn:http://admin:admin123@server:8081/nexus/content/repositories/snapshots!ch.mno.dummyapp.service.G_Technique/WS_Infrastructure-feature/1.0.0-SNAPSHOT/xml
  true    mvn:http://admin:admin123@server:8081/nexus/content/repositories/releases!ch.mno.dummyapp.service.G_Technique/WS_Infrastructure-feature/15.33.0/xml


features:removeurl ...( ! -> \! )

INSTALL
features:addurl  mvn:http://admin:admin123@server:8081/nexus/content/repositories/releases\!ch.mno.dummyapp.service.G_Technique/WS_Infrastructure-feature/15.33.0/xml
-> features:list = uninstalled
features:install WS_Infrastructure-feature

LIST
features:list
features:listurl
osgi:list

TODO: pas dans le TAC

UNINSTALL
features:uninstall  WS_Infrastructure-feature/15.33.0
features:removeurl mvn:http://admin:admin123@server:8081/nexus/content/repositories/releases!ch.mno.dummyapp.service.G_Technique/WS_Infrastructure-feature/15.33.0/xml
osgi:uninstall WS_Infrastructure

