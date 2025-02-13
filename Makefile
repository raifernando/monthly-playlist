API_KEYS_FILE := config.properties
MAKEFLAGS += --no-print-directory

all:
	@[ -f $(API_KEYS_FILE) ] && true || $(MAKE) createfile;
	@mvn clean compile assembly:single

createfile:
	@touch $(API_KEYS_FILE)
	@echo "API_KEY=\nCLIENT_ID=\nCLIENT_SECRET=\nLASTFM_USER=" >> $(API_KEYS_FILE)

signout:
	@java -cp target/monthly-playlist-v1.0-jar-with-dependencies.jar com.raifernando.spotify.SignOut