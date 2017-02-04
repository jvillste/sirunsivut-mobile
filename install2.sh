#!/bin/bash

cd sirunsivut/sites/all/themes/siru/javascript/compiled; zip -r mobile.zip .
scp mobile.zip root@95.85.52.76:mobile.zip
ssh root@95.85.52.76
