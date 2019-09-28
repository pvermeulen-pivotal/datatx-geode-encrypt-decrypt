#!/bin/bash
PW=`./decrypt.sh file:keyfile cyRPdrn7dcOIZR/Vcr4y/A==`
echo gfsh -e "connect --locator=localhost[10334] --user=user --password=$PW"