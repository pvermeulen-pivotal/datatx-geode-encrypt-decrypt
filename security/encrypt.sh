#!/bin/bash
java -cp ./lib/* -Dsecurity-encryption-master=$1 datatx.geode.security.Encryption encrypt $2