#!/usr/bin/env bash

# export account address environment variables
# see this file for available variables
source ./truffle/scripts/testrpc-accounts.sh

# create variables
configFile="./kin-sdk-core/src/androidTest/assets/testConfig.json"

# export token contract address environment variable
export TOKEN_CONTRACT_ADDRESS=$(cat ./truffle/token-contract-address)

echo "Set Contract Address ${TOKEN_CONTRACT_ADDRESS}"
echo ""
# write contract address to testConfig.json, will be read by androidTest
printf '"token_contract_address":"%s"\n' "${TOKEN_CONTRACT_ADDRESS}" >> ${configFile}

# write closing bracket to testConfig.json
printf '}' >> ${configFile}
