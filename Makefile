API_KEYS_FILE := config.properties
MAKEFLAGS += --no-print-directory

all:
	@[ -f $(API_KEYS_FILE) ] && true || $(MAKE) createfile;
	@mvn clean compile assembly:single

createfile:
	@touch $(API_KEYS_FILE)
	@echo "API_KEY=\nCLIENT_ID=\nCLIENT_SECRET=" >> $(API_KEYS_FILE)