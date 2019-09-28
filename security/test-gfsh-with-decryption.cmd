for /f "tokens=*" %%a in ('decrypt.cmd file:keyfile cyRPdrn7dcOIZR/Vcr4y/A==') do set PW=%%a
gfsh -e "connect --locator=localhost[10334] --user=user --password=%PW%"