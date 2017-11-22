#!/usr/bin/env bash

# prepare testrpc accounts parameter string e.g. --account="0x11c..,1000" --account="0xc5d...,1000" ....
source ./truffle/scripts/testrpc-accounts.sh

# create variables
configFile="./kin-sdk-core/src/androidTest/assets/testConfig.json"
accounts=""

# clear config file
> $configFile

# copy output to testConfig.json, will be read by androidTest
printf '{ \n  "accounts": [' >> ${configFile}
comma=','
for i in ${!account_array[@]}; do
    accounts+=$(printf '%saccount=%s,%s' "--" "${account_array[i]}" "${balance}")

    printf '{\n "private_key":"%s"\n }%s\n' "${account_array[i]}" "${comma}" >> ${configFile}

    if [ $i -lt 8 ]; then
        comma=','
    else
        comma=''
    fi

    if [ $i -lt 10 ]; then
        accounts+=" "
    fi
done
# accounts closing bracket, contract address is added in prepare-tests.sh
printf '], \n' >> ${configFile}

if (nc -z localhost 8545); then
    echo "Using existing testrpc instance on port $(ps -fade | grep -e 'node.*testrpc' | head -n 1 | awk '{print $2}')"
else
    echo -n "Starting testrpc instance on port ${port} "
    testrpc ${accounts} -u 0 -u 1 -p "${port}" > ./truffle/testrpc.log 2>&1 & echo $! > ./truffle/testrpc.pid
    echo $(cat ./truffle/testrpc.pid)
fi
