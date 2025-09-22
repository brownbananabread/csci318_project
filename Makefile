client:
	cd services/client-api && mvn spring-boot:run

user:
	cd services/user-api && mvn spring-boot:run

event:
	cd services/event-api && mvn spring-boot:run

activity:
	cd services/activity-api && mvn spring-boot:run

db:
	java -cp ~/.m2/repository/com/h2database/h2/2.2.224/h2-2.2.224.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9092 -baseDir /Users/jacobbrown/workspace/uni/csci318_project/database