# default target does nothing
.DEFAULT_GOAL: default
default: ;

# add truffle and testrpc to $PATH
export PATH := ./node_modules/.bin:$(PATH)

test:
	./gradlew build -PisRelease=true -Pandroid.buildOnlyTargetAbi=false -Ptravis
.PHONY: test

prepare-tests: truffle
	./scripts/prepare-tests.sh
.PHONY: test

truffle: testrpc truffle-clean
	./scripts/truffle.sh
.PHONY: truffle

truffle-clean:
	rm -f token-contract-address

testrpc: testrpc-run  # alias for testrpc-run
.PHONY: testrpc
testrpc-run: testrpc-kill
	./scripts/testrpc-run.sh
.PHONY: testrpc-run

testrpc-kill:
	./scripts/testrpc-kill.sh
.PHONY: testrpc-kill

clean: truffle-clean testrpc-kill
	rm -f truffle.log
	rm -f testrpc.log