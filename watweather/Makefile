
run:
	@ echo "Starting server."
	sbt run
dev:
	@ echo "Starting server for development."
	sbt ~reStart

test:
	@ echo "Running tests."
	sbt ~test

curl:
	@ echo "Running sample curl request."
	curl --location --request GET 'http://localhost:8080/watweather?latitude=34.0347&longitude=-78.2158'