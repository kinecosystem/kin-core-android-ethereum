#!/usr/bin/env bash

if [ -f './truffle/testrpc.pid' ]; then
    echo "killing testrpc on port $(cat ./truffle/testrpc.pid)"
    # Don't fail if the process is already killed
    kill -SIGINT $(cat ./truffle/testrpc.pid) || true
    rm -f ./truffle/testrpc.pid
else
    echo "./truffle/testrpc.pid not found, doing nothing"
fi
