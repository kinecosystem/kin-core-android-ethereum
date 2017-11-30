# default target does nothing
.DEFAULT_GOAL: default
default: ;

# add truffle and testrpc to $PATH
export PATH := ./truffle/node_modules/.bin:$(PATH)
export PATH := /usr/local/bin:$(PATH)

test:
	./gradlew :sample:assembleDebug
#	./gradlew :kin-sdk-core:connectedAndroidTest
.PHONY: test

prepare-tests: truffle
	truffle/scripts/prepare-tests.sh
.PHONY: test

truffle: testrpc truffle-clean
	truffle/scripts/truffle.sh
.PHONY: truffle

truffle-clean:
	rm -f truffle/token-contract-address
.PHONY: truffle-clean

testrpc: testrpc-run  # alias for testrpc-run
.PHONY: testrpc

testrpc-run: testrpc-kill
	truffle/scripts/testrpc-run.sh
.PHONY: testrpc-run

testrpc-kill:
	truffle/scripts/testrpc-kill.sh
.PHONY: testrpc-kill

clean: truffle-clean testrpc-kill
	rm -f truffle/truffle.log
	rm -f truffle/testrpc.log
