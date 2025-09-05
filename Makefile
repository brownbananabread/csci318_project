client-api:
	cd services/client-api && mvn spring-boot:run -Dspring-boot.run.fork=false

user-api:
	cd services/user-api && mvn spring-boot:run -Dspring-boot.run.fork=false

db:
	java -cp ~/.m2/repository/com/h2database/h2/2.2.224/h2-2.2.224.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9092 -baseDir /Users/jacobbrown/workspace/uni/csci318_project/database